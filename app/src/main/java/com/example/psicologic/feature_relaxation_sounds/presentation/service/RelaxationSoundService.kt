// RelaxationSoundService.kt
package com.example.psicologic.feature_relaxation_sounds.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.example.psicologic.R
import com.example.psicologic.feature_relaxation_sounds.data.local.RelaxationDatabase
import com.example.psicologic.feature_relaxation_sounds.data.repository.RelaxationRepositoryImpl
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.SaveRelaxationSessionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class RelaxationSoundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val binder = RelaxationBinder()
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var repository: RelaxationRepositoryImpl
    private lateinit var saveSessionUseCase: SaveRelaxationSessionUseCase

    private var currentSound: RelaxationSound? = null
    private var startTime: Date? = null

    // Flujos de estado observables
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentProgress = MutableStateFlow(0)
    val currentProgress: StateFlow<Int> = _currentProgress.asStateFlow()

    // Nuevo flujo para el ID del sonido actual
    private val _currentSoundId = MutableStateFlow<Int?>(null)
    val currentSoundId: StateFlow<Int?> = _currentSoundId.asStateFlow()

    override fun onCreate() {
        super.onCreate()

        // Inicializar el WakeLock con configuración optimizada
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "LumiMood:RelaxationServiceWakeLock"
        ).apply {
            setReferenceCounted(false) // Importante para evitar fugas de memoria
        }

        // Inicializar MediaSession
        mediaSession = MediaSessionCompat(this, "LumiMood")
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                currentSound?.id?.let { soundId ->
                    serviceScope.launch {
                        playSound(soundId)
                    }
                }
            }

            override fun onPause() {
                super.onPause()
                pauseSound()
            }

            override fun onStop() {
                super.onStop()
                stopSound()
            }
        })
        mediaSession.isActive = true

        // Inicializar el repositorio
        val dao = RelaxationDatabase.getDatabase(applicationContext).relaxationSoundDao()
        repository = RelaxationRepositoryImpl(dao)
        saveSessionUseCase = SaveRelaxationSessionUseCase(repository)

        // Crear canal de notificación
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Muestra una notificación básica inmediatamente para mantener el servicio activo
        startForeground(NOTIFICATION_ID, createBasicNotification().build())

        // Maneja MediaButtonReceiver
        MediaButtonReceiver.handleIntent(mediaSession, intent)

        when(intent?.action) {
            ACTION_PLAY -> {
                val soundId = intent.getIntExtra(EXTRA_SOUND_ID, -1)
                if (soundId != -1) {
                    // Adquirir WakeLock si no está activo
                    if (!wakeLock.isHeld) {
                        wakeLock.acquire(TimeUnit.HOURS.toMillis(8)) // 8 horas máximo
                    }

                    serviceScope.launch {
                        playSound(soundId)
                    }
                }
            }
            ACTION_PAUSE -> pauseSound()
            ACTION_RESUME -> resumeSound()
            ACTION_STOP -> stopSound()
        }

        // Usar START_STICKY para que Android intente reiniciar el servicio si se mata
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // El servicio seguirá ejecutándose incluso si la tarea se elimina
        // No necesitamos detener el servicio aquí
    }

    override fun onDestroy() {
        super.onDestroy()

        if (wakeLock.isHeld) {
            try {
                wakeLock.release()
            } catch (e: Exception) {
                Log.e("RelaxationService", "Error al liberar WakeLock", e)
            }
        }

        mediaSession.isActive = false
        mediaSession.release()

        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()

        // Guardar la sesión si se está reproduciendo
        currentSound?.let { sound ->
            serviceScope.launch {
                startTime?.let { start ->
                    val endTime = Date()
                    val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(
                        endTime.time - start.time
                    ).toInt()

                    saveSessionUseCase(
                        soundId = sound.id,
                        durationMinutes = durationMinutes,
                        completed = false
                    )
                }
            }
        }
    }

    inner class RelaxationBinder : Binder() {
        fun getService(): RelaxationSoundService = this@RelaxationSoundService
    }

    private suspend fun playSound(soundId: Int) {
        // Detener cualquier reproducción actual
        stopSoundInternal()

        // Obtener el sonido desde la base de datos
        val sound = repository.getSoundById(soundId)
        if (sound != null) {
            currentSound = sound
            _currentSoundId.value = sound.id
            startTime = Date()

            try {
                // Obtener el ID del recurso de audio
                val resourceId = resources.getIdentifier(
                    sound.resourcePath,
                    "raw",
                    packageName
                )

                if (resourceId == 0) {
                    Log.e("RelaxationService", "No se encontró el recurso de audio: ${sound.resourcePath}")
                    return
                }

                Log.d("RelaxationService", "Reproduciendo sonido: ${sound.name} con ID de recurso: $resourceId")

                mediaPlayer = MediaPlayer.create(this, resourceId).apply {
                    setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    isLooping = true // Repetir el sonido indefinidamente

                    setOnCompletionListener {
                        serviceScope.launch {
                            // Guardar la sesión como completada
                            saveSessionUseCase(
                                soundId = sound.id,
                                durationMinutes = TimeUnit.SECONDS.toMinutes(sound.durationSeconds.toLong()).toInt(),
                                completed = true
                            )
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e("RelaxationService", "Error en MediaPlayer: what=$what, extra=$extra")
                        stopSelf()
                        true
                    }

                    start()
                }

                _isPlaying.value = true

                // Actualizar MediaSession
                mediaSession.setMetadata(
                    MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, sound.name)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "LumiMood")
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, sound.durationSeconds * 1000L)
                        .build()
                )

                // Iniciar un temporizador para actualizar el progreso
                serviceScope.launch {
                    while (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                        val progress = mediaPlayer?.currentPosition ?: 0
                        _currentProgress.value = progress
                        delay(1000) // Actualizar cada segundo
                    }
                }

                // Mostrar la notificación del servicio en primer plano
                startForeground(NOTIFICATION_ID, createNotification(sound).build())

            } catch (e: Exception) {
                Log.e("RelaxationService", "Error al reproducir el sonido", e)
                stopSelf()
            }
        } else {
            Log.e("RelaxationService", "No se encontró el sonido con ID: $soundId")
            stopSelf()
        }
    }

    private fun pauseSound() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        updateNotification()
    }

    private fun resumeSound() {
        mediaPlayer?.start()

        // Asegurarse de que el WakeLock está activo
        if (!wakeLock.isHeld) {
            wakeLock.acquire(TimeUnit.HOURS.toMillis(8))
        }

        _isPlaying.value = true
        updateNotification()
    }

    // Método interno para detener el sonido sin finalizar el servicio
    private fun stopSoundInternal() {
        currentSound?.let { sound ->
            serviceScope.launch {
                // Si hay un sonido reproduciéndose, guardar la sesión
                startTime?.let { start ->
                    val endTime = Date()
                    val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(
                        endTime.time - start.time
                    ).toInt()

                    saveSessionUseCase(
                        soundId = sound.id,
                        durationMinutes = durationMinutes,
                        completed = false
                    )
                }
            }
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentSoundId.value = null
        currentSound = null
        startTime = null
    }

    // Método público para detener y finalizar el servicio
    private fun stopSound() {
        stopSoundInternal()
        stopForeground(true)
        stopSelf()
    }

    private fun createBasicNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("LumiMood")
            .setContentText("Preparando música relajante...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LumiMood Relajación",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Sonidos de relajación para LumiMood"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(sound: RelaxationSound): NotificationCompat.Builder {
        // Intent para abrir la app cuando se toca la notificación
        val contentIntent = Intent(this, Class.forName("com.example.psicologic.MainActivity")).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingContentIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intents para los botones de control
        val pauseResumeIntent = Intent(this, RelaxationSoundService::class.java).apply {
            action = if (_isPlaying.value) ACTION_PAUSE else ACTION_RESUME
        }
        val pendingPauseResumeIntent = PendingIntent.getService(
            this,
            1,
            pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, RelaxationSoundService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Crear la notificación
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("LumiMood - ${sound.name}")
            .setContentText("Reproduciendo música relajante")
            .setContentIntent(pendingContentIntent)
            .addAction(
                if (_isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
                if (_isPlaying.value) "Pausar" else "Reproducir",
                pendingPauseResumeIntent
            )
            .addAction(
                R.drawable.ic_stop,
                "Detener",
                pendingStopIntent
            )
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Mostrar en pantalla de bloqueo
    }

    private fun updateNotification() {
        currentSound?.let { sound ->
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification(sound).build())
        }
    }

    companion object {
        const val CHANNEL_ID = "relaxation_sounds_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_PLAY = "com.example.psicologic.action.PLAY"
        const val ACTION_PAUSE = "com.example.psicologic.action.PAUSE"
        const val ACTION_RESUME = "com.example.psicologic.action.RESUME"
        const val ACTION_STOP = "com.example.psicologic.action.STOP"

        const val EXTRA_SOUND_ID = "extra_sound_id"
    }
}
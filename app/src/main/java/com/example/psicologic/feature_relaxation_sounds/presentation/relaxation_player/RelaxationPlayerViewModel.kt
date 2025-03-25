// RelaxationPlayerViewModel.kt
package com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.GetRelaxationSoundsUseCase
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.PlayRelaxationSoundUseCase
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.StopRelaxationSoundUseCase
import com.example.psicologic.feature_relaxation_sounds.presentation.service.RelaxationSoundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RelaxationPlayerViewModel(
    private val getRelaxationSoundsUseCase: GetRelaxationSoundsUseCase,
    private val playRelaxationSoundUseCase: PlayRelaxationSoundUseCase,
    private val stopRelaxationSoundUseCase: StopRelaxationSoundUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RelaxationPlayerState())
    val state: StateFlow<RelaxationPlayerState> = _state.asStateFlow()

    private var service: RelaxationSoundService? = null
    private var bound = false
    private var sharedPreferences: SharedPreferences? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as RelaxationSoundService.RelaxationBinder
            service = serviceBinder.getService()
            bound = true

            Log.d("RelaxationVM", "Servicio conectado")

            // Observar estado de reproducción
            service?.isPlaying?.onEach { isPlaying ->
                _state.value = _state.value.copy(isPlaying = isPlaying)
                Log.d("RelaxationVM", "isPlaying actualizado: $isPlaying")
            }?.launchIn(viewModelScope)

            // Observar progreso actual
            service?.currentProgress?.onEach { progress ->
                _state.value = _state.value.copy(currentProgress = progress)
            }?.launchIn(viewModelScope)

            // Observar el ID del sonido actual
            service?.currentSoundId?.onEach { soundId ->
                soundId?.let {
                    _state.value = _state.value.copy(selectedSoundId = it)
                    Log.d("RelaxationVM", "selectedSoundId actualizado: $it")

                    // Guardar el ID en SharedPreferences para recuperarlo después de reiniciar
                    saveCurrentSoundId(it)
                }
            }?.launchIn(viewModelScope)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("RelaxationVM", "Servicio desconectado")
            service = null
            bound = false
            // No actualizamos isPlaying aquí para permitir que siga en segundo plano
        }
    }

    init {
        loadSounds()
    }

    fun initialize(context: Context) {
        // Inicializar SharedPreferences
        sharedPreferences = context.getSharedPreferences("relaxation_prefs", Context.MODE_PRIVATE)

        // Intentar cargar el ID del sonido guardado previamente
        val savedSoundId = getSavedSoundId()
        savedSoundId?.let {
            _state.value = _state.value.copy(selectedSoundId = it)
        }

        // Verificar permisos de batería
        val batteryOptimized = checkBatteryOptimization(context)
        _state.value = _state.value.copy(batteryOptimizationIgnored = batteryOptimized)

        // Enlazar con el servicio
        bindService(context)
    }

    fun bindService(context: Context) {
        try {
            val intent = Intent(context, RelaxationSoundService::class.java)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Log.d("RelaxationVM", "Intentando enlazar con el servicio")
        } catch (e: Exception) {
            Log.e("RelaxationVM", "Error al enlazar con el servicio", e)
        }
    }

    fun unbindService(context: Context) {
        if (bound) {
            try {
                context.unbindService(connection)
                Log.d("RelaxationVM", "Servicio desenlazado")
            } catch (e: Exception) {
                Log.e("RelaxationVM", "Error al desenlazar el servicio", e)
            }
            bound = false
        }
    }

    private fun loadSounds() {
        viewModelScope.launch {
            try {
                getRelaxationSoundsUseCase().onEach { sounds ->
                    _state.value = _state.value.copy(
                        sounds = sounds,
                        isLoading = false
                    )
                    Log.d("RelaxationVM", "Sonidos cargados: ${sounds.size}")
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                Log.e("RelaxationVM", "Error al cargar sonidos", e)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun playSound(context: Context, soundId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedSoundId = soundId
            )

            // Verificar y solicitar permisos de batería si es necesario
            if (!_state.value.batteryOptimizationIgnored) {
                checkBatteryOptimization(context)
            }

            Log.d("RelaxationVM", "Iniciando reproducción del sonido: $soundId")

            // Iniciar el servicio como Foreground Service
            val intent = Intent(context, RelaxationSoundService::class.java).apply {
                action = RelaxationSoundService.ACTION_PLAY
                putExtra(RelaxationSoundService.EXTRA_SOUND_ID, soundId)
            }

            saveCurrentSoundId(soundId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            // Asegurarse de estar enlazado con el servicio
            if (!bound) {
                bindService(context)
            }
        }
    }

    fun pauseResumeSound(context: Context) {
        val action = if (_state.value.isPlaying) {
            RelaxationSoundService.ACTION_PAUSE
        } else {
            RelaxationSoundService.ACTION_RESUME
        }

        Log.d("RelaxationVM", "Ejecutando acción: $action")

        val intent = Intent(context, RelaxationSoundService::class.java).apply {
            this.action = action
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopSound(context: Context) {
        viewModelScope.launch {
            Log.d("RelaxationVM", "Deteniendo reproducción")

            val intent = Intent(context, RelaxationSoundService::class.java).apply {
                action = RelaxationSoundService.ACTION_STOP
            }
            context.startService(intent)

            _state.value = _state.value.copy(
                selectedSoundId = null,
                isPlaying = false
            )

            // Borrar el ID del sonido guardado
            clearSavedSoundId()
        }
    }

    /**
     * Verifica si la aplicación tiene permiso para ignorar optimizaciones de batería
     * y solicita al usuario si es necesario
     */
    fun checkBatteryOptimization(context: Context): Boolean {
        val packageName = context.packageName
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
        _state.value = _state.value.copy(batteryOptimizationIgnored = isIgnoringBatteryOptimizations)

        return if (isIgnoringBatteryOptimizations) {
            Log.d("RelaxationVM", "La app ya está exenta de optimizaciones de batería")
            true
        } else {
            // Si no tiene permisos, solicitarlos
            try {
                Log.d("RelaxationVM", "Solicitando exención de optimizaciones de batería")
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("RelaxationVM", "Error al solicitar exención de batería", e)
                // Algunos dispositivos pueden no soportar esta acción
            }
            false
        }
    }

    // Métodos para gestionar el estado persistente
    private fun saveCurrentSoundId(soundId: Int) {
        sharedPreferences?.edit()?.apply {
            putInt("current_sound_id", soundId)
            apply()
        }
    }

    private fun getSavedSoundId(): Int? {
        return sharedPreferences?.let {
            val id = it.getInt("current_sound_id", -1)
            if (id != -1) id else null
        }
    }

    private fun clearSavedSoundId() {
        sharedPreferences?.edit()?.apply {
            remove("current_sound_id")
            apply()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // No detenemos el servicio aquí para permitir que siga en segundo plano
        Log.d("RelaxationVM", "ViewModel destruido, servicio continúa en segundo plano")
    }
}

data class RelaxationPlayerState(
    val sounds: List<RelaxationSound> = emptyList(),
    val selectedSoundId: Int? = null,
    val isPlaying: Boolean = false,
    val currentProgress: Int = 0,
    val isLoading: Boolean = true,
    val batteryOptimizationIgnored: Boolean = false
)
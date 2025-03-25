// RelaxationDatabase.kt
package com.example.psicologic.feature_relaxation_sounds.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.psicologic.feature_relaxation_sounds.data.models.RelaxationSoundEntity
import com.example.psicologic.feature_relaxation_sounds.data.models.RelaxationSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RelaxationSoundEntity::class, RelaxationSessionEntity::class],
    version = 1,
    exportSchema = false  // Añadir esto para resolver el aviso
)
@TypeConverters(DateConverters::class)
abstract class RelaxationDatabase : RoomDatabase() {

    abstract fun relaxationSoundDao(): RelaxationSoundDao

    companion object {
        @Volatile
        private var INSTANCE: RelaxationDatabase? = null

        fun getDatabase(context: Context): RelaxationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RelaxationDatabase::class.java,
                    "relaxation_database"
                )
                    .addCallback(RelaxationDatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class RelaxationDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(database.relaxationSoundDao())
                }
            }
        }

        private suspend fun prepopulateDatabase(dao: RelaxationSoundDao) {
            // Precargamos las dos canciones específicas
            val sounds = listOf(
                RelaxationSoundEntity(
                    id = 1,
                    name = "Lights Are On",  // Corrección del nombre
                    description = "Música relajante para meditación",
                    resourcePath = "home",  // Usa el nombre del archivo real sin extensión
                    durationSeconds = 193,  // 3:13 en segundos
                    category = "Música"
                ),
                RelaxationSoundEntity(
                    id = 2,
                    name = "Caloncho Optimista",
                    description = "Música positiva para elevar tu estado de ánimo",
                    resourcePath = "caloncho_optimista",
                    durationSeconds = 212,  // 3:32 en segundos
                    category = "Música"
                )
            )
            dao.insertSounds(sounds)
        }
    }
}
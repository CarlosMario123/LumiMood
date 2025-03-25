// RelaxationSoundDao.kt
package com.example.psicologic.feature_relaxation_sounds.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.psicologic.feature_relaxation_sounds.data.models.RelaxationSoundEntity
import com.example.psicologic.feature_relaxation_sounds.data.models.RelaxationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelaxationSoundDao {
    @Query("SELECT * FROM relaxation_sounds")
    fun getAllSounds(): Flow<List<RelaxationSoundEntity>>

    @Query("SELECT * FROM relaxation_sounds WHERE id = :soundId")
    suspend fun getSoundById(soundId: Int): RelaxationSoundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSound(sound: RelaxationSoundEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSounds(sounds: List<RelaxationSoundEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: RelaxationSessionEntity): Long

    @Query("SELECT * FROM relaxation_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<RelaxationSessionEntity>>
}
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppCustomizationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppCustomizationDao {

    @Query("SELECT * FROM app_customizations")
    fun getAll(): Flow<List<AppCustomizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppCustomizationEntity)

    @Query("DELETE FROM app_customizations WHERE uniqueKey = :uniqueKey")
    suspend fun delete(uniqueKey: String)
}

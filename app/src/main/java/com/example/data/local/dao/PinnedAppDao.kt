package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PinnedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedAppDao {

    @Query("SELECT * FROM pinned_apps ORDER BY orderIndex ASC")
    fun getAllPinnedApps(): Flow<List<PinnedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: PinnedAppEntity)

    @Query("DELETE FROM pinned_apps WHERE packageName = :packageName")
    suspend fun unpinApp(packageName: String)

    @Query("DELETE FROM pinned_apps WHERE packageName NOT IN (:installedPackages)")
    suspend fun removeUninstalledPinnedApps(installedPackages: List<String>)

    @Query("UPDATE pinned_apps SET launchCount = launchCount + 1, lastLaunched = :time WHERE packageName = :packageName")
    suspend fun recordLaunch(packageName: String, time: Long = System.currentTimeMillis())
}

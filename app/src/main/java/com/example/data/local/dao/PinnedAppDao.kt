package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.PinnedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedAppDao {

    @Query("SELECT * FROM pinned_apps ORDER BY orderIndex ASC")
    fun getAllPinnedApps(): Flow<List<PinnedAppEntity>>

    /** One-shot read used by the reorder / insert / prune paths. */
    @Query("SELECT * FROM pinned_apps ORDER BY orderIndex ASC")
    suspend fun getPinnedAppsOnce(): List<PinnedAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: PinnedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPins(apps: List<PinnedAppEntity>)

    @Query("DELETE FROM pinned_apps")
    suspend fun clearPins()

    /**
     * Persists the dock exactly as given — membership AND order. A plain @Update
     * only rewrites rows that already exist, which silently dropped every newly
     * pinned app, so the whole list is rewritten inside one transaction instead.
     */
    @Transaction
    suspend fun replacePins(apps: List<PinnedAppEntity>) {
        clearPins()
        insertAllPins(apps)
    }

    @Query("DELETE FROM pinned_apps WHERE packageName = :packageName")
    suspend fun unpinApp(packageName: String)

    @Query("UPDATE pinned_apps SET launchCount = launchCount + 1, lastLaunched = :time WHERE packageName = :packageName")
    suspend fun recordLaunch(packageName: String, time: Long = System.currentTimeMillis())
}

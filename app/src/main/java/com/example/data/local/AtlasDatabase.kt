package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AppCustomizationDao
import com.example.data.local.dao.FocusDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.PinnedAppDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.AppCustomizationEntity
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.PinnedAppEntity
import com.example.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        FocusSessionEntity::class,
        PinnedAppEntity::class,
        NoteEntity::class,
        AppCustomizationEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AtlasDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun focusDao(): FocusDao
    abstract fun pinnedAppDao(): PinnedAppDao
    abstract fun noteDao(): NoteDao
    abstract fun appCustomizationDao(): AppCustomizationDao

    companion object {
        @Volatile
        private var INSTANCE: AtlasDatabase? = null

        /**
         * Real migration instead of a destructive wipe: the pinned dock and every
         * other user table survive the upgrade. (Losing this table was one of the
         * reasons dock pins used to reset.)
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `app_customizations` (" +
                        "`uniqueKey` TEXT NOT NULL, " +
                        "`customLabel` TEXT, " +
                        "`iconPresetId` INTEGER, " +
                        "`customIconUri` TEXT, " +
                        "`updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`uniqueKey`))"
                )
            }
        }

        fun getInstance(context: Context): AtlasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtlasDatabase::class.java,
                    "atlas_launcher.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

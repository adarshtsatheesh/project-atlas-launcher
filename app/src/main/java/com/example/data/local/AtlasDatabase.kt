package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.FocusDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.PinnedAppDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.PinnedAppEntity
import com.example.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        FocusSessionEntity::class,
        PinnedAppEntity::class,
        NoteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AtlasDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun focusDao(): FocusDao
    abstract fun pinnedAppDao(): PinnedAppDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AtlasDatabase? = null

        fun getInstance(context: Context): AtlasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtlasDatabase::class.java,
                    "atlas_launcher.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

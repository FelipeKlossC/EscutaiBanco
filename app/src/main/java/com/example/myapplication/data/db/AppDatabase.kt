package com.example.myapplication.data.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.myapplication.model.Musica
import com.example.myapplication.model.Playlist

@Database(entities = [Musica::class, Playlist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun musicaDao(): MusicaDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            } else {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}

package ru.rst.penyakitbawang.database

import androidx.room.RoomDatabase
import androidx.room.Database

@Database(entities = [DataHistoryEntity::class], version = 1, exportSchema = false)
abstract class DataHistoryDatabase : RoomDatabase() {
    abstract fun dataHistoryDao(): DataHistoryDao

}
package ru.rst.penyakitbawang.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity (tableName = "data")
@Parcelize
data class DataHistoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: String,
    val result: String,
    val score: Float,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable
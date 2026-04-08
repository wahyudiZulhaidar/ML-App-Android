package ru.rst.penyakitbawang.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DataHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataHistoryEntity: DataHistoryEntity)

    @Query("SELECT * FROM data ORDER BY timeStamp DESC LIMIT 10")
    fun getDataHistory(): LiveData<List<DataHistoryEntity>>

    @Query("DELETE FROM data WHERE id NOT IN (SELECT id FROM data ORDER BY timeStamp DESC LIMIT 10)")
    fun deleteOldHistory()
}
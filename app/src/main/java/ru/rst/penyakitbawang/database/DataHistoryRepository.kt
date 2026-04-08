package ru.rst.penyakitbawang.database

import androidx.lifecycle.LiveData
import javax.inject.Inject

class DataHistoryRepository @Inject constructor(private val dao: DataHistoryDao) {

    fun getDataHistoryOrdered(): LiveData<List<DataHistoryEntity>> = dao.getDataHistory()

//    suspend fun insert(history: DataHistoryEntity) {
//        dao.insert(history)
//    }
//
//    suspend fun delete(history: DataHistoryEntity) {
//        dao.deleteOldHistory(history)
//    }
}
package ru.rst.penyakitbawang.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.rst.penyakitbawang.database.DataHistoryEntity
import ru.rst.penyakitbawang.database.DataHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: DataHistoryRepository) : ViewModel() {
    val getDataHistory: LiveData<List<DataHistoryEntity>> = repository.getDataHistoryOrdered()
}

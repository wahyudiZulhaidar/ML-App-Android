package ru.rst.penyakitbawang.di

import android.content.Context
import androidx.room.Room
import ru.rst.penyakitbawang.database.DataHistoryDao
import ru.rst.penyakitbawang.database.DataHistoryDatabase
import ru.rst.penyakitbawang.helper.ClassifierListener
import ru.rst.penyakitbawang.helper.ClassifierListenerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DataHistoryDatabase {
        return Room.databaseBuilder(
            context,
            DataHistoryDatabase::class.java,
            "data_history"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDataHistoryDao(database: DataHistoryDatabase): DataHistoryDao {
        return database.dataHistoryDao()
    }

    @Provides
    @Singleton
    fun provideClassifierListener(): ClassifierListener {
        return ClassifierListenerImpl()
    }
}
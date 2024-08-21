package com.limongradstudio.gyfer

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.Database
import com.limongradstudio.gyfer.data.repositories.WordRepositoryImpl
import com.limongradstudio.gyfer.domain.repositories.WordRepository
import com.limongradstudio.gyfer.presentation.viewmodels.WordViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

class MainApplication : Application() {
    private val appModule = module {
        single {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(),
                name = "words.db"
            ) as SqlDriver
        }
        single { Database(get()) }
        single<WordRepository> { WordRepositoryImpl(get()) }
        viewModel { WordViewModel(get()) }

    }

    override fun onCreate() {
        super.onCreate()
        initKoin()

    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
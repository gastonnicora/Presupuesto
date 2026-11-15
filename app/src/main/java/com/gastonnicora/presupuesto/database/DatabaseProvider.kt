package com.gastonnicora.presupuesto.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver

object DatabaseProvider {
    const val DB_NAME = "presupuesto.db"

    @Volatile
    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder<AppDatabase>(
                context.applicationContext,
                DB_NAME
            )
                .setDriver(AndroidSQLiteDriver())
                .addMigrations(*ALL_MIGRATIONS)
                .build()
                .also { instance = it }
        }

    /** Cierra la base. La próxima llamada a get() la vuelve a abrir. */
    fun close() {
        synchronized(this) {
            instance?.close()
            instance = null
        }
    }
}

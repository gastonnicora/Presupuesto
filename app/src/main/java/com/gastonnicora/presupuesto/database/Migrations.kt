package com.gastonnicora.presupuesto.database


import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

// Ejemplo: versión 3 -> 4 agregando una columna
val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE Presupuesto ADD COLUMN condicionVenta TEXT NOT NULL DEFAULT 'Contado'"
        )
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_3_4)
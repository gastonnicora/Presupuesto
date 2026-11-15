package com.gastonnicora.presupuesto.database

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_budgets_client_id ON budgets(client_id)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_budget_items_item_id ON budget_items(item_id)"
        )
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
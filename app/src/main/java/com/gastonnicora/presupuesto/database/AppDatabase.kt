package com.gastonnicora.presupuesto.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.gastonnicora.presupuesto.dao.BudgetDao
import com.gastonnicora.presupuesto.dao.ClientDao
import com.gastonnicora.presupuesto.dao.ItemDao
import com.gastonnicora.presupuesto.model.Budget
import com.gastonnicora.presupuesto.model.BudgetItem
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.Item

@Database(
    entities = [Client::class, Item::class, Budget::class, BudgetItem::class],
    exportSchema = true,
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun itemDao(): ItemDao
    abstract fun budgetDao(): BudgetDao
}
package com.gastonnicora.presupuesto.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

/** Relación entre un presupuesto y los productos que incluye, con su cantidad. */
@Entity(
    tableName = "budget_items",
    primaryKeys = ["budget_id", "item_id"],
    foreignKeys = [
        ForeignKey(
            entity = Budget::class,
            parentColumns = ["id"],
            childColumns = ["budget_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("item_id")]
)
data class BudgetItem(
    @ColumnInfo(name = "budget_id") val budgetId: Int,
    @ColumnInfo(name = "item_id") val itemId: Int,
    val quantity: Int = 1
)
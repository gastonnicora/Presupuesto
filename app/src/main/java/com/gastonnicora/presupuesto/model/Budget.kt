package com.gastonnicora.presupuesto.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

/** Presupuesto emitido a un cliente. */
@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val date: String,
    @ColumnInfo(name = "sales_condition") val salesCondition: SalesCondition,
    @ColumnInfo(name = "client_id") val clientId: Int,
    val total: Long

)
/** Presupuesto de ejemplo para las vistas previas. */
internal fun sampleBudget() = Budget(
    id = 1,
    date = formatDate(System.currentTimeMillis()),
    salesCondition = SalesCondition.CASH,
    clientId = 1,
    total = 1250000L
)

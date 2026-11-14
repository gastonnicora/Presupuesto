package com.gastonnicora.presupuesto.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** Cliente al que se le emite un presupuesto. */
@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    val address: String="-",
    @ColumnInfo(name = "iva_category") val ivaCategory: IvaCategory= IvaCategory.CONSUMIDOR_FINAL,
    val locality: String="-",
    val cuit: String="-",
    val active: Boolean = true
)

/** Cliente de ejemplo para las vistas previas. */
internal fun sampleClient() = Client(
    id = 1,
    name = "Juan Pérez",
    address = "Calle Falsa (123)",
    ivaCategory = IvaCategory.CONSUMIDOR_FINAL,
    locality = "La Plata",
    cuit = "11111111111"
)
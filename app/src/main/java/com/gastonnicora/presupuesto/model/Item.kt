package com.gastonnicora.presupuesto.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** Ítem que se incluye en un presupuesto. */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    // Precio en centavos para evitar errores de redondeo: 200020 = $2000.20
    @ColumnInfo(name = "unit_price_cents") val unitPriceCents: Long,
    val active: Boolean = true
)



/** Ítem de ejemplo para las vistas previas. */
internal fun sampleItem() = Item(
    id = 1,
    name = "Rollo de alambre corralero (200m)",
    unitPriceCents = 200020L,
    true
)
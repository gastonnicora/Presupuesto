package com.gastonnicora.presupuesto.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Formatea centavos como pesos: 200020 -> "$2.000,20" */
fun formatPrice(cents: Long): String {
    val negative = cents < 0
    val absolute = kotlin.math.abs(cents)
    val pesos = (absolute / 100).toString()
    val centavos = (absolute % 100).toString().padStart(2, '0')

    val grouped = StringBuilder(pesos.length + pesos.length / 3)
    pesos.forEachIndexed { index, digit ->
        if (index > 0 && (pesos.length - index) % 3 == 0) grouped.append('.')
        grouped.append(digit)
    }

    return "$" + (if (negative) "-" else "") + grouped + "," + centavos
}


fun formatDate(millis: Long): String =
    java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "AR"))
        .format(java.util.Date(millis))


suspend fun loadLogo(path: String?): ByteArray? {
    if (path.isNullOrBlank()) return null
    return withContext(Dispatchers.IO) {
        runCatching { File(path).readBytes() }.getOrNull()
    }
}

package com.gastonnicora.presupuesto.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Formatea centavos como pesos: 200020 -> "$2.000,20" */
fun formatPrice(cents: Long): String {
    val pesos = cents / 100
    val centavos = (cents % 100).toString().padStart(2, '0')
    val pesosFormateados = "%,d".format(pesos).replace(',', '.')
    return "$$pesosFormateados,$centavos"
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

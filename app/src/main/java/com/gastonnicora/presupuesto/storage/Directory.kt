package com.gastonnicora.presupuesto.storage

import android.content.Context
import java.io.File

/** Carpeta raíz de los PDF: Presupuestos/ dentro del almacenamiento externo de la app. */
fun presupuestosRaiz(context: Context): File =
    File(context.getExternalFilesDir(null), "Presupuestos")

/** Crea (si hace falta) la subcarpeta de un cliente y la devuelve. */
fun createDirectory(context: Context, path: String): File {
    val carpeta = File(presupuestosRaiz(context), path)
    if (!carpeta.exists()) carpeta.mkdirs()
    return carpeta
}

package com.gastonnicora.presupuesto.storage

import android.content.Context
import java.io.File

fun createDirectory(context: Context,path: String): File {
    val carpeta = File(context.getExternalFilesDir(null), "Presupuestos/$path")
    if (!carpeta.exists()) {
        carpeta.mkdirs()
    }
    return carpeta
}

fun moveDirectory(origen: File, destino: File): Boolean {
    if (!origen.exists()) return false

    // Copia todo, incluyendo subcarpetas. Sobrescribe si ya existe.
    val copiado = origen.copyRecursively(destino, overwrite = true)
    if (!copiado) return false

    // Solo borra el origen si la copia salió bien
    return origen.deleteRecursively()
}


val PATH="Presupuestos/"
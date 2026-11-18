package com.gastonnicora.presupuesto.storage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File


fun sharePdf(context: Context, archivo: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Compartir presupuesto"))
}


fun savePdf(context: Context, clientId: Any, fileName: String, bytes: ByteArray): File {
    val carpeta = createDirectory(context, clientId.toString()) // -> Presupuestos/<clientId>
    val archivo = File(carpeta, "presupuesto_${fileName}.pdf")
    archivo.writeBytes(bytes)
    return archivo
}

fun deletePdf(context: Context, clientId: Any, fileName: String): Boolean {
    val carpeta = File(presupuestosRaiz(context), "$clientId")
    val archivo = File(carpeta, "presupuesto_${fileName}.pdf")

    if (!archivo.exists()) return false

    val borrado = archivo.delete()

    // Si la carpeta del cliente quedó vacía, la borra también
    if (borrado && carpeta.exists() && carpeta.list()?.isEmpty() == true) {
        carpeta.delete()
    }

    return borrado
}

data class PdfEntry(
    val clientId: String,
    val fileName: String,
    val file: File,
    val modified: Long
)

fun listAllPdfs(context: Context): List<PdfEntry> {
    val raiz = presupuestosRaiz(context)
    return raiz.listFiles { f -> f.isDirectory }.orEmpty()
        .flatMap { carpeta ->
            carpeta.listFiles { f -> f.isFile && f.extension.equals("pdf", ignoreCase = true) }
                .orEmpty()
                .map { archivo ->
                    PdfEntry(carpeta.name, archivo.name, archivo, archivo.lastModified())
                }
        }
        .sortedByDescending { it.modified }
}

fun openPdf(context: Context, archivo: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una app para abrir PDF", Toast.LENGTH_SHORT).show()
    }
}
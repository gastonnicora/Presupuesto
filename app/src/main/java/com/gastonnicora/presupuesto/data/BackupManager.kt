package com.gastonnicora.presupuesto.data

import android.content.Context
import android.net.Uri
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.gastonnicora.presupuesto.database.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

object BackupManager {

    // La versión más baja de la que tenés migraciones hasta llegar a la actual
    private const val VERSION_MINIMA_SOPORTADA = 1
    private const val VERSION_ESQUEMA = 2 // la versión actual de AppDatabase

    suspend fun exportar(context: Context, destino: Uri): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val archivo = context.getDatabasePath(DatabaseProvider.DB_NAME)
                check(archivo.exists()) { "Todavía no hay datos para guardar" }

                // Cerrar la instancia de Room antes de abrir una conexión propia
                DatabaseProvider.close()

                // Volcar todo el -wal al archivo principal y truncarlo
                val conexion = AndroidSQLiteDriver().open(archivo.path)
                try {
                    conexion.prepare("PRAGMA wal_checkpoint(TRUNCATE)").use { it.step() }
                } finally {
                    conexion.close()
                }

                val salida = context.contentResolver.openOutputStream(destino, "wt")
                    ?: error("No se pudo abrir el archivo de destino")
                salida.use { out ->
                    archivo.inputStream().use { entrada -> entrada.copyTo(out) }
                }
                Unit
            }
        }

    suspend fun importar(context: Context, origen: Uri): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val temporal = File(context.cacheDir, "import_temp.db")
                context.contentResolver.openInputStream(origen)?.use { entrada ->
                    temporal.outputStream().use { entrada.copyTo(it) }
                } ?: error("No se pudo leer el archivo elegido")

                try {

                    check(esCopiaValida(temporal)) {
                        "El archivo no es una copia válida de esta versión de la app"
                    }

                    DatabaseProvider.close()
                    val archivo = context.getDatabasePath(DatabaseProvider.DB_NAME)
                    File(archivo.path + "-wal").delete()
                    File(archivo.path + "-shm").delete()
                    temporal.copyTo(archivo, overwrite = true)
                } finally {
                    temporal.delete()
                }
                Unit
            }

        }


    /** Revisa la cabecera SQLite y que user_version (bytes 60-63) esté en el rango soportado. */
    private fun esCopiaValida(archivo: File): Boolean {
        val cabecera = ByteArray(64)
        val leidos = archivo.inputStream().use { input ->
            var total = 0
            while (total < cabecera.size) {
                val n = input.read(cabecera, total, cabecera.size - total)
                if (n < 0) break
                total += n
            }
            total
        }
        if (leidos < cabecera.size) return false

        val firma = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        if (!cabecera.copyOfRange(0, 16).contentEquals(firma)) return false

        val version = ByteBuffer.wrap(cabecera, 60, 4).int
        return version in VERSION_MINIMA_SOPORTADA..VERSION_ESQUEMA
    }


}
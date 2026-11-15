package com.gastonnicora.presupuesto.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.model.IvaCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

// Mismo nombre que la versión anterior: así no se pierden los datos ya guardados.
private val Context.companyDataStore by preferencesDataStore(name = "owner_data")

class CompanyRepository(private val context: Context) {

    // Las claves son las mismas que ya están guardadas en el celular.
    private object Keys {
        val NAME = stringPreferencesKey("company")
        val EMAIL = stringPreferencesKey("email")
        val ADDRESS = stringPreferencesKey("direction")
        val PHONE = stringPreferencesKey("phone")
        val CUIT = stringPreferencesKey("cuit")
        val IIBB = stringPreferencesKey("iibb")
        val START_DATE = stringPreferencesKey("fromDate")
        val LOGO_PATH = stringPreferencesKey("imagePath")
        val IVA_CATEGORY = stringPreferencesKey("ivaCategory")
    }

    val companyData: Flow<CompanyData> = context.companyDataStore.data.map { prefs ->
        CompanyData(
            name = prefs[Keys.NAME] ?: "",
            email = prefs[Keys.EMAIL] ?: "",
            address = prefs[Keys.ADDRESS] ?: "",
            phone = prefs[Keys.PHONE] ?: "",
            cuit = prefs[Keys.CUIT] ?: "",
            iibb = prefs[Keys.IIBB] ?: "",
            startDate = prefs[Keys.START_DATE] ?: "",
            ivaCategory = prefs[Keys.IVA_CATEGORY]?.let { name ->
                runCatching { IvaCategory.valueOf(name) }.getOrNull()
            },
            logoPath = prefs[Keys.LOGO_PATH] ?: ""
        )
    }

    suspend fun save(company: CompanyData) {
        context.companyDataStore.edit { prefs ->
            prefs[Keys.NAME] = company.name
            prefs[Keys.EMAIL] = company.email
            prefs[Keys.ADDRESS] = company.address
            prefs[Keys.PHONE] = company.phone
            prefs[Keys.CUIT] = company.cuit
            prefs[Keys.IIBB] = company.iibb
            prefs[Keys.START_DATE] = company.startDate
            prefs[Keys.IVA_CATEGORY] = company.ivaCategory?.name ?: ""
            prefs[Keys.LOGO_PATH] = company.logoPath
        }
    }

    /** Copia el logo elegido al almacenamiento interno, reducido a 512 px. Devuelve la ruta nueva. */
    suspend fun importLogo(uri: Uri): String = withContext(Dispatchers.IO) {
        val original = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: error("No se pudo leer la imagen")

        val scale = MAX_LOGO_SIZE.toFloat() / maxOf(original.width, original.height)
        val scaled = if (scale < 1f) {
            original.scale((original.width * scale).toInt(), (original.height * scale).toInt())
        } else {
            original
        }

        val file = File(context.filesDir, "logo_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        file.absolutePath
    }

    companion object {
        private const val MAX_LOGO_SIZE = 512
    }
}

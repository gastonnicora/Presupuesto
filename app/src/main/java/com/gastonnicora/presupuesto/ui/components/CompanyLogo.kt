package com.gastonnicora.presupuesto.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Logo de la empresa. Si no hay logo, no dibuja nada.
 * La imagen se decodifica fuera del hilo principal, así no se traba la pantalla.
 */
@Composable
fun CompanyLogo(logoPath: String, modifier: Modifier = Modifier) {
    val logo by produceState<ImageBitmap?>(null, logoPath) {
        value = if (logoPath.isEmpty()) {
            null
        } else {
            withContext(Dispatchers.IO) { BitmapFactory.decodeFile(logoPath)?.asImageBitmap() }
        }
    }

    logo?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = "Logo de la empresa",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

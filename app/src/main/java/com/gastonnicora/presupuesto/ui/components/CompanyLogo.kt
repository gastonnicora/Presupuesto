package com.gastonnicora.presupuesto.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

/** Logo de la empresa. Si todavía no se eligió uno, muestra el emblema por defecto. */
@Composable
fun CompanyLogo(logoPath: String, modifier: Modifier = Modifier) {
    val logo = remember(logoPath) {
        if (logoPath.isEmpty()) null
        else BitmapFactory.decodeFile(logoPath)?.asImageBitmap()
    }

    if (logo != null) {
        Image(
            bitmap = logo,
            contentDescription = "Logo de la empresa",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

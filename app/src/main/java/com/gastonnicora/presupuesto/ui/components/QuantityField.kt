package com.gastonnicora.presupuesto.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Campo numérico para la cantidad. Acepta solo dígitos y, si queda vacío, vuelve al valor anterior al salir. */
@Composable
fun QuantityField(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // remember(quantity) reinicia el texto cuando la cantidad cambia desde los botones
    var text by remember(quantity) { mutableStateOf(quantity.toString()) }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(4)
            text = digits
            digits.toIntOrNull()?.takeIf { it > 0 }?.let(onQuantityChange)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        modifier = modifier
            .width(72.dp)
            .onFocusChanged { if (!it.isFocused) text = quantity.toString() }
    )
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun QuantityFieldPreview() {
    QuantityField(9999, {}, Modifier)
}
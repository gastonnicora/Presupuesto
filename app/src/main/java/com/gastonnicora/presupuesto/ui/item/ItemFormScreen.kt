package com.gastonnicora.presupuesto.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.model.formatPrice
import com.gastonnicora.presupuesto.model.sampleItem
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/**
 * Formulario de alta y edición de productos.
 * Si [initial] es null es un alta. Si no, precarga los datos y conserva el id.
 */
@Composable
fun ItemFormScreen(
    initial: Item? = null,
    onSave: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    // El usuario escribe pesos (ej: "2000,20"), se convierte a centavos al guardar
    var priceText by remember {
        mutableStateOf(initial?.let {
            formatPrice(it.unitPriceCents).removePrefix("$")
                .replace(".", "")
        } ?: "")
    }

    val priceCents = parsePriceToCents(priceText)
    val isFormValid = name.isNotBlank() && priceCents != null && priceCents > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = priceText,
            onValueChange = { input ->
                // Solo dígitos, una coma y hasta dos decimales
                if (PRICE_INPUT.matches(input)) priceText = input
            },
            label = { Text("Precio unitario ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = priceText.isNotEmpty() && (priceCents == null || priceCents <= 0),
            supportingText = {
                if (priceText.isNotEmpty() && (priceCents == null || priceCents <= 0)) {
                    Text("Ingresá un precio mayor a cero")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (priceCents != null) {
                    onSave(
                        Item(
                            id = initial?.id ?: 0,
                            name = name.trim(),
                            unitPriceCents = priceCents
                        )
                    )
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Guardar producto")
        }
    }
}

/** "2000,20" -> 200020. Devuelve null si el texto no es un precio válido. */
private fun parsePriceToCents(text: String): Long? {
    if (text.isBlank()) return null
    val parts = text.split(",")
    val pesos = parts[0].toLongOrNull() ?: return null
    val centavos = when (parts.size) {
        1 -> 0L
        2 -> parts[1].padEnd(2, '0').toLongOrNull() ?: return null
        else -> return null
    }
    return pesos * 100 + centavos
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemFormPreview() {
    PresupuestoTheme {
        ItemFormScreen(initial = sampleItem(), onSave = {})
    }
}

private val PRICE_INPUT = Regex("^\\d*(,\\d{0,2})?$")

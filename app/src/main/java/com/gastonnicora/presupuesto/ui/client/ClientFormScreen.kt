package com.gastonnicora.presupuesto.ui.client

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
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.IvaCategory
import com.gastonnicora.presupuesto.model.sampleClient
import com.gastonnicora.presupuesto.ui.components.EnumDropdown
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/**
 * Formulario de alta y edición de clientes.
 * Si [initial] es null es un alta. Si no, precarga los datos y conserva id y active.
 */
@Composable
fun ClientFormScreen(
    initial: Client? = null,
    onSave: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var address by remember { mutableStateOf(initial?.address ?: "") }
    var locality by remember { mutableStateOf(initial?.locality ?: "") }
    var cuit by remember { mutableStateOf(initial?.cuit ?: "") }
    var ivaCategory by remember { mutableStateOf(initial?.ivaCategory) }

    val isFormValid = name.isNotBlank()

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
            label = { Text("Nombre y apellido") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = locality,
            onValueChange = { locality = it },
            label = { Text("Localidad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cuit,
            onValueChange = { input ->
                // Solo dígitos, máximo 11
                if (input.length <= 11 && input.all { it.isDigit() }) cuit = input
            },
            label = { Text("CUIT") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = cuit.length == 11,
            supportingText = {
                if (cuit.length == 11) Text("CUIT inválido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        EnumDropdown(
            label = "Condición frente al IVA",
            options = IvaCategory.entries,
            selected = ivaCategory ?: IvaCategory.CONSUMIDOR_FINAL,
            optionLabel = { it.label },
            onSelected = { ivaCategory = it }
        )


        Button(
            onClick = {
                onSave(
                    Client(
                        id = initial?.id ?: 0,
                        name = name.trim(),
                        address = address.trim(),
                        ivaCategory = ivaCategory ?: IvaCategory.CONSUMIDOR_FINAL,
                        locality = locality.trim(),
                        cuit = cuit,
                        active = initial?.active ?: true
                    )
                )


            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Guardar cliente")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClientFormPreview() {
    PresupuestoTheme {
        ClientFormScreen(initial = sampleClient(), onSave = {})
    }
}
package com.gastonnicora.presupuesto.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.sampleClient
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/** Dropdown con buscador: el usuario escribe para filtrar y después elige de la lista. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdown(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(selected?.let(optionLabel) ?: "") }
    val filtered = options.filter { optionLabel(it).contains(query, ignoreCase = true) }
    val showMenu = expanded && filtered.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = showMenu,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { text ->
                query = text
                expanded = true
                // Si el usuario cambia el texto, la selección anterior deja de valer
                onSelected(null)
            },
            label = { Text(label) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMenu) },
            isError = selected == null,
            supportingText = {
                if (selected == null) Text("Seleccioná una opción")
            },
            modifier = modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = showMenu,
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        query = optionLabel(option)
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchableDropdownPreview() {
    PresupuestoTheme {
        var selected by remember { mutableStateOf<Client?>(null) }
        SearchableDropdown(
            label = "Cliente",
            options = List(4) { index ->
                sampleClient().copy(
                    id = index,
                    name = "Cliente ${index + 1}"
                )
            },
            selected = selected,
            optionLabel = { it.name },
            onSelected = { selected = it }
        )
    }
}
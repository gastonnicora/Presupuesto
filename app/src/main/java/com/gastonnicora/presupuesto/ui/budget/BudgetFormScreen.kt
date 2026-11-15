package com.gastonnicora.presupuesto.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.dao.BudgetLine
import com.gastonnicora.presupuesto.model.Budget
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.model.SalesCondition
import com.gastonnicora.presupuesto.model.formatDate
import com.gastonnicora.presupuesto.model.formatPrice
import com.gastonnicora.presupuesto.model.sampleClient
import com.gastonnicora.presupuesto.model.sampleItem
import com.gastonnicora.presupuesto.ui.components.EnumDropdown
import com.gastonnicora.presupuesto.ui.components.QuantityField
import com.gastonnicora.presupuesto.ui.components.SearchBar
import com.gastonnicora.presupuesto.ui.components.SearchableDropdown
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/**
 * Formulario de presupuesto (alta y edición).
 * La fecha no se edita: en alta es la de hoy y en edición se conserva la original.
 * Devuelve el presupuesto y sus líneas. La Activity los guarda en una sola transacción.
 */
@Composable
fun BudgetFormScreen(
    clients: List<Client>,
    products: List<Item>,
    initial: Budget? = null,
    initialLines: List<BudgetLine> = emptyList(),
    onSave: (Budget, List<BudgetLine>) -> Unit,
    modifier: Modifier = Modifier
) {
    var client by remember { mutableStateOf(clients.find { it.id == initial?.clientId }) }
    val dateText = remember { initial?.date ?: formatDate(System.currentTimeMillis()) }
    var condition by remember { mutableStateOf(initial?.salesCondition ?: SalesCondition.CASH) }
    var lines by remember { mutableStateOf(initialLines) }
    var showProductSheet by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val total = lines.sumOf { it.subtotalCents }
    val isFormValid = client != null && lines.isNotEmpty()

    Box(modifier = modifier
        .fillMaxSize()
        .imePadding()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // La lista ocupa el espacio disponible; el pie queda abajo, siempre visible
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SearchableDropdown(
                        label = "Cliente",
                        options = clients,
                        selected = client,
                        optionLabel = { it.name },
                        onSelected = { client = it }
                    )
                }

                item {
                    Text(
                        text = "Fecha: $dateText",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                item {
                    EnumDropdown(
                        label = "Condición de venta",
                        options = SalesCondition.entries,
                        selected = condition,
                        optionLabel = { it.label },
                        onSelected = { condition = it }
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Productos",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        FilledTonalButton(onClick = { showProductSheet = true }) {
                            Text("Agregar productos")
                        }
                    }
                }

                if (lines.isEmpty()) {
                    item {
                        Text(
                            text = "Todavía no agregaste productos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(lines, key = { it.itemId }) { line ->
                        BudgetLineRow(
                            line = line,
                            onQuantityChange = { newQuantity ->
                                lines = lines.map {
                                    if (it.itemId == line.itemId) it.copy(quantity = newQuantity) else it
                                }
                            },
                            onRemove = {
                                lines = lines.filter { it.itemId != line.itemId }
                            }
                        )
                    }
                }
            }

            BudgetFooter(
                total = total,
                enabled = isFormValid && !isSaving,
                onSave = {
                    client?.let { selectedClient ->
                        isSaving = true
                        onSave(
                            Budget(
                                id = initial?.id ?: 0,
                                date = dateText,
                                salesCondition = condition,
                                clientId = selectedClient.id,
                                total = total,
                            ),
                            lines
                        )
                    }
                }
            )
        }

        if (isSaving) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        if (showProductSheet) {
            ProductPickerSheet(
                products = products,
                onDismiss = { showProductSheet = false },
                onConfirm = { picked ->
                    lines = mergeLines(lines, picked)
                    showProductSheet = false
                }
            )
        }
    }
}

/** Pie fijo con el total y el botón de guardar. Siempre visible, incluso con el teclado abierto. */
@Composable
private fun BudgetFooter(
    total: Long,
    enabled: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatPrice(total),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar presupuesto")
        }
    }
}

/** Una línea del presupuesto con cantidad editable, subtotal y botón para quitarla. */
@Composable
private fun BudgetLineRow(
    line: BudgetLine,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val detail = remember(line.unitPriceCents, line.quantity) {
        "${formatPrice(line.unitPriceCents)} c/u · Subtotal ${formatPrice(line.subtotalCents)}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = line.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { onQuantityChange(line.quantity - 1) },
            enabled = line.quantity > 1
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Restar")
        }
        QuantityField(
            quantity = line.quantity,
            onQuantityChange = onQuantityChange,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        IconButton(onClick = { onQuantityChange(line.quantity + 1) }) {
            Icon(Icons.Default.Add, contentDescription = "Sumar")
        }
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Quitar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Hoja inferior para elegir productos y cantidades. Devuelve las líneas elegidas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductPickerSheet(
    products: List<Item>,
    onDismiss: () -> Unit,
    onConfirm: (List<BudgetLine>) -> Unit
) {
    var query by remember { mutableStateOf("") }
    // id del producto -> cantidad elegida. Solo entran los marcados.
    var selection by remember { mutableStateOf(emptyMap<Int, Int>()) }
    val visible = remember(products, query) {
        products.filter { it.name.contains(query, ignoreCase = true) }
    }

    val picked = remember(products, selection) {
        products
            .filter { selection.containsKey(it.id) }
            .map {
                BudgetLine(
                    itemId = it.id,
                    name = it.name,
                    unitPriceCents = it.unitPriceCents,
                    quantity = selection.getValue(it.id)
                )
            }
    }

    // Abre la hoja a altura completa para que el botón tenga lugar cuando sube el teclado
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Agregar productos",
                style = MaterialTheme.typography.titleMedium
            )

            SearchBar(
                query = query,
                onQueryChange = { query = it },
                label = "Buscar producto",
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                visible.forEach { product ->
                    val quantity = selection[product.id] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = quantity > 0,
                            onCheckedChange = { checked ->
                                selection = if (checked) {
                                    selection + (product.id to 1)
                                } else {
                                    selection - product.id
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = formatPrice(product.unitPriceCents),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (quantity > 0) {
                            IconButton(onClick = {
                                selection =
                                    selection + (product.id to (quantity - 1).coerceAtLeast(1))
                            }) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar")
                            }
                            QuantityField(
                                quantity = quantity,
                                onQuantityChange = { newQuantity ->
                                    selection = selection + (product.id to newQuantity)
                                }
                            )
                            IconButton(onClick = {
                                selection = selection + (product.id to quantity + 1)
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirm(picked) },
                enabled = picked.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar ${picked.sumOf { it.quantity }} unidades")
            }
        }
    }
}

/** Suma las cantidades si el producto ya estaba en el presupuesto, en lugar de duplicar la línea. */
private fun mergeLines(current: List<BudgetLine>, added: List<BudgetLine>): List<BudgetLine> {
    val result = current.toMutableList()
    added.forEach { newLine ->
        val index = result.indexOfFirst { it.itemId == newLine.itemId }
        if (index >= 0) {
            result[index] = result[index].copy(quantity = result[index].quantity + newLine.quantity)
        } else {
            result.add(newLine)
        }
    }
    return result
}

// Datos de ejemplo para las vistas previas
private val previewClients = listOf(
    sampleClient().copy(id = 1, name = "Juan Pérez"),
    sampleClient().copy(id = 2, name = "Arena Rurales"),
    sampleClient().copy(id = 3, name = "Estancia La Esperanza")
)

private val previewProducts = listOf(
    sampleItem().copy(id = 1, name = "Rollo de alambre corralero (200m)", unitPriceCents = 200020L),
    sampleItem().copy(id = 2, name = "Poste de quebracho 2,20m", unitPriceCents = 85000L),
    sampleItem().copy(id = 3, name = "Grampas x kg", unitPriceCents = 320050L)
)

private fun previewLine(item: Item, quantity: Int) = BudgetLine(
    itemId = item.id,
    name = item.name,
    unitPriceCents = item.unitPriceCents,
    quantity = quantity
)

/** Edición: cliente elegido, productos cargados y cantidades de distinto largo para probar el campo. */
@Preview(name = "Edición con productos", showBackground = true, showSystemUi = true)
@Composable
private fun BudgetFormEditPreview() {
    PresupuestoTheme {
        BudgetFormScreen(
            clients = previewClients,
            products = previewProducts,
            initial = Budget(
                id = 7,
                date = "12/11/2026",
                salesCondition = SalesCondition.CASH,
                clientId = 2,
                total = 0
            ),
            initialLines = listOf(
                previewLine(previewProducts[0], quantity = 3),
                previewLine(previewProducts[1], quantity = 12),
                previewLine(previewProducts[2], quantity = 1234567)
            ),
            onSave = { _, _ -> }
        )
    }
}

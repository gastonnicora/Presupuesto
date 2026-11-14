package com.gastonnicora.presupuesto.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.model.formatPrice
import com.gastonnicora.presupuesto.model.sampleItem
import com.gastonnicora.presupuesto.ui.components.ItemCard
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/** Lista de productos. El padding inferior deja lugar para que el FAB no tape el último producto. */
@Composable
fun ItemListScreen(
    items: List<Item>,
    onEdit: (Item) -> Unit,
    onDelete: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemCard(
                title = item.name,
                description = "Precio unitario: ${formatPrice(item.unitPriceCents)}",
                onEdit = { onEdit(item) },
                onDelete = { onDelete(item) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemListPreview() {
    PresupuestoTheme {
        ItemListScreen(
            items = List(5) { index ->
                sampleItem().copy(id = index, name = "Producto ${index + 1}")
            },
            onEdit = {},
            onDelete = {}
        )
    }
}
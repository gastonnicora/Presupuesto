package com.gastonnicora.presupuesto.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.model.sampleItem
import com.gastonnicora.presupuesto.ui.components.SearchBar
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme


/** Pantalla de productos: buscador, lista y botón para agregar uno nuevo. */
@Composable
fun ItemScreen(
    items: List<Item>,
    onEdit: (Item) -> Unit,
    onDelete: (Item) -> Unit,
    onAdd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val filtered = remember(items, query) {
        items.filter { it.name.contains(query, ignoreCase = true) }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .imePadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query,
                onQueryChange = { query = it },
                label = "Buscar producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ItemListScreen(
                items = filtered,
                onEdit = onEdit,
                onDelete = onDelete,
                modifier = Modifier.weight(1f)
            )
        }

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nuevo producto"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemScreenPreview() {
    PresupuestoTheme {
        ItemScreen(
            items = List(5) { index -> sampleItem().copy(id = index, name = "Ítem ${index + 1}") },
            onEdit = {},
            onDelete = {}
        )
    }
}
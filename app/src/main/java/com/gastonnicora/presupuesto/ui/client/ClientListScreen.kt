package com.gastonnicora.presupuesto.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.sampleClient
import com.gastonnicora.presupuesto.ui.components.ItemCard
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/** Lista de clientes. El padding inferior deja lugar para que el FAB no tape el último ítem. */
@Composable
fun ClientListScreen(
    clients: List<Client>,
    onEdit: (Client) -> Unit,
    onDelete: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clients, key = { it.id }) { client ->
            ItemCard(
                title = client.name,
                description = client.address,
                onEdit = { onEdit(client) },
                onDelete = { onDelete(client) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClientListPreview() {
    PresupuestoTheme {
        ClientListScreen(
            clients = List(5) { index ->
                sampleClient().copy(
                    id = index,
                    name = "Cliente ${index + 1}"
                )
            },
            onEdit = {},
            onDelete = {}
        )
    }
}
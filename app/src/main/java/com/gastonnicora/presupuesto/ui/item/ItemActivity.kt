package com.gastonnicora.presupuesto.ui.item

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.gastonnicora.presupuesto.database.DatabaseProvider
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Pantalla para mostrar los productos. Se actualiza sola cuando cambia la base. */
class ItemActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val itemDao = DatabaseProvider.get(applicationContext).itemDao()

        setContent {
            PresupuestoTheme {
                val items by itemDao.observeActive().collectAsState(initial = emptyList())

                // Producto pendiente de confirmar la eliminación. null = no hay diálogo.
                var itemToDelete by remember { mutableStateOf<Item?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Productos") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    ItemScreen(
                        items = items,
                        onEdit = { item ->
                            startActivity(
                                Intent(this@ItemActivity, ItemFormActivity::class.java)
                                    .putExtra(ItemFormActivity.EXTRA_ITEM_ID, item.id)
                            )
                        },
                        onDelete = { item -> itemToDelete = item },
                        onAdd = {
                            startActivity(Intent(this@ItemActivity, ItemFormActivity::class.java))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                itemToDelete?.let { item ->
                    AlertDialog(
                        onDismissRequest = { itemToDelete = null },
                        title = { Text("Eliminar producto") },
                        text = { Text("¿Seguro que querés eliminar ${item.name}?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            itemDao.update(item.copy(active = false))
                                        }
                                    }
                                    itemToDelete = null
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { itemToDelete = null }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
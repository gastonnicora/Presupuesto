package com.gastonnicora.presupuesto.ui.client

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
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Pantalla para mostrar los clientes. Se actualiza sola cuando cambia la base. */
class ClientActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val clientDao = DatabaseProvider.get(applicationContext).clientDao()

        setContent {
            PresupuestoTheme {
                val clients by clientDao.observeActive().collectAsState(initial = emptyList())

                // Cliente pendiente de confirmar la eliminación. null = no hay diálogo.
                var clientToDelete by remember { mutableStateOf<Client?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Clientes") },
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
                    ClientScreen(
                        clients = clients,
                        onEdit = { client ->
                            startActivity(
                                Intent(this@ClientActivity, ClientFormActivity::class.java)
                                    .putExtra(ClientFormActivity.EXTRA_CLIENT_ID, client.id)
                            )
                        },
                        onDelete = { client -> clientToDelete = client },
                        onAdd = {
                            startActivity(
                                Intent(
                                    this@ClientActivity,
                                    ClientFormActivity::class.java
                                )
                            )
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                clientToDelete?.let { client ->
                    AlertDialog(
                        onDismissRequest = { clientToDelete = null },
                        title = { Text("Eliminar cliente") },
                        text = { Text("¿Seguro que querés eliminar a ${client.name}?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            clientDao.update(client.copy(active = false))
                                        }
                                    }
                                    clientToDelete = null
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { clientToDelete = null }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
package com.gastonnicora.presupuesto.ui.client

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.gastonnicora.presupuesto.database.DatabaseProvider
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.storage.createDirectory
import com.gastonnicora.presupuesto.storage.moveDirectory
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Alta y edición de clientes. Sin EXTRA_CLIENT_ID es alta; con id, es edición. */
class ClientFormActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CLIENT_ID = "client_id"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val clientDao = DatabaseProvider.get(applicationContext).clientDao()
        val clientId = intent.getIntExtra(EXTRA_CLIENT_ID, -1).takeIf { it != -1 }
        val isEdit = clientId != null

        setContent {
            PresupuestoTheme {
                var initial by remember { mutableStateOf<Client?>(null) }
                // En alta no hay nada que cargar, así que el formulario se muestra de una vez
                var loaded by remember { mutableStateOf(!isEdit) }

                LaunchedEffect(clientId) {
                    if (clientId != null) {
                        initial = clientDao.findById(clientId)
                        loaded = true
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (isEdit) "Editar cliente" else "Crear cliente") },
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
                    if (loaded) {
                        ClientFormScreen(
                            initial = initial,
                            onSave = { client ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        if (isEdit) {
                                            clientDao.update(client)
                                        } else {
                                            val id = clientDao.insert(client).toInt()
                                            createDirectory(
                                                context = applicationContext,
                                                "$id"
                                            )
                                        }
                                        finish()
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
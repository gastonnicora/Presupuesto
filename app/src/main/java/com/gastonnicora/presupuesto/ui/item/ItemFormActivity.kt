package com.gastonnicora.presupuesto.ui.item

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
import com.gastonnicora.presupuesto.model.Item
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Alta y edición de productos. Sin EXTRA_ITEM_ID es alta; con id, es edición. */
class ItemFormActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ITEM_ID = "item_id"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val itemDao = DatabaseProvider.get(applicationContext).itemDao()
        val itemId = intent.getIntExtra(EXTRA_ITEM_ID, -1).takeIf { it != -1 }
        val isEdit = itemId != null

        setContent {
            PresupuestoTheme {
                var initial by remember { mutableStateOf<Item?>(null) }
                // En alta no hay nada que cargar, así que el formulario se muestra de una vez
                var loaded by remember { mutableStateOf(!isEdit) }

                LaunchedEffect(itemId) {
                    if (itemId != null) {
                        val found = itemDao.findById(itemId)
                        if (found == null) {
                            // El producto ya no existe (fue eliminado): cerramos la pantalla
                            finish()
                        } else {
                            initial = found
                            loaded = true
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (isEdit) "Editar producto" else "Crear producto") },
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
                        ItemFormScreen(
                            initial = initial,
                            onSave = { item ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        if (isEdit) {
                                            itemDao.update(item)
                                        } else {
                                            itemDao.insertAll(item)
                                        }
                                    }
                                    // Ya de vuelta en el hilo principal
                                    finish()
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

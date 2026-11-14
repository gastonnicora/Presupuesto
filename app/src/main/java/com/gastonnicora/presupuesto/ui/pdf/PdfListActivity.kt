package com.gastonnicora.presupuesto.ui.pdf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.gastonnicora.presupuesto.database.DatabaseProvider
import com.gastonnicora.presupuesto.storage.*
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfListActivity : ComponentActivity() {
    private val clientDao by lazy { DatabaseProvider.get(applicationContext).clientDao() }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PresupuestoTheme {
                var pdfs by remember { mutableStateOf<List<PdfEntry>>(emptyList()) }
                var clientNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

                fun recargar() {
                    lifecycleScope.launch {
                        pdfs = listAllPdfs(applicationContext)
                        // Ajusta `name` al campo real de Client
                        clientNames = clientDao.observeActive().first()
                            .associate { it.id.toString() to it.name }
                    }
                }

                LaunchedEffect(Unit) { recargar() }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Presupuestos PDF") },
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
                    PdfListContent(
                        pdfs = pdfs,
                        clientNames = clientNames,
                        onOpen = { openPdf(this@PdfListActivity, it.file) },
                        onShare = { sharePdf(this@PdfListActivity, it.file) },
                        onDelete = { entry ->
                            val idPresupuesto = entry.fileName
                                .removePrefix("presupuesto_")
                                .removeSuffix(".pdf")
                            deletePdf(applicationContext, entry.clientId, idPresupuesto)
                            recargar()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PdfListContent(
    pdfs: List<PdfEntry>,
    clientNames: Map<String, String>,
    onOpen: (PdfEntry) -> Unit,
    onShare: (PdfEntry) -> Unit,
    onDelete: (PdfEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pdfs.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Todavía no hay presupuestos guardados")
        }
        return
    }

    var aBorrar by remember { mutableStateOf<PdfEntry?>(null) }
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "AR")) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pdfs, key = { it.file.absolutePath }) { entry ->
            Card(
                onClick = { onOpen(entry) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = clientNames[entry.clientId] ?: "Cliente ${entry.clientId}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("")
                        Text(
                            text = formato.format(Date(entry.modified)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = { onShare(entry) }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    IconButton(onClick = { aBorrar = entry }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar")
                    }
                }
            }
        }
    }

    aBorrar?.let { entry ->
        AlertDialog(
            onDismissRequest = { aBorrar = null },
            title = { Text("Borrar presupuesto") },
            text = { Text("Se borrará el PDF ${entry.fileName}. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(entry)
                    aBorrar = null
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { aBorrar = null }) { Text("Cancelar") }
            }
        )
    }
}
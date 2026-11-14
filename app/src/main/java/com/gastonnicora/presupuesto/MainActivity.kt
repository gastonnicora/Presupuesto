package com.gastonnicora.presupuesto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.gastonnicora.presupuesto.data.BackupManager
import com.gastonnicora.presupuesto.data.CompanyRepository
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.ui.budget.BudgetActivity
import com.gastonnicora.presupuesto.ui.client.ClientActivity
import com.gastonnicora.presupuesto.ui.company.CompanyActivity
import com.gastonnicora.presupuesto.ui.home.HomeScreen
import com.gastonnicora.presupuesto.ui.item.ItemActivity
import com.gastonnicora.presupuesto.ui.pdf.PdfListActivity
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.jvm.java

class MainActivity : ComponentActivity() {

    private val companyRepository by lazy { CompanyRepository(applicationContext) }

    // null mientras se cargan los datos
    private var company by mutableStateOf<CompanyData?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PresupuestoTheme {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Presupuesto") }) }
                ) { innerPadding ->
                    company?.let { loaded ->
                        HomeScreen(
                            company = loaded,
                            onEditCompany = {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        CompanyActivity::class.java
                                    )
                                )
                            },
                            onOpenClients = {
                                startActivity(Intent(this@MainActivity, ClientActivity::class.java))
                            },
                            onOpenItems = {
                                startActivity(Intent(this@MainActivity, ItemActivity::class.java))
                            },
                            onOpenBudget = {
                                startActivity(Intent(this@MainActivity, BudgetActivity::class.java))
                            },
                            onOpenPDF = {
                                startActivity(Intent(this, PdfListActivity::class.java))
                            },
                            onExportCopy = { exportLauncher.launch("presupuesto_${LocalDate.now()}.db") },
                            onImportCopy = { importLauncher.launch(arrayOf("*/*")) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Corre al abrir la app y al volver desde CompanyActivity
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val loaded = companyRepository.companyData.first()
                if (loaded.isValid()) {
                    company = loaded
                } else {
                    startActivity(Intent(this@MainActivity, CompanyActivity::class.java))
                }
            }
        }
    }

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { exportarCopia(it) } }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { importarCopia(it) } }

    private fun exportarCopia(uri: Uri) {
        lifecycleScope.launch {
            BackupManager.exportar(this@MainActivity, uri)
                .onSuccess { mostrar("Copia guardada") }
                .onFailure { mostrar("Error al exportar: ${it.message}") }
        }
    }

    private fun importarCopia(uri: Uri) {
        lifecycleScope.launch {
            BackupManager.importar(this@MainActivity, uri)
                .onSuccess { mostrar("Datos restaurados") }
                .onFailure { mostrar("Error al importar: ${it.message}") }
        }
    }

    private fun mostrar(texto: String) =
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
}

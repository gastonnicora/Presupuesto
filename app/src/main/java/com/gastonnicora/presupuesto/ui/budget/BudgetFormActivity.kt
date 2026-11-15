package com.gastonnicora.presupuesto.ui.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.gastonnicora.presupuesto.dao.BudgetLine
import com.gastonnicora.presupuesto.data.CompanyRepository
import com.gastonnicora.presupuesto.database.DatabaseProvider
import com.gastonnicora.presupuesto.model.Budget
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.model.loadLogo
import com.gastonnicora.presupuesto.storage.deletePdf
import com.gastonnicora.presupuesto.storage.savePdf
import com.gastonnicora.presupuesto.storage.sharePdf
import com.gastonnicora.presupuesto.ui.pdf.generatePDF
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Alta y edición de presupuestos. Sin EXTRA_BUDGET_ID es alta; con id, es edición. */
class BudgetFormActivity : ComponentActivity() {
    private val companyRepository by lazy { CompanyRepository(applicationContext) }

    companion object {
        const val EXTRA_BUDGET_ID = "budget_id"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.get(applicationContext)
        val budgetDao = db.budgetDao()
        val clientDao = db.clientDao()
        val itemDao = db.itemDao()

        val budgetId = intent.getIntExtra(EXTRA_BUDGET_ID, -1).takeIf { it != -1 }
        val isEdit = budgetId != null

        setContent {
            PresupuestoTheme {
                var formData by remember { mutableStateOf<BudgetFormData?>(null) }

                // Los productos se observan aparte: el formulario aparece sin esperar a cargarlos
                val products by itemDao.observeActive().collectAsState(initial = emptyList())

                LaunchedEffect(budgetId) {
                    // Todas las consultas corren en paralelo en lugar de una tras otra
                    coroutineScope {
                        val clients = async { clientDao.observeActive().first() }
                        val budget = async { budgetId?.let { budgetDao.findById(it) } }
                        val lines = async {
                            budgetId?.let { budgetDao.observeLines(it).first() }.orEmpty()
                        }
                        val company = async { companyRepository.companyData.first() }
                        val logo = async { loadLogo(company.await().logoPath) }

                        formData = BudgetFormData(
                            clients = clients.await(),
                            budget = budget.await(),
                            lines = lines.await(),
                            company = company.await(),
                            logo = logo.await()
                        )
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (isEdit) "Editar presupuesto" else "Nuevo presupuesto") },
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
                    val data = formData
                    if (data != null) {
                        BudgetFormScreen(
                            clients = data.clients,
                            products = products,
                            initial = data.budget,
                            initialLines = data.lines,
                            onSave = { budget, lines ->
                                lifecycleScope.launch {

                                    withContext(Dispatchers.IO) {
                                        val id = budgetDao.saveBudget(budget, lines, budgetId)

                                        // En edición, borra el PDF del cliente original (puede ser distinto si cambió)
                                        data.budget?.let { original ->
                                            deletePdf(applicationContext, original.clientId, "$id")
                                        }

                                        // El cliente se lee al guardar porque puede haber cambiado
                                        val client = clientDao.findById(budget.clientId)!!
                                        val items = budgetDao.observeLines(id)
                                        val bud = budget.copy(id = id)
                                        val document = generatePDF(
                                            data.logo,
                                            bud,
                                            data.company,
                                            client,
                                            items.first()
                                        )
                                        val archivo = savePdf(
                                            applicationContext,
                                            client.id,
                                            "$id",
                                            document.toByteArray()
                                        )
                                        sharePdf(this@BudgetFormActivity, archivo)
                                        finish()
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

}

private data class BudgetFormData(
    val clients: List<Client>,
    val budget: Budget?,
    val lines: List<BudgetLine>,
    val company: CompanyData,
    val logo: ByteArray?
)
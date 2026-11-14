package com.gastonnicora.presupuesto.ui.budget

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
import com.gastonnicora.presupuesto.storage.deletePdf
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Pantalla de presupuestos. Se actualiza sola cuando cambia la base. */
class BudgetActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val budgetDao = DatabaseProvider.get(applicationContext).budgetDao()

        setContent {
            PresupuestoTheme {
                val budgets by budgetDao.observeSummaries().collectAsState(initial = emptyList())

                // Presupuesto pendiente de confirmar la eliminación. null = no hay diálogo.
                var budgetToDelete by remember { mutableStateOf<Int?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Presupuestos") },
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
                    BudgetScreen(
                        budgets = budgets,
                        onEdit = { budget ->
                            startActivity(
                                Intent(this@BudgetActivity, BudgetFormActivity::class.java)
                                    .putExtra(BudgetFormActivity.EXTRA_BUDGET_ID, budget)
                            )
                        },
                        onDelete = { budget -> budgetToDelete = budget },
                        onAdd = {
                            startActivity(
                                Intent(
                                    this@BudgetActivity,
                                    BudgetFormActivity::class.java
                                )
                            )
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                budgetToDelete?.let { budget ->
                    AlertDialog(
                        onDismissRequest = { budgetToDelete = null },
                        title = { Text("Eliminar presupuesto") },
                        text = { Text("¿Seguro que querés eliminar el presupuesto?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            val bud = budgetDao.findById(budget)
                                            deletePdf(
                                                applicationContext,
                                                "${bud!!.clientId}",
                                                "$budget"
                                            )
                                            budgetDao.deleteById(budget)
                                        }
                                    }
                                    budgetToDelete = null
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { budgetToDelete = null }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
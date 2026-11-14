package com.gastonnicora.presupuesto.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.dao.BudgetSummary
import com.gastonnicora.presupuesto.model.SalesCondition
import com.gastonnicora.presupuesto.ui.components.DatePickerSheet
import com.gastonnicora.presupuesto.ui.components.ItemCard
import com.gastonnicora.presupuesto.ui.components.SearchBar
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Convierte "dd/MM/yyyy" a LocalDate. Devuelve null si el texto no tiene ese formato. */
private fun parseDate(text: String): LocalDate? =
    try {
        LocalDate.parse(text, DATE_FORMAT)
    } catch (e: DateTimeParseException) {
        null
    }

/** Pantalla de presupuestos: buscador, filtro por fechas, lista y botón para agregar uno nuevo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    budgets: List<BudgetSummary>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    val from = fromDate
    val to = toDate

    val filtered = budgets.filter { budget ->
        val date = parseDate(budget.date)
        val matchesText = budget.clientName.contains(query, ignoreCase = true)
        val afterFrom = from == null || (date != null && !date.isBefore(from))
        val beforeTo = to == null || (date != null && !date.isAfter(to))
        matchesText && afterFrom && beforeTo
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                label = "Buscar cliente",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showFromPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(from?.format(DATE_FORMAT) ?: "Desde")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { showToPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(to?.format(DATE_FORMAT) ?: "Hasta")
                }
                if (from != null || to != null) {
                    IconButton(onClick = {
                        fromDate = null
                        toDate = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar fechas"
                        )
                    }
                }
            }

            BudgetListScreen(
                budgets = filtered,
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
                contentDescription = "Nuevo presupuesto"
            )
        }
    }

    if (showFromPicker) {
        DatePickerSheet(
            initial = from,
            maxDate = LocalDate.now(),
            onConfirm = {
                fromDate = it
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        DatePickerSheet(
            initial = to,
            maxDate = LocalDate.now(),
            onConfirm = {
                toDate = it
                showToPicker = false
            },
            onDismiss = { showToPicker = false }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BudgetScreenPreview() {
    PresupuestoTheme {
        BudgetScreen(
            budgets = List(5) { index ->
                BudgetSummary(
                    budgetId = index,
                    date = "27/03/2026",
                    clientName = "Juan Pérez",
                    salesCondition = "CASH",
                    total = 402334
                )
            },
            onEdit = {},
            onDelete = {}
        )
    }
}
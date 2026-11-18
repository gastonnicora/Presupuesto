package com.gastonnicora.presupuesto.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.dao.BudgetSummary
import com.gastonnicora.presupuesto.model.SalesCondition
import com.gastonnicora.presupuesto.model.formatPrice
import com.gastonnicora.presupuesto.ui.components.ItemCard
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

/** Lista de productos. El padding inferior deja lugar para que el FAB no tape el último ítem. */
@Composable
fun BudgetListScreen(
    budgets: List<BudgetSummary>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(budgets, key = { it.budgetId }) { budget ->
            ItemCard(
                title = budget.clientName,
                description = "${budget.date} · ${formatPrice(budget.total)}",
                onEdit = { onEdit(budget.budgetId) },
                onDelete = { onDelete(budget.budgetId) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BudgetListPreview() {
    PresupuestoTheme {
        BudgetListScreen(
            budgets = List(5) { index ->
                BudgetSummary(
                    index, "27/03/20206", "Juan Perez",
                    SalesCondition.CASH.label, 2333300L
                )
            },
            onEdit = {},
            onDelete = {}
        )
    }
}
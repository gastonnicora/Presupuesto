package com.gastonnicora.presupuesto.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import com.gastonnicora.presupuesto.model.Budget
import com.gastonnicora.presupuesto.model.BudgetItem
import kotlinx.coroutines.flow.Flow

/** Línea de un presupuesto: un producto con su cantidad. Resultado del join, no es una tabla. */
data class BudgetLine(
    val itemId: Int,
    val name: String,
    val unitPriceCents: Long,
    val quantity: Int
) {
    val subtotalCents: Long get() = unitPriceCents * quantity
}

/** Presupuesto con el nombre del cliente. Resultado del join, no es una tabla. */
data class BudgetSummary(
    val budgetId: Int,
    val date: String,
    val clientName: String,
    val salesCondition: String,
    val total: Long
)

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun findById(id: Int): Budget?

    /** Productos de un presupuesto, con nombre y precio del producto. */
    @Query(
        """
        SELECT i.id AS itemId,
               i.name AS name,
               i.unit_price_cents AS unitPriceCents,
               bi.quantity AS quantity
        FROM budget_items bi
        INNER JOIN items i ON i.id = bi.item_id
        WHERE bi.budget_id = :budgetId
        ORDER BY i.name
        """
    )
    fun observeLines(budgetId: Int): Flow<List<BudgetLine>>

    /** Devuelve el id generado para el presupuesto nuevo. */
    @Insert
    suspend fun insertBudget(budget: Budget): Long

    @Insert
    suspend fun insertBudgetItems(budgetItems: List<BudgetItem>)

    @Query("DELETE FROM budget_items WHERE budget_id = :budgetId")
    suspend fun deleteLines(budgetId: Int)

    /** Al borrar el presupuesto, Room borra sus líneas en cascada (ON DELETE CASCADE). */
    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Int)

    /** Presupuestos con el nombre del cliente, del más reciente al más antiguo. */
    @Query(
        """
        SELECT b.id AS budgetId,
               b.date AS date,
               c.name AS clientName,
               b.sales_condition AS salesCondition,
               b.total as total
        FROM budgets b
        INNER JOIN clients c ON c.id = b.client_id
        ORDER BY b.id DESC
        """
    )
    fun observeSummaries(): Flow<List<BudgetSummary>>


    @Transaction
    suspend fun saveBudget(budget: Budget, lines: List<BudgetLine>, existingId: Int?): Int {
        if (existingId == null) {
            val newId = insertBudget(budget).toInt()
            insertBudgetItems(lines.map {
                BudgetItem(
                    budgetId = newId,
                    itemId = it.itemId,
                    quantity = it.quantity
                )
            })
            return newId
        } else {
            update(budget.copy(id = existingId))
            deleteLines(existingId)
            insertBudgetItems(lines.map {
                BudgetItem(
                    budgetId = existingId,
                    itemId = it.itemId,
                    quantity = it.quantity
                )
            })
            return existingId
        }
    }

    @Update
    suspend fun update(budget: Budget)

    /**
     * uso:
     * val budgetId = budgetDao.insertBudget(
     *     Budget(date = date, salesCondition = condition, clientId = client.id)
     * ).toInt()
     *
     * budgetDao.insertBudgetItems(
     *     lines.map { BudgetItem(budgetId = budgetId, itemId = it.itemId, quantity = it.quantity) }
     * )
     */
}
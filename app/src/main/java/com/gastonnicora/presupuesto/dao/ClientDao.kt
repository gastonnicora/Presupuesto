package com.gastonnicora.presupuesto.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.gastonnicora.presupuesto.model.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    /** Clientes activos ordenados por nombre. Emite una lista nueva cada vez que cambia la tabla. */
    @Query("SELECT * FROM clients WHERE active = 1 ORDER BY name")
    fun observeActive(): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun findById(id: Int): Client?

    @Query("SELECT * FROM clients WHERE id IN (:clientIds)")
    suspend fun loadAllByIds(clientIds: IntArray): List<Client>

    // Pasar el patrón con comodines, por ejemplo "%Juan%"
    @Query("SELECT * FROM clients WHERE name LIKE :name AND active = 1 LIMIT 1")
    suspend fun findByName(name: String): Client?

    @Insert
    suspend fun insert(client: Client): Long

    @Update
    suspend fun update(client: Client)

    @Delete
    suspend fun delete(client: Client)
}
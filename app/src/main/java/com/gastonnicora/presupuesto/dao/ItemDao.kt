package com.gastonnicora.presupuesto.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.gastonnicora.presupuesto.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    /** Ítems ordenados por nombre. Emite una lista nueva cada vez que cambia la tabla. */
    @Query("SELECT * FROM items WHERE active = 1 ORDER BY name")
    fun observeActive(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun findById(id: Int): Item?

    @Query("SELECT * FROM items WHERE id IN (:itemIds)")
    suspend fun loadAllByIds(itemIds: IntArray): List<Item>

    // Pasar el patrón con comodines, por ejemplo "%alambre%"
    @Query("SELECT * FROM items WHERE name LIKE :name ORDER BY name")
    suspend fun searchByName(name: String): List<Item>

    @Insert
    suspend fun insertAll(vararg items: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
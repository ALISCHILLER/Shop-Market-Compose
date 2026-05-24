package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.ProductModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productModelEntity: List<ProductModelEntity>)

    @Query("DELETE FROM Product")
    fun delete()

    @Query("SELECT * FROM Product ORDER BY productName ASC")
    fun getAll(): Flow<List<ProductModelEntity>>

    @Query(
        """
        SELECT * FROM Product
        WHERE productGroupCode = :code
        ORDER BY productName ASC
        """
    )
    fun getProduct(code: Int): Flow<List<ProductModelEntity>>

    @Query("SELECT COUNT(*) FROM Product")
    fun getProductCount(): Int

    @Query(
        """
        SELECT * FROM Product
        WHERE productName LIKE '%' || :searchQuery || '%'
           OR CAST(productCode AS TEXT) LIKE '%' || :searchQuery || '%'
        ORDER BY productName ASC
        """
    )
    fun searchProducts(searchQuery: String): Flow<List<ProductModelEntity>>

    @Query(
        """
        SELECT * FROM Product
        WHERE (:groupCode = 99 OR productGroupCode = :groupCode)
          AND (
                :searchQuery = ''
                OR productName LIKE '%' || :searchQuery || '%'
                OR CAST(productCode AS TEXT) LIKE '%' || :searchQuery || '%'
          )
        ORDER BY productName ASC
        """
    )
    fun observeProducts(
        groupCode: Int,
        searchQuery: String
    ): Flow<List<ProductModelEntity>>
}

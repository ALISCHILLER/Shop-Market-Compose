package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.ProductGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productGroupEntity: List<ProductGroupEntity>)

    @Query("DELETE FROM ProductGroup")
    fun delete()

    @Query(
        """
        SELECT * FROM ProductGroup
        WHERE productCategoryCode NOT IN (0, 99)
        ORDER BY productCategoryCode ASC
        """
    )
    fun getAll(): Flow<List<ProductGroupEntity>>

    @Query("SELECT COUNT(*) FROM ProductGroup")
    fun getProductGroupCount(): Int

    @Query(
        """
        INSERT OR REPLACE INTO ProductGroup (
            productCategoryCode,
            productCategoryName,
            productCategoryImage,
            productCategoryImageUnselect
        )
        VALUES (99, 'همه', NULL, NULL)
        """
    )
    suspend fun insertZeroItem()
}
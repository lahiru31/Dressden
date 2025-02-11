package com.dressden.app.data.local.dao

import androidx.room.*
import com.dressden.app.data.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavoriteProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): Product?

    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("""
        UPDATE products 
        SET isFavorite = :isFavorite 
        WHERE id = :productId
    """)
    suspend fun updateFavoriteStatus(productId: String, isFavorite: Boolean)

    @Query("""
        UPDATE products 
        SET stockQuantity = :quantity 
        WHERE id = :productId
    """)
    suspend fun updateStockQuantity(productId: String, quantity: Int)

    @Query("""
        SELECT * FROM products 
        WHERE category = :category 
        AND price BETWEEN :minPrice AND :maxPrice
    """)
    fun getFilteredProducts(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<Product>>

    @Query("""
        SELECT DISTINCT category 
        FROM products 
        ORDER BY category ASC
    """)
    fun getAllCategories(): Flow<List<String>>

    @Query("""
        SELECT * FROM products 
        WHERE discount > 0 
        AND (discountEndDate IS NULL OR discountEndDate > :currentTime)
    """)
    fun getProductsOnSale(currentTime: Long): Flow<List<Product>>

    @Query("""
        SELECT * FROM products 
        WHERE inStock = 1 
        AND stockQuantity > 0 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getNewArrivals(limit: Int): Flow<List<Product>>

    @Transaction
    @Query("""
        SELECT * FROM products 
        WHERE id IN 
        (SELECT productId FROM orders_products WHERE orderId = :orderId)
    """)
    fun getProductsInOrder(orderId: String): Flow<List<Product>>

    @Query("""
        SELECT COUNT(*) 
        FROM products 
        WHERE category = :category
    """)
    suspend fun getProductCountInCategory(category: String): Int

    @Query("SELECT MIN(price) FROM products")
    suspend fun getMinPrice(): Double

    @Query("SELECT MAX(price) FROM products")
    suspend fun getMaxPrice(): Double
}

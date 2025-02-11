package com.dressden.app.data.local.dao

import androidx.room.*
import com.dressden.app.data.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): Product?

    @Query("SELECT * FROM products WHERE category_id = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>

    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        OR brand LIKE '%' || :query || '%'
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE is_featured = 1")
    fun getFeaturedProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE is_new_arrival = 1")
    fun getNewArrivals(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE is_bestseller = 1")
    fun getBestsellers(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE stock_quantity <= :threshold")
    fun getLowStockProducts(threshold: Int = Product.MIN_STOCK_THRESHOLD): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("DELETE FROM products WHERE category_id = :categoryId")
    suspend fun deleteProductsByCategory(categoryId: String)

    @Transaction
    @Query("""
        SELECT * FROM products 
        WHERE category_id = :categoryId 
        ORDER BY 
            CASE 
                WHEN :sortBy = 'price' AND :sortOrder = 'asc' THEN price 
                WHEN :sortBy = 'price' AND :sortOrder = 'desc' THEN -price
                WHEN :sortBy = 'name' AND :sortOrder = 'asc' THEN name
                WHEN :sortBy = 'name' AND :sortOrder = 'desc' THEN -name
                WHEN :sortBy = 'rating' THEN -rating
                ELSE created_at
            END
    """)
    fun getProductsByCategorySorted(
        categoryId: String,
        sortBy: String,
        sortOrder: String
    ): Flow<List<Product>>

    @Query("""
        SELECT * FROM products 
        WHERE price BETWEEN :minPrice AND :maxPrice
        AND (:brand IS NULL OR brand = :brand)
        AND (:size IS NULL OR :size IN (
            SELECT json_each.value 
            FROM json_each(available_sizes)
        ))
        AND (:color IS NULL OR :color IN (
            SELECT json_each.value 
            FROM json_each(available_colors)
        ))
    """)
    fun filterProducts(
        minPrice: Double,
        maxPrice: Double,
        brand: String? = null,
        size: String? = null,
        color: String? = null
    ): Flow<List<Product>>

    @Query("""
        SELECT DISTINCT brand 
        FROM products 
        WHERE brand IS NOT NULL 
        ORDER BY brand ASC
    """)
    fun getAllBrands(): Flow<List<String>>

    @Query("""
        SELECT DISTINCT json_each.value 
        FROM products, json_each(products.available_sizes) 
        ORDER BY json_each.value ASC
    """)
    fun getAllSizes(): Flow<List<String>>

    @Query("""
        SELECT DISTINCT json_each.value 
        FROM products, json_each(products.available_colors) 
        ORDER BY json_each.value ASC
    """)
    fun getAllColors(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("SELECT AVG(rating) FROM products WHERE category_id = :categoryId")
    suspend fun getAverageCategoryRating(categoryId: String): Float

    @Query("""
        SELECT * FROM products 
        WHERE id IN (
            SELECT product_id 
            FROM (
                SELECT product_id, COUNT(*) as order_count 
                FROM orders 
                GROUP BY product_id 
                ORDER BY order_count DESC 
                LIMIT :limit
            )
        )
    """)
    fun getMostOrderedProducts(limit: Int = 10): Flow<List<Product>>

    companion object {
        private const val TAG = "ProductDao"
    }
}

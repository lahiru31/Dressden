package com.dressden.app.data.repository

import androidx.room.withTransaction
import com.dressden.app.data.api.ApiService
import com.dressden.app.data.local.AppDatabase
import com.dressden.app.data.local.dao.ProductDao
import com.dressden.app.data.models.Product
import com.dressden.app.utils.Resource
import com.dressden.app.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    private val db: AppDatabase,
    private val productDao: ProductDao
) {
    fun getProducts(
        category: String? = null,
        query: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        forceRefresh: Boolean = false
    ) = networkBoundResource(
        query = {
            when {
                !query.isNullOrEmpty() -> productDao.searchProducts(query)
                !category.isNullOrEmpty() && !sortBy.isNullOrEmpty() -> {
                    productDao.getProductsByCategorySorted(category, sortBy, sortOrder ?: "asc")
                }
                !category.isNullOrEmpty() -> productDao.getProductsByCategory(category)
                else -> productDao.getAllProducts()
            }
        },
        fetch = {
            apiService.getProducts(
                token = "Bearer ${getAuthToken()}",
                category = category,
                query = query,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
        },
        saveFetchResult = { response ->
            db.withTransaction {
                if (!category.isNullOrEmpty()) {
                    productDao.deleteProductsByCategory(category)
                } else {
                    productDao.deleteAllProducts()
                }
                productDao.insertProducts(response.body() ?: emptyList())
            }
        },
        shouldFetch = { cachedProducts ->
            forceRefresh || cachedProducts.isEmpty()
        }
    )

    suspend fun getProduct(productId: String): Resource<Product> {
        return try {
            // Try to get from local database first
            val localProduct = productDao.getProductById(productId)
            if (localProduct != null) {
                Resource.Success(localProduct)
            } else {
                // Fetch from network if not in local database
                val response = apiService.getProduct(
                    token = "Bearer ${getAuthToken()}",
                    productId = productId
                )
                if (response.isSuccessful) {
                    val product = response.body()!!
                    productDao.insertProduct(product)
                    Resource.Success(product)
                } else {
                    Resource.Error("Failed to fetch product details")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    fun getFeaturedProducts() = networkBoundResource(
        query = { productDao.getFeaturedProducts() },
        fetch = {
            apiService.getFeaturedProducts("Bearer ${getAuthToken()}")
        },
        saveFetchResult = { response ->
            db.withTransaction {
                productDao.insertProducts(response.body() ?: emptyList())
            }
        }
    )

    fun getNewArrivals() = networkBoundResource(
        query = { productDao.getNewArrivals() },
        fetch = {
            apiService.getNewArrivals("Bearer ${getAuthToken()}")
        },
        saveFetchResult = { response ->
            db.withTransaction {
                productDao.insertProducts(response.body() ?: emptyList())
            }
        }
    )

    fun getBestsellers(): Flow<List<Product>> = productDao.getBestsellers()

    fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()

    suspend fun filterProducts(
        minPrice: Double,
        maxPrice: Double,
        brand: String? = null,
        size: String? = null,
        color: String? = null
    ): Flow<List<Product>> {
        return productDao.filterProducts(minPrice, maxPrice, brand, size, color)
    }

    fun getAllBrands(): Flow<List<String>> = productDao.getAllBrands()

    fun getAllSizes(): Flow<List<String>> = productDao.getAllSizes()

    fun getAllColors(): Flow<List<String>> = productDao.getAllColors()

    suspend fun getAverageCategoryRating(categoryId: String): Float {
        return productDao.getAverageCategoryRating(categoryId)
    }

    suspend fun getMostOrderedProducts(limit: Int = 10): List<Product> {
        return productDao.getMostOrderedProducts(limit).first()
    }

    suspend fun addReview(
        productId: String,
        rating: Float,
        comment: String
    ): Resource<Product> {
        return try {
            val response = apiService.addReview(
                token = "Bearer ${getAuthToken()}",
                productId = productId,
                rating = rating,
                comment = comment
            )
            if (response.isSuccessful) {
                val updatedProduct = response.body()!!
                productDao.updateProduct(updatedProduct)
                Resource.Success(updatedProduct)
            } else {
                Resource.Error("Failed to add review")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    private fun getAuthToken(): String {
        // This should be implemented to get the auth token from your auth manager
        return ""
    }

    companion object {
        private const val TAG = "ProductRepository"
    }
}

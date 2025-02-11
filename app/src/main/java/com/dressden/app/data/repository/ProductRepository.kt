package com.dressden.app.data.repository

import com.dressden.app.data.api.ApiService
import com.dressden.app.data.local.dao.ProductDao
import com.dressden.app.data.models.Product
import com.dressden.app.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val apiService: ApiService
) {
    // Existing methods...

    // Method to fetch products from the MySQL database
    suspend fun fetchProducts(): Result<List<Product>> {
        return try {
            val products = apiService.getProducts() // Assuming this method exists in ApiService
            productDao.insertProducts(products) // Save to local database
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.dressden.app.data.api

import com.dressden.app.data.models.Product
import com.dressden.app.data.models.User
import retrofit2.http.*

interface ApiService {
    // Existing methods...

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Product

    @POST("users")
    suspend fun createUserProfile(@Body user: User): User

    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") userId: String): User
}

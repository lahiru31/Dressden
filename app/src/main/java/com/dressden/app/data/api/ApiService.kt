package com.dressden.app.data.api

import com.dressden.app.data.models.Cart
import com.dressden.app.data.models.Order
import com.dressden.app.data.models.Product
import com.dressden.app.data.models.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("token") token: String
    ): Response<User>

    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("token") token: String
    ): Response<User>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>

    // User Profile
    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<User>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String?,
        @Field("phone") phone: String?,
        @Field("address") address: Map<String, String>?
    ): Response<User>

    @PUT("users/fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Field("fcm_token") fcmToken: String
    ): Response<Unit>

    // Products
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null,
        @Query("query") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("sort_by") sortBy: String? = null,
        @Query("sort_order") sortOrder: String? = null
    ): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: String
    ): Response<Product>

    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Header("Authorization") token: String
    ): Response<List<Product>>

    @GET("products/new-arrivals")
    suspend fun getNewArrivals(
        @Header("Authorization") token: String
    ): Response<List<Product>>

    // Cart
    @GET("cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<Cart>

    @POST("cart/items")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Field("product_id") productId: String,
        @Field("quantity") quantity: Int,
        @Field("size") size: String?,
        @Field("color") color: String?
    ): Response<Cart>

    @PUT("cart/items/{product_id}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String,
        @Field("quantity") quantity: Int
    ): Response<Cart>

    @DELETE("cart/items/{product_id}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String
    ): Response<Cart>

    @POST("cart/apply-coupon")
    suspend fun applyCoupon(
        @Header("Authorization") token: String,
        @Field("code") couponCode: String
    ): Response<Cart>

    @POST("cart/remove-coupon")
    suspend fun removeCoupon(
        @Header("Authorization") token: String
    ): Response<Cart>

    // Orders
    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<Order>

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Field("payment_method") paymentMethod: String,
        @Field("shipping_address") shippingAddress: Map<String, String>,
        @Field("billing_address") billingAddress: Map<String, String>
    ): Response<Order>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: String,
        @Field("reason") reason: String
    ): Response<Order>

    // Wishlist
    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") token: String
    ): Response<List<Product>>

    @POST("wishlist/{product_id}")
    suspend fun addToWishlist(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String
    ): Response<Unit>

    @DELETE("wishlist/{product_id}")
    suspend fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String
    ): Response<Unit>

    // Reviews
    @POST("products/{id}/reviews")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Path("id") productId: String,
        @Field("rating") rating: Float,
        @Field("comment") comment: String
    ): Response<Product>

    // Search
    @GET("search/suggestions")
    suspend fun getSearchSuggestions(
        @Header("Authorization") token: String,
        @Query("query") query: String
    ): Response<List<String>>
}

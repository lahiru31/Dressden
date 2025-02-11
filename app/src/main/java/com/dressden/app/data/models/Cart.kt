package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("items")
    val items: List<CartItem>,

    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    data class CartItem(
        @SerializedName("productId")
        val productId: String,

        @SerializedName("quantity")
        val quantity: Int,

        @SerializedName("size")
        val size: String,

        @SerializedName("color")
        val color: String,

        @SerializedName("price")
        val price: BigDecimal,

        @SerializedName("discountedPrice")
        val discountedPrice: BigDecimal? = null,

        @SerializedName("productName")
        val productName: String,

        @SerializedName("productImage")
        val productImage: String
    ) {
        val finalPrice: BigDecimal
            get() = discountedPrice ?: price

        val totalPrice: BigDecimal
            get() = finalPrice * BigDecimal(quantity)

        val hasDiscount: Boolean
            get() = discountedPrice != null && discountedPrice < price

        val discountPercentage: Int
            get() = if (hasDiscount && discountedPrice != null) {
                ((price - discountedPrice) * BigDecimal(100) / price).toInt()
            } else 0
    }

    val isEmpty: Boolean
        get() = items.isEmpty()

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    val subtotal: BigDecimal
        get() = items.fold(BigDecimal.ZERO) { acc, item -> acc + item.totalPrice }

    // Assuming a fixed shipping rate for simplicity
    // In a real app, this would be calculated based on location, weight, etc.
    val shippingCost: BigDecimal
        get() = if (isEmpty) BigDecimal.ZERO else BigDecimal("5.99")

    // Assuming a fixed tax rate of 10% for simplicity
    // In a real app, this would be calculated based on location and tax rules
    val tax: BigDecimal
        get() = subtotal * BigDecimal("0.10")

    val total: BigDecimal
        get() = subtotal + shippingCost + tax

    fun findItem(productId: String, size: String, color: String): CartItem? {
        return items.find { 
            it.productId == productId && 
            it.size == size && 
            it.color == color 
        }
    }

    fun containsProduct(productId: String): Boolean {
        return items.any { it.productId == productId }
    }

    fun getQuantityFor(productId: String, size: String, color: String): Int {
        return findItem(productId, size, color)?.quantity ?: 0
    }

    companion object {
        fun createEmpty(userId: String): Cart {
            return Cart(
                id = userId, // Using userId as cart id for simplicity
                userId = userId,
                items = emptyList()
            )
        }
    }
}

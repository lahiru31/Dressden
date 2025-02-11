package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("items")
    val items: List<CartItem>,

    @SerializedName("subtotal")
    val subtotal: BigDecimal,

    @SerializedName("tax")
    val tax: BigDecimal,

    @SerializedName("shipping_fee")
    val shippingFee: BigDecimal,

    @SerializedName("total")
    val total: BigDecimal,

    @SerializedName("coupon_code")
    val couponCode: String? = null,

    @SerializedName("discount")
    val discount: BigDecimal = BigDecimal.ZERO,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date
) {
    data class CartItem(
        @SerializedName("product_id")
        val productId: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("quantity")
        val quantity: Int,

        @SerializedName("price")
        val price: BigDecimal,

        @SerializedName("sale_price")
        val salePrice: BigDecimal? = null,

        @SerializedName("total")
        val total: BigDecimal,

        @SerializedName("size")
        val size: String? = null,

        @SerializedName("color")
        val color: String? = null,

        @SerializedName("image")
        val image: String? = null,

        @SerializedName("stock_quantity")
        val stockQuantity: Int,

        @SerializedName("is_available")
        val isAvailable: Boolean = true
    ) {
        // Computed properties
        val isOnSale: Boolean
            get() = salePrice != null && salePrice < price

        val currentPrice: BigDecimal
            get() = salePrice ?: price

        val discountPercentage: Int?
            get() = if (isOnSale) {
                ((price - salePrice!!) * BigDecimal(100) / price).toInt()
            } else null

        val hasEnoughStock: Boolean
            get() = stockQuantity >= quantity

        // Helper functions
        fun getFormattedPrice(): String = "₹%.2f".format(currentPrice)
        
        fun getFormattedTotal(): String = "₹%.2f".format(total)
    }

    // Computed properties
    val totalItems: Int
        get() = items.sumOf { it.quantity }

    val hasUnavailableItems: Boolean
        get() = items.any { !it.isAvailable }

    val hasInsufficientStockItems: Boolean
        get() = items.any { !it.hasEnoughStock }

    val hasCouponApplied: Boolean
        get() = !couponCode.isNullOrBlank() && discount > BigDecimal.ZERO

    // Helper functions
    fun getFormattedSubtotal(): String = "₹%.2f".format(subtotal)
    
    fun getFormattedTax(): String = "₹%.2f".format(tax)
    
    fun getFormattedShippingFee(): String = "₹%.2f".format(shippingFee)
    
    fun getFormattedDiscount(): String = "₹%.2f".format(discount)
    
    fun getFormattedTotal(): String = "₹%.2f".format(total)

    fun findItem(productId: String, size: String? = null, color: String? = null): CartItem? {
        return items.find { 
            it.productId == productId && 
            it.size == size && 
            it.color == color 
        }
    }

    fun canCheckout(): Boolean {
        return items.isNotEmpty() &&
                !hasUnavailableItems &&
                !hasInsufficientStockItems
    }

    companion object {
        fun createEmpty(userId: String) = Cart(
            id = "",
            userId = userId,
            items = emptyList(),
            subtotal = BigDecimal.ZERO,
            tax = BigDecimal.ZERO,
            shippingFee = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}

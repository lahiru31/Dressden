package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("items")
    val items: List<OrderItem>,

    @SerializedName("status")
    val status: OrderStatus,

    @SerializedName("shippingAddress")
    val shippingAddress: User.Address,

    @SerializedName("billingAddress")
    val billingAddress: User.Address,

    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethod,

    @SerializedName("subtotal")
    val subtotal: BigDecimal,

    @SerializedName("shippingCost")
    val shippingCost: BigDecimal,

    @SerializedName("tax")
    val tax: BigDecimal,

    @SerializedName("total")
    val total: BigDecimal,

    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @SerializedName("estimatedDeliveryDate")
    val estimatedDeliveryDate: Long? = null,

    @SerializedName("trackingNumber")
    val trackingNumber: String? = null
) {
    data class OrderItem(
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
        val discountedPrice: BigDecimal? = null
    ) {
        val finalPrice: BigDecimal
            get() = discountedPrice ?: price

        val totalPrice: BigDecimal
            get() = finalPrice * BigDecimal(quantity)
    }

    enum class OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED
    }

    enum class PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        NET_BANKING,
        UPI,
        CASH_ON_DELIVERY,
        WALLET
    }

    val canCancel: Boolean
        get() = status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)

    val canReturn: Boolean
        get() = status == OrderStatus.DELIVERED

    val isActive: Boolean
        get() = status !in listOf(OrderStatus.CANCELLED, OrderStatus.RETURNED)

    val statusText: String
        get() = status.name.replace("_", " ").capitalize()
}

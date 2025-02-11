package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("status")
    val status: OrderStatus,

    @SerializedName("items")
    val items: List<OrderItem>,

    @SerializedName("subtotal")
    val subtotal: BigDecimal,

    @SerializedName("tax")
    val tax: BigDecimal,

    @SerializedName("shipping_fee")
    val shippingFee: BigDecimal,

    @SerializedName("total")
    val total: BigDecimal,

    @SerializedName("payment_method")
    val paymentMethod: PaymentMethod,

    @SerializedName("payment_status")
    val paymentStatus: PaymentStatus,

    @SerializedName("shipping_address")
    val shippingAddress: Address,

    @SerializedName("billing_address")
    val billingAddress: Address,

    @SerializedName("tracking_number")
    val trackingNumber: String? = null,

    @SerializedName("estimated_delivery")
    val estimatedDelivery: Date? = null,

    @SerializedName("actual_delivery")
    val actualDelivery: Date? = null,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("cancellation_reason")
    val cancellationReason: String? = null
) {
    enum class OrderStatus {
        @SerializedName("pending")
        PENDING,

        @SerializedName("confirmed")
        CONFIRMED,

        @SerializedName("processing")
        PROCESSING,

        @SerializedName("shipped")
        SHIPPED,

        @SerializedName("delivered")
        DELIVERED,

        @SerializedName("cancelled")
        CANCELLED,

        @SerializedName("returned")
        RETURNED
    }

    enum class PaymentMethod {
        @SerializedName("credit_card")
        CREDIT_CARD,

        @SerializedName("debit_card")
        DEBIT_CARD,

        @SerializedName("upi")
        UPI,

        @SerializedName("net_banking")
        NET_BANKING,

        @SerializedName("cod")
        CASH_ON_DELIVERY
    }

    enum class PaymentStatus {
        @SerializedName("pending")
        PENDING,

        @SerializedName("completed")
        COMPLETED,

        @SerializedName("failed")
        FAILED,

        @SerializedName("refunded")
        REFUNDED
    }

    data class OrderItem(
        @SerializedName("product_id")
        val productId: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("quantity")
        val quantity: Int,

        @SerializedName("price")
        val price: BigDecimal,

        @SerializedName("total")
        val total: BigDecimal,

        @SerializedName("size")
        val size: String? = null,

        @SerializedName("color")
        val color: String? = null,

        @SerializedName("image")
        val image: String? = null
    )

    data class Address(
        @SerializedName("name")
        val name: String,

        @SerializedName("phone")
        val phone: String,

        @SerializedName("address_line1")
        val addressLine1: String,

        @SerializedName("address_line2")
        val addressLine2: String? = null,

        @SerializedName("city")
        val city: String,

        @SerializedName("state")
        val state: String,

        @SerializedName("postal_code")
        val postalCode: String,

        @SerializedName("country")
        val country: String
    )

    // Computed properties
    val isActive: Boolean
        get() = status !in listOf(OrderStatus.CANCELLED, OrderStatus.DELIVERED, OrderStatus.RETURNED)

    val canCancel: Boolean
        get() = status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)

    val canReturn: Boolean
        get() = status == OrderStatus.DELIVERED

    val isPaymentPending: Boolean
        get() = paymentStatus == PaymentStatus.PENDING

    val totalItems: Int
        get() = items.sumOf { it.quantity }

    // Helper functions
    fun getFormattedTotal(): String = "₹%.2f".format(total)

    fun getFormattedStatus(): String = status.name.lowercase().capitalize()

    fun getStatusColor(): Int = when (status) {
        OrderStatus.PENDING -> android.R.color.holo_orange_light
        OrderStatus.CONFIRMED -> android.R.color.holo_blue_light
        OrderStatus.PROCESSING -> android.R.color.holo_purple
        OrderStatus.SHIPPED -> android.R.color.holo_blue_dark
        OrderStatus.DELIVERED -> android.R.color.holo_green_light
        OrderStatus.CANCELLED -> android.R.color.holo_red_light
        OrderStatus.RETURNED -> android.R.color.darker_gray
    }
}

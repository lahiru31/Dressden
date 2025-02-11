package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: BigDecimal,

    @SerializedName("sale_price")
    val salePrice: BigDecimal? = null,

    @SerializedName("category_id")
    val categoryId: String,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("images")
    val images: List<String>,

    @SerializedName("thumbnail")
    val thumbnail: String,

    @SerializedName("stock_quantity")
    val stockQuantity: Int,

    @SerializedName("brand")
    val brand: String? = null,

    @SerializedName("size")
    val size: String? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("tags")
    val tags: List<String> = emptyList(),

    @SerializedName("rating")
    val rating: Float = 0f,

    @SerializedName("review_count")
    val reviewCount: Int = 0,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("is_featured")
    val isFeatured: Boolean = false,

    @SerializedName("is_new_arrival")
    val isNewArrival: Boolean = false,

    @SerializedName("is_bestseller")
    val isBestseller: Boolean = false,

    @SerializedName("specifications")
    val specifications: Map<String, String> = emptyMap(),

    @SerializedName("available_sizes")
    val availableSizes: List<String> = emptyList(),

    @SerializedName("available_colors")
    val availableColors: List<String> = emptyList()
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

    val isInStock: Boolean
        get() = stockQuantity > 0

    val hasVariants: Boolean
        get() = availableSizes.isNotEmpty() || availableColors.isNotEmpty()

    companion object {
        const val MIN_STOCK_THRESHOLD = 5
    }

    // Helper functions
    fun isLowStock(): Boolean = stockQuantity in 1..MIN_STOCK_THRESHOLD

    fun hasSpecification(key: String): Boolean = specifications.containsKey(key)

    fun getSpecification(key: String): String? = specifications[key]

    fun hasTag(tag: String): Boolean = tags.contains(tag)

    fun hasSize(size: String): Boolean = availableSizes.contains(size)

    fun hasColor(color: String): Boolean = availableColors.contains(color)

    fun getFormattedPrice(): String = "₹%.2f".format(currentPrice)

    fun getFormattedRating(): String = "%.1f".format(rating)
}

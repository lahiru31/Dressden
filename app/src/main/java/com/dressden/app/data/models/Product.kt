package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    
    val isFavorite: Boolean = false,
    val inStock: Boolean = true,
    val stockQuantity: Int = 0,
    
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    
    val discount: Double = 0.0,
    val discountEndDate: Date? = null,
    
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    
    val brand: String? = null,
    val material: String? = null,
    val care: String? = null,
    
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    val hasDiscount: Boolean
        get() = discount > 0 && (discountEndDate?.after(Date()) ?: false)

    val finalPrice: Double
        get() = if (hasDiscount) {
            price * (1 - discount)
        } else {
            price
        }

    val isAvailable: Boolean
        get() = inStock && stockQuantity > 0

    companion object {
        const val CATEGORY_DRESSES = "dresses"
        const val CATEGORY_TOPS = "tops"
        const val CATEGORY_BOTTOMS = "bottoms"
        const val CATEGORY_OUTERWEAR = "outerwear"
        const val CATEGORY_ACCESSORIES = "accessories"

        val CATEGORIES = listOf(
            CATEGORY_DRESSES,
            CATEGORY_TOPS,
            CATEGORY_BOTTOMS,
            CATEGORY_OUTERWEAR,
            CATEGORY_ACCESSORIES
        )
    }
}

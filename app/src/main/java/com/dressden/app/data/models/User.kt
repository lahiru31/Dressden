package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("addresses")
    val addresses: List<Address> = emptyList(),

    @SerializedName("wishlist")
    val wishlist: List<String> = emptyList(), // List of product IDs

    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName"

    data class Address(
        @SerializedName("id")
        val id: String,

        @SerializedName("type")
        val type: AddressType,

        @SerializedName("name")
        val name: String,

        @SerializedName("streetAddress")
        val streetAddress: String,

        @SerializedName("city")
        val city: String,

        @SerializedName("state")
        val state: String,

        @SerializedName("postalCode")
        val postalCode: String,

        @SerializedName("country")
        val country: String,

        @SerializedName("isDefault")
        val isDefault: Boolean = false
    )

    enum class AddressType {
        HOME, WORK, OTHER
    }
}

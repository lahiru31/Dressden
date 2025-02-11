package com.dressden.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("profile_image")
    val profileImage: String? = null,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("address_line1")
    val addressLine1: String? = null,

    @SerializedName("address_line2")
    val addressLine2: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("postal_code")
    val postalCode: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("notification_enabled")
    val notificationEnabled: Boolean = true,

    @SerializedName("location_enabled")
    val locationEnabled: Boolean = true,

    @SerializedName("fcm_token")
    val fcmToken: String? = null,

    @SerializedName("last_login")
    val lastLogin: Date? = null,

    @SerializedName("preferences")
    val preferences: Map<String, Any>? = null
) {
    // Computed properties
    val fullAddress: String?
        get() = buildString {
            addressLine1?.let { append(it) }
            addressLine2?.let { append(", ").append(it) }
            city?.let { append(", ").append(it) }
            state?.let { append(", ").append(it) }
            postalCode?.let { append(" ").append(it) }
            country?.let { append(", ").append(it) }
        }.takeIf { it.isNotEmpty() }

    val hasCompleteProfile: Boolean
        get() = !name.isNullOrBlank() && 
                !email.isNullOrBlank() && 
                !phone.isNullOrBlank() && 
                !addressLine1.isNullOrBlank() &&
                !city.isNullOrBlank() &&
                !state.isNullOrBlank() &&
                !postalCode.isNullOrBlank() &&
                !country.isNullOrBlank()

    companion object {
        fun createEmpty() = User(
            id = "",
            email = "",
            name = "",
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}

package com.dressden.app.services

import android.util.Log
import com.dressden.app.utils.notifications.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DressDenFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            handleNotificationMessage(it)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        when (data["type"]) {
            "order_update" -> {
                val orderId = data["order_id"]
                val status = data["status"]
                if (orderId != null && status != null) {
                    notificationManager.showOrderStatusNotification(orderId, status)
                }
            }
            "new_product" -> {
                val productName = data["product_name"]
                if (productName != null) {
                    notificationManager.showNewProductNotification(productName)
                }
            }
            "promotion" -> {
                val title = data["title"] ?: "New Promotion"
                val message = data["message"] ?: "Check out our latest offers!"
                notificationManager.showPromotionalNotification(title, message)
            }
            "chat_message" -> {
                val sender = data["sender"] ?: "Support"
                val message = data["message"] ?: "New message received"
                notificationManager.showChatMessage(sender, message)
            }
        }
    }

    private fun handleNotificationMessage(notification: RemoteMessage.Notification) {
        notificationManager.showSystemNotification(
            notification.title ?: "Dress Den",
            notification.body ?: "You have a new notification"
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // Send the token to your server
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement sending token to backend server
        // This would typically be handled by your AuthRepository or a dedicated TokenRepository
    }

    companion object {
        private const val TAG = "FirebaseMessaging"
    }
}

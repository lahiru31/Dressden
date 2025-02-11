package com.dressden.app.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dressden.app.data.api.ApiService
import com.dressden.app.data.local.dao.ProductDao
import com.dressden.app.utils.notifications.NotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class OrderSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "order_sync_worker"
        private const val MAX_RETRIES = 3
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get pending orders that need to be synced
            val pendingOrders = productDao.getPendingOrders()
            
            if (pendingOrders.isEmpty()) {
                return@withContext Result.success()
            }

            // Sync each pending order
            pendingOrders.forEach { order ->
                try {
                    // Attempt to sync order with backend
                    val response = apiService.syncOrder(order)
                    
                    if (response.isSuccessful) {
                        // Update local order status
                        productDao.updateOrderStatus(order.id, "SYNCED")
                        
                        // Show notification for successful sync
                        notificationManager.showOrderStatusNotification(
                            order.id,
                            "Order successfully synced"
                        )
                    } else {
                        // Handle unsuccessful sync
                        productDao.updateOrderStatus(order.id, "SYNC_FAILED")
                        throw Exception("Failed to sync order: ${response.message()}")
                    }
                } catch (e: Exception) {
                    // Log error and continue with next order
                    e.printStackTrace()
                }
            }

            // Return success if we've processed all orders
            Result.success()
        } catch (e: Exception) {
            // Determine if we should retry
            val runAttemptCount = runAttemptCount
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                // Show notification for sync failure
                notificationManager.showNotification(
                    "Order Sync Failed",
                    "Failed to sync orders. Please try again later."
                )
                Result.failure()
            }
        }
    }
}

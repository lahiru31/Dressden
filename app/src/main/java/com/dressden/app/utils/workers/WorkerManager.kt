package com.dressden.app.utils.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dressden.app.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleOrderSync(forceSync: Boolean = false) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf("force_sync" to forceSync)

        val orderSyncRequest = OneTimeWorkRequestBuilder<OrderSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(orderSyncRequest)
    }

    fun schedulePeriodicOrderSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<OrderSyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_ORDER_SYNC,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicSyncRequest
        )
    }

    fun scheduleProductSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val productSyncRequest = PeriodicWorkRequestBuilder<OrderSyncWorker>(
            repeatInterval = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_PRODUCT_SYNC,
            ExistingPeriodicWorkPolicy.UPDATE,
            productSyncRequest
        )
    }

    fun cancelAllWork() {
        workManager.cancelAllWork()
    }

    fun cancelWorkById(workName: String) {
        workManager.cancelUniqueWork(workName)
    }

    fun getWorkStatus(workName: String) = 
        workManager.getWorkInfosForUniqueWorkLiveData(workName)

    fun pruneWork() {
        workManager.pruneWork()
    }

    fun isWorkScheduled(workName: String): Boolean {
        val workInfo = workManager.getWorkInfosForUniqueWork(workName).get()
        return workInfo.any { !it.state.isFinished }
    }

    fun getWorkProgress(workName: String): Float {
        val workInfo = workManager.getWorkInfosForUniqueWork(workName).get()
        return workInfo
            .firstOrNull { it.state == WorkInfo.State.RUNNING }
            ?.progress
            ?.getFloat(Constants.WORK_PROGRESS, 0f) ?: 0f
    }

    companion object {
        private const val TAG = "WorkerManager"
    }
}

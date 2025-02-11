package com.dressden.app.utils.telephony

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.dressden.app.utils.permissions.PermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val smsManager: SmsManager by lazy {
        context.getSystemService(SmsManager::class.java)
    }

    fun makePhoneCall(
        phoneNumber: String,
        onError: (Exception) -> Unit
    ) {
        if (!permissionManager.checkTelephonyPermissions()) {
            onError(SecurityException("Call phone permission not granted"))
            return
        }

        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun sendSMS(
        phoneNumber: String,
        message: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (!permissionManager.checkTelephonyPermissions()) {
            onError(SecurityException("Send SMS permission not granted"))
            return
        }

        try {
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun composeSMS(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun isPhoneCallAvailable(): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isSMSAvailable(): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "TelephonyManager"
    }
}

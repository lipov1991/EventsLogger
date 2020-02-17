package pl.lipov.eventslogger.common.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsUtils(
    private val context: Context
) {

    companion object {
        private const val REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS = 123
    }

    fun hasExternalStoragePermissions(): Boolean =
        permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                permissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun permissionGranted(
        permissionName: String
    ): Boolean = ContextCompat.checkSelfPermission(context, permissionName) ==
            PackageManager.PERMISSION_GRANTED

    fun requestExternalStoragePermissions(
        activity: Activity
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS
        )
    }

    fun isExternalStoragePermissions(
        requestCode: Int
    ): Boolean = requestCode == REQUEST_CODE_EXTERNAL_STORAGE_PERMISSIONS

    fun externalStoragePermissionsGranted(
        grantResults: IntArray
    ): Boolean = grantResults.findLast { it != PackageManager.PERMISSION_GRANTED } == null
}

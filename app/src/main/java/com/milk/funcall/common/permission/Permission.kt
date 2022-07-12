package com.milk.funcall.common.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ForwardScope

object Permission {
    fun checkStoragePermission(
        activity: FragmentActivity,
        refuseRequest: (ForwardScope, List<String>) -> Unit = { _, _ -> },
        resultRequest: (Boolean, List<String>) -> Unit
    ) {
        val storagePermission = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeExternalStorage = obtainedPermission(activity, storagePermission[0])
        val readExternalStorage = obtainedPermission(activity, storagePermission[1])
        if (writeExternalStorage && readExternalStorage)
            resultRequest(true, mutableListOf())
        else {
            if (writeExternalStorage)
                storagePermission.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (readExternalStorage)
                storagePermission.remove(Manifest.permission.READ_EXTERNAL_STORAGE)
            PermissionX.init(activity).permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                .onForwardToSettings { scope, deniedList -> refuseRequest(scope, deniedList) }
                .request { allGranted, grantedList, _ ->
                    resultRequest(allGranted, grantedList)
                }
        }
    }

    private fun obtainedPermission(context: Context, permission: String) =
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
package com.jumbox.app.muslim.utils

import android.Manifest
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

object PermissionManager {

    fun cekPermission(activity: FragmentActivity, list: List<String>, onPermissionsChecked: () -> Unit, onPermissionsDenied: () -> Unit) {
        PermissionX.init(activity)
            .permissions(list)
            .request { allGranted, _, _ ->
                if (allGranted) { onPermissionsChecked.invoke()
                } else { onPermissionsDenied.invoke() }
            }
    }

    fun cekPermissionLocation(activity: FragmentActivity, onPermissionsChecked: () -> Unit, onPermissionsDenied: () -> Unit) {
        PermissionX.init(activity)
            .permissions(arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
            .request { allGranted, _, _ ->
                if (allGranted) { onPermissionsChecked.invoke()
                } else {
                    onPermissionsDenied.invoke()
                }
            }
    }

    fun cekPermissionStorage(activity: FragmentActivity, onPermissionsChecked: () -> Unit, onPermissionsDenied: () -> Unit) {
        PermissionX.init(activity)
            .permissions(
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_SMS
                )
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    onPermissionsChecked.invoke()
                } else {
                    onPermissionsDenied.invoke()
                }
            }
    }

    fun cekPermissionSMS(activity: FragmentActivity, onPermissionsChecked: () -> Unit, onPermissionsDenied: () -> Unit) {
        PermissionX.init(activity)
            .permissions(
                arrayListOf(
                    Manifest.permission.RECEIVE_SMS
                )
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    onPermissionsChecked.invoke()
                } else {
                    onPermissionsDenied.invoke()
                }
            }
    }
}
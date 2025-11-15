package com.js_loop_erp.components

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionResolver(val activity: Activity)
{

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions(){
        if(isPermissionGranted() != PackageManager.PERMISSION_GRANTED){
            showAlert()
        } else {
            //   Toast.makeText(activity, "Permissions already granted", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPermissionGranted():Int {
        var counter = 0;
        for(permission in LIST_OF_PERMISSION) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        //    Toast.makeText(activity, "Counter: ${counter}", Toast.LENGTH_LONG).show()

        return counter
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun deniedPermission(): String {
        for(permission in LIST_OF_PERMISSION){
            if(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED){
                return permission
            }
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("App Permission(s)")
        builder.setMessage(
            "This application requires certain permissions (battery optimization, background location, camera, and storage) " +
            "to function properly. These permissions allow continuous tracking, notifications, and file handling. " +
            "Granting them may slightly increase battery usage, but without them some features may not work correctly."
        );
        builder.setPositiveButton("OK", {dialog, which ->
            requestPermissions()
        })
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions(){
        val permission = deniedPermission()
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            Toast.makeText(activity, "Go-To Settings and Grant Permission(s)", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(activity, LIST_OF_PERMISSION.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    companion object {

        val PERMISSION_REQUEST_CODE = 123342

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val LIST_OF_PERMISSION = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA,
            // Add this for Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            else null
        ).filterNotNull()
    }
}
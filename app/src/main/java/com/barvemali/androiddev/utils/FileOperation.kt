package com.barvemali.androiddev.utils

import android.R.id.input
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream




fun getFilePathFromUri(context: Context, uri: Uri?): String? {
    val contentResolver = context.contentResolver
    val cursor = uri?.let { contentResolver.query(it, null, null, null, null) }
    return cursor?.use {
        it.moveToFirst()
        val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME) // MediaStore.Images.Media.DATA for images
        if (columnIndex != -1) {
            it.getString(columnIndex)
        } else {
            null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getFileFromUri(context: Context, uri: Uri?): ByteArray? {
    var result: ByteArray? = null
    if (uri != null) {
        context.contentResolver.openFileDescriptor(uri, "r")?.use {
            FileInputStream(it.fileDescriptor).use {
                result = it.readAllBytes()
            }
        }
    }
    return result
}
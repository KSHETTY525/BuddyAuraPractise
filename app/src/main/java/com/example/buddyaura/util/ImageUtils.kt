package com.example.buddyaura.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun saveBitmap(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
        val output = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        output.flush()
        output.close()
        return Uri.fromFile(file)
    }
}

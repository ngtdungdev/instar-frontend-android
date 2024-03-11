package com.instar.frontend_android.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import java.io.FileOutputStream

class SaveImageToFile {
    companion object {
        fun saveImage(context: Context, bitmap: Bitmap, position: Int): String? {
            val filename = "image$position.png"
            val fos: FileOutputStream
            try {
                fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close()
                return context.getFileStreamPath(filename).absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
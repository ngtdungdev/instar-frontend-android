package com.instar.frontend_android.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
class SaveImageToFile {
    companion object {
        fun saveImage(context: Context, bitmap: Bitmap, text: String): String? {
            val filename = "image$text.png"
            try {
                context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.flush()
                    fos.close()
                }

                return context.getFileStreamPath(filename).absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
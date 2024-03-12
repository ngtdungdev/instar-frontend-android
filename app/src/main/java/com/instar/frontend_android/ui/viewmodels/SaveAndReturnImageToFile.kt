package com.instar.frontend_android.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

class SaveAndReturnImageToFile {
    companion object {
        fun stringToBitmap(filePath: String, context: Context): Bitmap? {
            return try {
                val file = File(context.filesDir, filePath)
                val text = file.readText()
                file.exists()
                val encodeByte: ByteArray = Base64.decode(text, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message
                null
            }
        }
        fun bitmapToBase64(bitmap: Bitmap, text: String, context: Context): String {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            val file = File(context.filesDir, "image$text.txt")
            if (!file.exists()) {
                file.createNewFile()
            }
            file.writeText(Base64.encodeToString(byteArray, Base64.DEFAULT))
            file.exists()
            return "image$text.txt"
        }
    }
}
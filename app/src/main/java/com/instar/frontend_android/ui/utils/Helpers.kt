package com.instar.frontend_android.ui.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.adapters.CarouselAdapter
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

object Helpers {
    @JvmStatic
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    @JvmStatic
    fun decodeJwt(token: String): JSONObject {
        val parts = token.split(".")
        if (parts.size == 3) {
            val body = parts[1]
            val decodedBytes = Base64.getUrlDecoder().decode(body)
            return JSONObject(String(decodedBytes))
        } else {
            throw IllegalArgumentException("Invalid token format")
        }
    }

    @JvmStatic
    fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = decodeJwt(accessToken)
            return decodedTokenJson.getString("id")
        }
        return null
    }

    @JvmStatic
    fun convertToTimeAgo(timestamp: String): String {
        println(timestamp)
        try {
            val dateTime = ZonedDateTime.parse(timestamp)
            val now = ZonedDateTime.now()
            val duration = Duration.between(dateTime, now)

            return when {
                duration.seconds < 60 -> "vừa xong"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
                duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
                duration.toDays() < 7 -> "${duration.toDays()} ngày trước"
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        .withLocale(Locale("vi")) // Set Vietnamese locale for month name
                    dateTime.format(formatter)
                }
            }
        } catch (e: Exception) {
            try {
                // Chuyển chuỗi timestamp thành số long
                val timestampLong = timestamp.toLong()

                // Convert milliseconds since epoch to ZonedDateTime
                val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestampLong), ZoneId.systemDefault())

                // Now proceed with your logic to calculate time ago

                val now = ZonedDateTime.now()
                val duration = Duration.between(dateTime, now)

                return when {
                    duration.seconds < 60 -> "vừa xong"
                    duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
                    duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
                    duration.toDays() < 7 -> "${duration.toDays()} ngày trước"
                    else -> {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .withLocale(Locale("vi")) // Set Vietnamese locale for month name
                        dateTime.format(formatter)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return "Invalid timestamp"
            }
        }
    }


    @JvmStatic
    fun getMediaType(url: String): CarouselAdapter.MediaType {
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp", "avif") // Danh sách các phần mở rộng hình ảnh
        val videoExtensions = listOf("mp4", "avi", "mov", "mkv") // Danh sách các phần mở rộng video

        val extension = url.substringAfterLast(".") // Lấy phần mở rộng của URL

        return when {
            imageExtensions.contains(extension) -> CarouselAdapter.MediaType.IMAGE
            videoExtensions.contains(extension) -> CarouselAdapter.MediaType.VIDEO
            else -> throw IllegalArgumentException("Unsupported media type for URL: $url")
        }
    }

    @JvmStatic
    suspend fun convertToMultipartParts(context: Context, imageAndVideoList: List<ImageAndVideo>, name: String = "files"): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()

        withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver

            for (imageAndVideo in imageAndVideoList) {
                val uri = Uri.parse(imageAndVideo.uri)
                val inputStream = contentResolver.openInputStream(uri)
                inputStream.use { input ->
                    input?.let {
                        val fileName = getFileName(context, uri)
                        // Mở một bản sao mới của inputStream để sử dụng trong writeTo
                        val inputStreamCopy = contentResolver.openInputStream(uri)
                        val requestBody = object : RequestBody() {
                            override fun contentType(): MediaType? {
                                return contentResolver.getType(uri)?.toMediaTypeOrNull()
                            }

                            override fun writeTo(sink: BufferedSink) {
                                inputStreamCopy?.use { inputStream ->
                                    try {
                                        sink.writeAll(inputStream.source())
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                        val part = MultipartBody.Part.createFormData(name, fileName, requestBody)
                        parts.add(part)
                    }
                }
            }
        }

        return parts
    }

    @JvmStatic
    public fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow("_display_name"))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }

    @JvmStatic
    suspend fun convertToMultipartPart(context: Context, imageAndVideo: ImageAndVideo, name: String = "files"): MultipartBody.Part {
        val uri = Uri.parse(imageAndVideo.uri)
        val contentResolver: ContentResolver = context.contentResolver

        return withContext(Dispatchers.IO) {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream.use { input ->
                input?.let {
                    val fileName = getFileName(context, uri)
                    // Create a copy of inputStream to use for writing to sink
                    val inputStreamCopy = contentResolver.openInputStream(uri)
                    val requestBody = object : RequestBody() {
                        override fun contentType(): MediaType? {
                            return contentResolver.getType(uri)?.toMediaTypeOrNull()
                        }

                        override fun writeTo(sink: BufferedSink) {
                            inputStreamCopy?.use { inputStream ->
                                try {
                                    sink.writeAll(inputStream.source())
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    MultipartBody.Part.createFormData(name, fileName, requestBody)
                } ?: throw IOException("Failed to open InputStream")
            }
        }
    }

    @JvmStatic
    suspend fun convertToMultipartPartURI(context: Context, uriString: String, name: String = "files"): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse(uriString)
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            inputStream.use { input ->
                input?.let {
                    val fileName = getFileName(context, uri)
                    val requestBody = object : RequestBody() {
                        override fun contentType(): MediaType? {
                            return contentResolver.getType(uri)?.toMediaTypeOrNull()
                        }

                        override fun writeTo(sink: BufferedSink) {
                            val inputStreamCopy = contentResolver.openInputStream(uri)
                            inputStreamCopy.use { inputStream ->
                                sink.writeAll(inputStream!!.source())
                            }
                        }
                    }
                    MultipartBody.Part.createFormData(name, fileName ?: "unknown", requestBody)
                } ?: throw IOException("Failed to open InputStream")
            }
        }
    }

    @JvmStatic
    fun getImageBytesFromImageAndVideo(imageAndVideo: ImageAndVideo): ByteString? {
        // Xử lý trích xuất dữ liệu hình ảnh từ imageAndVideo
        val uriString = imageAndVideo.filePath
        // Chuyển đổi chuỗi String thành ByteString
        return uriString.let { ByteString.copyFrom(it.toByteArray()) }
    }
}

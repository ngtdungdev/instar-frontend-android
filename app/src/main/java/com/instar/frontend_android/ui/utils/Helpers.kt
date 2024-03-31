package com.instar.frontend_android.ui.utils

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
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
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import java.io.FileInputStream
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
            val decodedBytes = Base64.getDecoder().decode(body)
            val jsonObject = JSONObject(String(decodedBytes))
            return jsonObject
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
        try {
            // Convert milliseconds since epoch to ZonedDateTime
            val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), ZoneId.systemDefault())

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
            // Handle parsing exception
            e.printStackTrace()
            return "Invalid timestamp"
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
    fun convertToMultipartParts(imageAndVideoList: List<ImageAndVideo>): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()

        for (imageAndVideo in imageAndVideoList) {
            val file = File(imageAndVideo.filePath)
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestFile)
            parts.add(part)
        }

        return parts
    }


    @JvmStatic
    fun getImageBytesFromImageAndVideo(imageAndVideo: ImageAndVideo): ByteString? {
        // Xử lý trích xuất dữ liệu hình ảnh từ imageAndVideo
        val uriString = imageAndVideo.filePath
        // Chuyển đổi chuỗi String thành ByteString
        return uriString.let { ByteString.copyFrom(it.toByteArray()) }
    }

    @JvmStatic
    fun detectSensitiveContent(imageAndVideo: ImageAndVideo): Boolean {
        // Xử lý trích xuất dữ liệu hình ảnh từ imageAndVideo
        val imageBytes: ByteString? = getImageBytesFromImageAndVideo(imageAndVideo)

        if (imageBytes != null) {
            try {
                // Load credentials from the JSON key file
                val credentials = GoogleCredentials.fromStream(FileInputStream("app/credentials.json"))

                // Create ImageAnnotatorSettings with credentials
                val settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider { credentials }
                    .build()

                // Create ImageAnnotatorClient with settings
                val client = ImageAnnotatorClient.create(settings)

                try {
                    // Create Image
                    val image = Image.newBuilder().setContent(imageBytes).build()

                    // Create Feature for label detection
                    val feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build()

                    // Create AnnotateImageRequest
                    val request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feature)
                        .setImage(image)
                        .build()

                    // Create BatchAnnotateImagesRequest
                    val batchRequest = BatchAnnotateImagesRequest.newBuilder().addRequests(request).build()

                    // Perform image annotation
                    val response: BatchAnnotateImagesResponse = client.batchAnnotateImages(batchRequest)

                    // Extract labels from the response
                    val labels = response.responsesList[0].labelAnnotationsList

                    // Check for sensitive labels
                    for (label in labels) {
                        when (label.description) {
                            "Violence", "Gore", "Suggestive", "Offensive", "Blood" -> return true
                        }
                    }

                } finally {
                    client.close()
                }
            } catch (e: IOException) {
                // Handle IOException
                e.printStackTrace()
            } catch (e: Exception) {
                // Handle other exceptions
                e.printStackTrace()
            }
        } else {
            // Xử lý khi không thể trích xuất dữ liệu hình ảnh
            println("Failed to extract image data from ImageAndVideo")
        }

        return false
    }
}

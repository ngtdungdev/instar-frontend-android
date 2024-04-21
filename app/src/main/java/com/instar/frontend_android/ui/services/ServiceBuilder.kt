package com.instar.frontend_android.ui.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.instar.frontend_android.types.responses.ApiResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StaticFieldLeak")
object ServiceBuilder {
    private lateinit var context: Context
    private const val BASE_URL = "http://192.168.1.6:8080/api/"
    private lateinit var authService: AuthService

    // OkHttpClient setup with custom settings
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Gson setup for better JSON parsing
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Retrofit setup with OkHttpClient and GsonConverterFactory
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    /**
     * Build service for given service type.
     */
    fun <T> buildService(serviceType: Class<T>, context: Context): T {
        this.context = context
        authService = retrofit.create(AuthService::class.java)
        val tokenInterceptor = TokenInterceptor(context)
        val newHttpClient = okHttpClient.newBuilder()
            .addInterceptor(tokenInterceptor)
            .build()

        val newRetrofit = retrofit.newBuilder()
            .client(newHttpClient)
            .build()

        return newRetrofit.create(serviceType)
    }

    /**
     * Extension function to handle Retrofit Call responses.
     */
    data class ApiError(val message: String, val status: Any, val data: Any?)

    fun <T> Call<ApiResponse<T>>.handleResponse(
        onSuccess: (response: ApiResponse<T>) -> Unit,
        onError: (error: ApiError) -> Unit,
    ) {
        this.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(call: Call<ApiResponse<T>>, response: Response<ApiResponse<T>>) {
                val aCall = call.clone()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        onSuccess(body)
                    } else {
                        onError(ApiError("Response body is null", response.code(), null))
                    }
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val refreshToken = sharedPreferences.getString("refreshToken", null)
                        if (refreshToken != null) {
                            authService.refreshToken("Bearer $refreshToken").enqueue(object : Callback<ApiResponse<Any>> {
                                override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                                    if (response.isSuccessful) {
                                        val data = response.body()?.data
                                        val jsonObject = Gson().toJsonTree(data).asJsonObject
                                        if (data != null) {
                                            if (jsonObject.has("access_token")) {
                                                val accessToken = jsonObject.get("access_token").asString
                                                setAccessToken(context, accessToken)

                                                return aCall.enqueue(object : Callback<ApiResponse<T>> {
                                                    override fun onResponse(call: Call<ApiResponse<T>>, response: Response<ApiResponse<T>>) {
                                                        if (response.isSuccessful) {
                                                            val body = response.body()
                                                            if (body != null) {
                                                                onSuccess(body)
                                                            } else {
                                                                onError(ApiError("Response body is null", response.code(), null))
                                                            }
                                                        } else {
                                                            onError(ApiError("Unsuccessful response", response.code(), null))
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                                                        onError(ApiError("Failed to execute request", -1, t.message))
                                                    }
                                                })
                                            }
                                        }
                                    } else {
                                        setRefreshToken(context, null)
                                        setAccessToken(context, null)
                                        onError(ApiError("Unsuccessful response", response.code(), null))
                                    }
                                }

                                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                                }
                            })
                        }
                        else onError(ApiError("Unsuccessful response", response.code(), null))
                    } else {
                        onError(ApiError("Unsuccessful response", response.code(), null))
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                onError(ApiError("Failed to execute request", -1, t.message))
            }
        })
    }

    suspend fun <T> Call<ApiResponse<T>>.awaitResponse(): ApiResponse<T> {
        return suspendCoroutine { continuation ->
            handleResponse(
                onSuccess = { response ->
                    continuation.resumeWith(Result.success(response))
                },
                onError = { error ->
                    Log.e("Call API ERROR: ", error.toString())
                    continuation.resume(ApiResponse(error = "Failed to execute request"))
                }
            )
        }
    }

    /**
     * Interceptor to add access token to requests if available.
     */

    public fun setAccessToken(context: Context, accessToken: String?) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            apply()
        }
    }

    public fun setRefreshToken(context: Context, token: String?) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("refreshToken", token)
            apply()
        }
    }

    private class TokenInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()
            val accessToken = getAccessToken(context) // Retrieve access token here
            val modifiedRequest = if (accessToken != null && accessToken != "" && originalRequest.header("Authorization") != "") {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                originalRequest
            }

            return chain.proceed(modifiedRequest)
        }

        // Function to retrieve access token from SharedPreferences
        private fun getAccessToken(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            return sharedPreferences.getString("accessToken", null)
        }
    }
}

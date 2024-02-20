package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val URL = "http://10.0.2.2:8080/api/"

    private val okHttp = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    fun <T> Call<ApiResponse<T>>.handleResponse(
        onSuccess: (response: ApiResponse<T>) -> Unit,
        onError: (error: String) -> Unit
    ) {
        this.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(call: Call<ApiResponse<T>>, response: Response<ApiResponse<T>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        onSuccess(it)
                    } ?: run {
                        onError("Empty response body")
                    }
                } else {
                    onError("Unsuccessful response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                onError("Error: ${t.message}")
            }
        })
    }
}

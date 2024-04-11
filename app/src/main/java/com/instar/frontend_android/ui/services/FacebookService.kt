package com.instar.frontend_android.ui.services

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FacebookService(context: Activity) {
    private val activity: Activity = context
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val loginManager: LoginManager = LoginManager.getInstance()
    private var registered: Boolean = false

    fun login(onSuccess: ((AccessToken) -> Unit)? = null,
              onCancel: (() -> Unit)? = null,
              onError: ((FacebookException) -> Unit)? = null) {
        if (onSuccess != null || onCancel != null || onError != null) {
            addListeners(
                onSuccess ?: {},
                onCancel ?: {},
                onError ?: {})
        }
        loginManager.logInWithReadPermissions(activity, listOf("public_profile"))
    }

    fun logout() {
        loginManager.logOut()
    }

    fun getUserPublicProfile(accessToken: AccessToken? = AccessToken.getCurrentAccessToken(),
                             fields: String = "id,name,email,picture.type(large)",
                             callback: (JSONObject?) -> Unit) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            callback(jsonObject)
        }

        val parameters = Bundle()
        parameters.putString("fields", fields)
        request.parameters = parameters

        request.executeAsync()
    }

    fun addListeners(onSuccess: (AccessToken) -> Unit,
                     onCancel: () -> Unit = {},
                     onError: (FacebookException) -> Unit = {}) {
        if (registered) {
            loginManager.unregisterCallback(callbackManager)
            registered = false
        }
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                onSuccess(result.accessToken)
            }

            override fun onCancel() {
                onCancel()
            }

            override fun onError(error: FacebookException) {
                onError(error)
            }
        })
        registered = true
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun isLoggedIn(): Boolean {
            val accessToken = AccessToken.getCurrentAccessToken()
            return accessToken != null && !accessToken.isExpired
        }
    }
}
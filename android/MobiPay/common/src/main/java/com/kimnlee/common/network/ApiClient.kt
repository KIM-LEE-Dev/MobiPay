package com.kimnlee.common.network

import com.kimnlee.common.BuildConfig
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.api.UnAuthService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor(private val authManager: AuthManager?) {
    private val baseUrl = BuildConfig.BASE_URL

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val authToken = authManager?.getAuthToken()

        val newRequest = if (!authToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }

    private val authenticatedOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val unauthenticatedOkHttpClient = OkHttpClient.Builder()
        .build()

    // AuthToken을 사용하는 Api (백엔드 통신할 때 사용)
    val authenticatedApi: Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080") // 나중에 local.properties나 gradle.properties로 이동 예정
        .client(authenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // AuthToken을 사용하지 않는 Api (로그인 때 사용)
    val unAuthenticatedApi: Retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8080")
            .client(unauthenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // fcmService용 retrofit
    val fcmApi: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(unauthenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ocrService: OCRService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OCRService::class.java)
    }

    companion object {
        @Volatile
        private var instance: ApiClient? = null

        fun getInstance(authManager: AuthManager? = null): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient(authManager).also { instance = it }
            }
        }
    }
}
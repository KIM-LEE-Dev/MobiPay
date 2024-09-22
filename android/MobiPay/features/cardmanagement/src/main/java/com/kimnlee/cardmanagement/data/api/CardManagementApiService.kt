package com.kimnlee.cardmanagement.data.api

import com.kimnlee.api.network.ApiClient
import com.kimnlee.cardmanagement.data.model.User
import retrofit2.http.GET

interface CardManagementApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    companion object {
        val instance: CardManagementApiService by lazy {
            ApiClient.retrofit.create(CardManagementApiService::class.java)
        }
    }
}
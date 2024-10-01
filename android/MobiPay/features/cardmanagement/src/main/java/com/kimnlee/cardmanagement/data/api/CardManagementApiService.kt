package com.kimnlee.cardmanagement.data.api

import com.kimnlee.cardmanagement.data.model.Card
import com.kimnlee.common.network.ApiClient
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager
import retrofit2.http.GET

interface CardManagementApiService {

    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @GET("api/v1/cards")
    suspend fun getCards(): List<Card>
}
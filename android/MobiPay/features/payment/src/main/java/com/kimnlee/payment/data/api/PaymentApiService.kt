package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.Photos
import retrofit2.http.GET

interface PaymentApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photos>

}
// common 모듈에 AuthService 인터페이스 정의
package com.kimnlee.common.auth.api

import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Void>

    @POST("api/v1/users/detail")
    suspend fun register(@Body registrationRequest: RegistrationRequest): Response<Void>
}
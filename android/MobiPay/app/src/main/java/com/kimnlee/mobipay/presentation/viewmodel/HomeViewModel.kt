package com.kimnlee.mobipay.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val apiClient: ApiClient,
) : ViewModel() {
    // 네이버 맵 서비스
    private val _naverMapService = MutableStateFlow<NaverMapService?>(apiClient.naverMapService)
    val naverMapService: StateFlow<NaverMapService?> = _naverMapService
}
package com.kimnlee.mobipay.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.kimnlee.vehiclemanagement.data.api.VehicleApiService
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.data.model.VehicleItem
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.vehiclemanagement.data.api.MerchantApiService
import com.kimnlee.vehiclemanagement.data.model.PaidParkingLotResponse

class HomeViewModel (apiClient: ApiClient, private val authManager: AuthManager) : ViewModel() {
    private val _naverMapService = MutableStateFlow<NaverMapService?>(apiClient.naverMapService)
    val naverMapService: StateFlow<NaverMapService?> = _naverMapService

    private val _hasNewNotifications = MutableStateFlow(false)
    val hasNewNotifications: StateFlow<Boolean> = _hasNewNotifications

    private val vehicleService: VehicleApiService = apiClient.authenticatedApi.create(VehicleApiService::class.java)

    private val merchantService: MerchantApiService = apiClient.authenticatedMerchantApi.create(MerchantApiService::class.java)

    private val _vehicles = MutableStateFlow<List<VehicleItem>>(emptyList())
    val vehicles: StateFlow<List<VehicleItem>> = _vehicles

    private val _carMembers = MutableStateFlow<List<CarMember>>(emptyList())
    val carMembers: StateFlow<List<CarMember>> = _carMembers

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userPhoneNumber = MutableStateFlow("")
    val userPhoneNumber: StateFlow<String> = _userPhoneNumber

    private val _paidParkingDetail = MutableStateFlow<PaidParkingLotResponse?>(null)
    val paidParkingDetail: StateFlow<PaidParkingLotResponse?> = _paidParkingDetail

    init {
        viewModelScope.launch {
            EventBus.events.collectLatest { event ->
                when (event) {
                    is NewNotificationEvent -> updateNotificationStatus(event.hasNew)
                }
            }
        }
        getUserVehicles()
        loadUserName()
        loadUserPhoneNumber()
//        getIfUserParkedAtPaidParkingLot()
    }

    private fun loadUserName() {
        val userInfo = authManager.getUserInfo()
        _userName.value = userInfo.name
    }

    private fun updateNotificationStatus(hasNew: Boolean) {
        _hasNewNotifications.value = hasNew
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            updateNotificationStatus(false)
        }
    }

    private fun getUserVehicles() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getUserVehicleList()
                if (response.isSuccessful) {
                    response.body()?.let { listResponse ->
                        _vehicles.value = listResponse.items
                        // 주차 정보 불러옴
                        getIfUserParkedAtPaidParkingLot()
                    }
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error fetching user vehicles: ${e.message}")
            }
        }
    }

    private fun getIfUserParkedAtPaidParkingLot() {
        viewModelScope.launch {
            try {
                _vehicles.value.forEach { vehicle ->
                    val carNumber = vehicle.number
                    Log.d("HomeViewModel", "getIfUserParkedAtPaidParkingLot 차량번호 확인: $carNumber")

                    val response = merchantService.getIfUsingPaidParkingLot(carNumber)
                    if (response.isSuccessful) {
                        _paidParkingDetail.value = response.body()
                        Log.d("HomeViewModel", "getIfUserParkedAtPaidParkingLot 유료주차장 입차정보 $carNumber: $paidParkingDetail")
                    } else {
                        Log.d("HomeViewModel", "getIfUserParkedAtPaidParkingLot 유료주차장 입차정보 가져오기 실패: $carNumber \n getIfUserParkedAtPaidParkingLot ${response.code()} / ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "getIfUserParkedAtPaidParkingLot 유료주차장 입차정보 가져오기 실패: ${e.message}")
            }
        }
    }

    fun getCarMembers(carId: Int) {
        viewModelScope.launch {
            try {
                val response = vehicleService.getVehicleMembers(carId)
                if (response.isSuccessful) {
                    response.body()?.let { carMembersResponse ->
                        val vehicle = _vehicles.value.find { it.carId == carId }
                        val ownerId = vehicle?.ownerId
                        val sortedMembers = carMembersResponse.items.sortedWith(
                            compareBy<CarMember> { member ->
                                when {
                                    member.memberId == ownerId -> 0 // 오너를 첫 번째로
                                    member.phoneNumber == _userPhoneNumber.value -> 1 // 현재 사용자를 두 번째로 (오너가 아닌 경우)
                                    else -> 2 // 다른 멤버들
                                }
                            }.thenBy { it.name } // 각 그룹 내에서 이름순으로 정렬
                        )
                        _carMembers.value = sortedMembers
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to get car members: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting car members", e)
            }
        }
    }

    fun refreshVehicles() {
        getUserVehicles()
    }

    private fun loadUserPhoneNumber() {
        val userInfo = authManager.getUserInfo()
        _userPhoneNumber.value = userInfo.phoneNumber
    }
}
package com.kimnlee.onboard.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.network.ApiClient
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

@Composable
fun OnBoardMemberInvitation(
    vehicleViewModel: VehicleManagementViewModel,
    cardManagementViewModel: CardManagementViewModel,
    apiClient: ApiClient,
    cardInfos: List<CardInfo>,
    onNavigateBack: () -> Unit,
    finishRegister : () -> Unit
){
    Column {
        Text(text = "멤버 초대 페이지")
    }
}
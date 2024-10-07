package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.mobipay.presentation.components.OnBoardAppIntroduction
import com.kimnlee.mobipay.presentation.components.OnBoardCardRegistration
import com.kimnlee.mobipay.presentation.components.OnBoardVehicleRegistration
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementScreen
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    loginviewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    vehicleViewModel: VehicleManagementViewModel,
    cardInfos: List<CardInfo>,
    navController: NavController,
    context: Context,
    authManager: AuthManager,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(color = MobiCardBgGray)
    ) {
        // 이전 버튼
        if (pagerState.currentPage in 1..2) {
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnBoardAppIntroduction(

                )
//                1 -> OnBoardVehicleRegistration()
                1 -> VehicleManagementScreen(
                    onNavigateToDetail = { vehicleId ->
                        navController.navigate("vehiclemanagement_detail/$vehicleId")
                    },
                    onNavigateToRegistration = { navController.navigate("vehiclemanagement_registration") },
                    viewModel = vehicleViewModel
                )

                2 -> OnBoardCardRegistration(
                    viewModel= homeViewModel,
                    onNavigateToRegistration = { selectedCards ->
                        val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
                        val json = Uri.encode(Gson().toJson(cardInfos))
                        navController.navigate("cardmanagement_registration/$json")
                    }
                )
            }
        }
        // 다음/완료 버튼
        Button(
            onClick = {
                if (pagerState.currentPage < 2) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    coroutineScope.launch {
//                        authManager.setFirstIn(true)
                    }
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(MobiBlue)
        ) {
            Text(
                if (pagerState.currentPage == 2) "완료" else "Continue",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
            )

        }
    }
}
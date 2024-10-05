package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.mobipay.presentation.components.AppIntroductionPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    context: Context,
    authManager: AuthManager,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(8.dp).background(color = MobiCardBgGray)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> AppIntroductionPage()
                1 -> CardRegistrationPage()
                2 -> VehicleRegistrationPage(coroutineScope = coroutineScope, authManager = authManager, navController = navController)
            }
        }
    }
}

@Composable
fun CardRegistrationPage() {
    // 카드 등록 페이지 내용
}

@Composable
fun VehicleRegistrationPage(coroutineScope : CoroutineScope, authManager: AuthManager, navController: NavController) {
    // 차량 등록 페이지 내용
    Button(
        onClick = {
                coroutineScope.launch {
                    authManager.setFirstIn(true)
                }
                navController.navigate("home")
        }
    ) {
        Text( "완료" )
    }
}
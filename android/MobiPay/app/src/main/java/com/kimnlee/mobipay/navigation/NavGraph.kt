package com.kimnlee.mobipay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.auth.navigation.authNavGraph
import com.kimnlee.auth.presentation.screen.PaymentScreen
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.mobipay.presentation.screen.SettingScreen
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager
) {
    val loginViewModel = LoginViewModel(authManager)
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        authNavGraph(navController, authManager)

        composable("home") {
            ScreenWithBottomNav(navController) {
                HomeScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
        }
        composable("settings") {
            ScreenWithBottomNav(navController) {
                SettingScreen(
                    authManager = authManager,
                    navController = navController
                )
            }
        }
        composable("payment") {
            ScreenWithBottomNav(navController) {
                PaymentScreen(
                    navController = navController
                )
            }
        }

        paymentNavGraph(navController)
        cardManagementNavGraph(navController)
        vehicleManagementNavGraph(navController)
        memberInvitationNavGraph(navController)
    }
}

@Composable
fun ScreenWithBottomNav(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    BottomNavigation(navController = navController) {
        content()
    }
}
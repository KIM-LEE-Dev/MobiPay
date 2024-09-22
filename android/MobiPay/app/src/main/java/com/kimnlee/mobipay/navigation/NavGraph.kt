package com.kimnlee.mobipay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.auth.navigation.authNavGraph
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
    val isLoggedIn by authManager.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "auth"
    ) {
        authNavGraph(navController, authManager)

        composable("home") {
            ScreenWithBottomNav(navController) {
                HomeScreen(authManager = authManager, navController = navController)
            }
        }
        composable("settings") {
            ScreenWithBottomNav(navController) {
                SettingScreen(authManager = authManager, navController = navController)
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
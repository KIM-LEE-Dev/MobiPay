package com.kimnlee.vehiclemanagement.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.common.network.ApiClient
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementDetailScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleRegistrationScreen
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

fun NavGraphBuilder.vehicleManagementNavGraph(
    navController: NavHostController,
    context: Context,
    apiClient: ApiClient,
    vehicleManagementViewModel: VehicleManagementViewModel,
    memberInvitationViewModel: MemberInvitationViewModel,
    cardManagementViewModel: CardManagementViewModel
) {
    navigation(startDestination = "vehiclemanagement_main", route = "vehiclemanagement") {
        composable("vehiclemanagement_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                VehicleManagementScreen(
                    onNavigateToDetail = { vehicleId ->
                        navController.navigate("vehiclemanagement_detail/$vehicleId")
                    },
                    onNavigateToRegistration = { navController.navigate("vehiclemanagement_registration") },
                    viewModel = vehicleManagementViewModel
                )
            }
        }
        composable("vehiclemanagement_detail/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            BottomNavigation(navController) {
                VehicleManagementDetailScreen(
                    context = context,
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.navigate("vehiclemanagement_main") },
                    onNavigateToInvitePhone = { navController.navigate("memberinvitation_phone/$vehicleId") },
                    onNavigateToNotification = { navController.navigate("notification_main") },
                    navController = navController,
                    cardManagementViewModel = cardManagementViewModel,
                    viewModel = vehicleManagementViewModel,
                    memberInvitationViewModel = memberInvitationViewModel
                )
            }
        }
        composable("vehiclemanagement_registration",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                VehicleRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() },
                    apiClient = apiClient,
                    viewModel = vehicleManagementViewModel
                )
            }
        }
    }
}
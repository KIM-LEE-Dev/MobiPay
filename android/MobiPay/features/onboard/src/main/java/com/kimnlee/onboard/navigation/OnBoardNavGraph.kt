package com.onboard.onboard.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.common.network.ApiClient
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.onboard.presentation.screen.AppIntroduction
import com.kimnlee.onboard.presentation.screen.OnBoardOwnedCardListScreen
import com.kimnlee.onboard.presentation.screen.OnBoardVehicleManagementScreen
import com.kimnlee.onboard.presentation.screen.OnBoardVehicleRegistrationScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementDetailScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleRegistrationScreen
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

fun NavGraphBuilder.onBoardNavGraph(
    navController: NavHostController,
    context: Context,
    authManager: AuthManager,
    apiClient: ApiClient,
    cardManagementViewModel: CardManagementViewModel,
    vehicleManagementViewModel: VehicleManagementViewModel,
    memberInvitationViewModel: MemberInvitationViewModel
) {
    navigation(startDestination = "onboard_main", route = "onboard") {
        // 첫 번째 온보딩 페이지
        composable("onboard_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
                AppIntroduction(
                    goOwnerPage = {navController.navigate("onboard_vehicle_registration")},
                    goMemberPage = {navController.navigate("onboard_member_invite")},
                )
            }
    }
    // 두 번째 온보딩 페이지 - 차량
    composable("onboard_vehicle_registration",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
            OnBoardVehicleRegistrationScreen(
                apiClient = apiClient,
                viewModel = vehicleManagementViewModel,
                onNavigateBack = { navController.navigateUp() },
                onNavigateOwnedCard = {navController.navigate("onboard_card_list")}
            )
    }
    // 세 번째 온보딩 페이지 - 카드
    composable("onboard_card_list",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        OnBoardOwnedCardListScreen(
            viewModel = cardManagementViewModel,
            onNavigateBack = { navController.navigateUp() },
            onNavigateToRegistration = { selectedCards ->
                val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
                val json = Uri.encode(Gson().toJson(cardInfos))
                navController.navigate("onboard_card_registration/$json")
            }
        )
        }
    composable(
        route = "onboard_card_registration/{cardInfos}",
        arguments = listOf(navArgument("cardInfos") { type = NavType.StringType }),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardInfos") ?: "")
        val cardInfos =
            Gson().fromJson<List<CardInfo>>(json, object : TypeToken<List<CardInfo>>() {}.type)
    }
}

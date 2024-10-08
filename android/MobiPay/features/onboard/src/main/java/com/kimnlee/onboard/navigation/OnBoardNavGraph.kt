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
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.memberinvitation.presentation.screen.InvitationWaitingScreen
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.onboard.presentation.screen.AppIntroduction
import com.kimnlee.onboard.presentation.screen.OnBoardCardRegistrationScreen
import com.kimnlee.onboard.presentation.screen.OnBoardMemberInvitation
import com.kimnlee.onboard.presentation.screen.OnBoardOwnedCardListScreen
import com.kimnlee.onboard.presentation.screen.OnBoardVehicleRegistrationScreen
import com.kimnlee.onboard.presentation.screen.OnBoardVehicleScreen
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import kotlin.math.log

fun NavGraphBuilder.onBoardNavGraph(
    navController: NavHostController,
    apiClient: ApiClient,
    cardManagementViewModel: CardManagementViewModel,
    vehicleManagementViewModel: VehicleManagementViewModel,
    memberInvitationViewModel: MemberInvitationViewModel,
    loginViewModel: LoginViewModel
) {
    navigation(startDestination = "onboard_main", route = "onboard") {
        // 첫 번째 온보딩 페이지
        composable("onboard_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            AppIntroduction(
                onNavigateOwnedCard = { navController.navigate("onboard_card_list") },
                goHome = {
                    navController.navigate("home")
                    loginViewModel.finishOnboard()
                }
            )
        }
    }
    // 두 번째 온보딩 페이지 - 카드
    composable(
        "onboard_card_list",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) { backStackEntry ->
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
    // 카드 한도 설정 페이지
    composable(
        route = "onboard_card_registration/{cardInfos}",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        arguments = listOf(
            navArgument("cardInfos") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardInfos") ?: "")
        val cardInfos =
            Gson().fromJson<List<CardInfo>>(json, object : TypeToken<List<CardInfo>>() {}.type)
        OnBoardCardRegistrationScreen(
            cardInfos = cardInfos,
            onNavigateBack = { navController.navigateUp() },
            onNavigateVehicle = { cardsToRegister ->
                val cardsJson = Uri.encode(Gson().toJson(cardsToRegister))
                navController.navigate("onboard_vehicle/$cardsJson")
            }
        )
    }
    // 세 번째 온보딩 페이지 - 오너, 멤버 선택 화면
    composable(
        route = "onboard_vehicle/{cardsToRegister}",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        arguments = listOf(
            navArgument("cardsToRegister") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardsToRegister") ?: "")
        val cardsToRegister = Gson().fromJson<List<RegisterCardRequest>>(
            json,
            object : TypeToken<List<RegisterCardRequest>>() {}.type
        )
        OnBoardVehicleScreen(
            cardsToRegister = cardsToRegister,
            onNavigateBack = { navController.navigateUp() },
            onNavigateVehicleRegistration = {
                val cardsJson = Uri.encode(Gson().toJson(cardsToRegister))
                navController.navigate("onboard_vehicle_registration/$cardsJson")
            },
            onNavigateMemberInvite = {
                val cardsJson = Uri.encode(Gson().toJson(cardsToRegister))
                navController.navigate("onboard_member_invite/$cardsJson")
            },
        )
    }
    //  차량 등록
    composable(
        "onboard_vehicle_registration/{cardsToRegister}",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        arguments = listOf(
            navArgument("cardsToRegister") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardsToRegister") ?: "")
        val cardsToRegister = Gson().fromJson<List<RegisterCardRequest>>(
            json,
            object : TypeToken<List<RegisterCardRequest>>() {}.type
        )
        OnBoardVehicleRegistrationScreen(
            apiClient = apiClient,
            cardManagementViewModel = cardManagementViewModel,
            vehicleViewModel = vehicleManagementViewModel,
            cardsToRegister = cardsToRegister,
            onNavigateBack = { navController.navigateUp() },
            finishRegister = {
                navController.navigate("home")
                loginViewModel.finishOnboard()
            }
        )
    }

    // 멤버초대
    composable("onboard_member_invite/{cardInfos}",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        arguments = listOf(
            navArgument("cardInfos") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardInfos") ?: "")
        val cardInfos =
            Gson().fromJson<List<CardInfo>>(json, object : TypeToken<List<CardInfo>>() {}.type)
        InvitationWaitingScreen(
            navController = navController,
            memberInvitationViewModel = memberInvitationViewModel
        )
//        OnBoardMemberInvitation(
//            apiClient = apiClient,
//            cardManagementViewModel = cardManagementViewModel,
//            vehicleViewModel = vehicleManagementViewModel,
//            cardInfos = cardInfos,
//            onNavigateBack = { navController.navigateUp() },
//            finishRegister = { navController.navigate("home") }
//        )
    }
}

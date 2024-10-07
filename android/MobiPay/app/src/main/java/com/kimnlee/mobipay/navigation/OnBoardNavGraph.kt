package com.kimnlee.mobipay.navigation

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
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.presentation.components.OnBoardAppIntroduction
import com.kimnlee.mobipay.presentation.components.OnBoardCardRegistration
import com.kimnlee.mobipay.presentation.components.OnBoardCardRegistrationList
import com.kimnlee.mobipay.presentation.components.OnBoardFrame
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

fun NavGraphBuilder.onBoardNavGraph(
    navController: NavHostController,
    context: Context,
    authManager: AuthManager,
    homeViewModel: HomeViewModel,
    vehicleViewModel: VehicleManagementViewModel
) {
    navigation(startDestination = "onboard_main", route = "onboard") {
        composable("onboard_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            OnBoardFrame(
                homeViewModel = homeViewModel,
                navController = navController,
            ){
                OnBoardAppIntroduction()
            }
        }
    }
    composable("onboard_card_list",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        OnBoardFrame(
            homeViewModel = homeViewModel,
            navController = navController,
        ){
        OnBoardCardRegistrationList(
            viewModel= homeViewModel,
            onNavigateToCardPage = {/* 카드 시트(2번 으로 가는 함수*/},
        onNavigateToRegister = { selectedCards ->
            val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
            val json = Uri.encode(Gson().toJson(cardInfos))
            navController.navigate("onboard_card_registration/$json") }
        )
    }}
    composable(
        route = "onboard_card_registration/{cardInfos}",
        arguments = listOf(navArgument("cardInfos") { type = NavType.StringType }),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) { backStackEntry ->
        val json = Uri.decode(backStackEntry.arguments?.getString("cardInfos") ?: "")
        val cardInfos =
            Gson().fromJson<List<CardInfo>>(json, object : TypeToken<List<CardInfo>>() {}.type)
        OnBoardFrame(
            homeViewModel = homeViewModel,
            navController = navController,
        ){
        OnBoardCardRegistration(
            viewModel = homeViewModel,
            cardInfos = cardInfos,
            onNavigateToCardPage = { navController.navigate("onboard_card_list")/* 카드 시트(2번 으로 가는 함수*/ }
        )
    }
}}

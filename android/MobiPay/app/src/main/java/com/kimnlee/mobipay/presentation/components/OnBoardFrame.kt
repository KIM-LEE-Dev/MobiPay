package com.kimnlee.mobipay.presentation.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardFrame(
    navController: NavController,
    homeViewModel: HomeViewModel,
    content: @Composable (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val ownedCards = homeViewModel.ownedCards.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                content(page)
//                    0 -> OnBoardAppIntroduction(
//
//                    )
//
//                    1 -> OnBoardVehicleRegistration()
//                1 -> VehicleManagementScreen(
//                    onNavigateToDetail = { vehicleId ->
//                        navController.navigate("vehiclemanagement_detail/$vehicleId")
//                    },
//                    onNavigateToRegistration = { navController.navigate("vehiclemanagement_registration") },
//                    viewModel = vehicleViewModel
//                )

//                    2 -> OnBoardCardRegistrationList(
//                        viewModel = homeViewModel,
//                        onNavigateToCardPage = {
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(2)
//                            }
//                        },
//                        onNavigateToRegister = { selectedCards ->
//                            val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
//                            val json = Uri.encode(Gson().toJson(cardInfos))
//                            navController.navigate("onboard_card_registration/$json")
//                        }
//                    )
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


}
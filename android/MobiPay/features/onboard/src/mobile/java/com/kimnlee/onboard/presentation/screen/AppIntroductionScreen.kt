package com.kimnlee.onboard.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.MyDataConsentStatus
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.R as CommonR
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.onboard.R

@Composable
fun AppIntroduction(
    cardManagementViewModel : CardManagementViewModel,
    onNavigateOwnedCard : () -> Unit,
    goHome : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MobiBgGray)
            .padding(vertical = 40.dp),
    ) {
        HeaderSection()
        FeatureItem(FeatureInfo("먼저, 카드를 등록해주세요.", "\uD83D\uDCB3"))
        Spacer(modifier = Modifier.padding(12.dp))
//        FeatureItem(FeatureInfo("그리고, 모비페이로 편해지세요.", "Enjoy MobiPay with the members","\uD83C\uDD93"))
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { goHome() }) {
            Text(text = "홈가기")
        }
        Button(onClick = {
                cardManagementViewModel.checkMyDataConsentStatus { status ->
                    when (status) {
                        is MyDataConsentStatus.Fetched -> {
                            if (status.isConsented) {
                                onNavigateOwnedCard()
                            } else {
                                onNavigateToMyDataAgreement()
                            }
                        }
                        is MyDataConsentStatus.Error -> {
                            // 에러 처리 (예: 토스트 메시지 표시)
                        }
                        else -> {} // Unknown 상태 처리
                    }
                }
},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "모비 페이 시작 하기",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = CommonR.drawable.ic_mobipay), contentDescription ="로고" )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun FeatureItem(feature: FeatureInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MobiCardBgGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = feature.imageRes, modifier = Modifier
                .height(60.dp)
                .background(Color.Transparent)
                .alignByBaseline(), fontSize = 50.sp,fontFamily = FontFamily(
                Font(com.kimnlee.common.R.font.emoji)
            ))
            Column (modifier = Modifier.padding(start = 12.dp)){
                Text(
                    text = feature.title,
                    color = MobiTextAlmostBlack,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class FeatureInfo(
    val title: String,
    val imageRes: String,
)
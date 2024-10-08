package com.kimnlee.onboard.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack

@Composable
fun OnBoardVehicleScreen(
    onNavigateVehicleRegistration: () -> Unit,
    onNavigateMemberInvite: () -> Unit,
    cardsToRegister: List<RegisterCardRequest>,
    onNavigateBack: () -> Unit

) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 6.dp, end = 20.dp, bottom = 24.dp)
            .zIndex(1f)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "뒤로 가기",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )
        }

        Text(
            text = "차량을 등록 하시겠어요?",
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.pbold)),
            color = MobiTextAlmostBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
//        Text(
//            text = "\uD83D\uDE99",
//            modifier = Modifier
//                .background(Color.Transparent),
//            fontFamily = FontFamily(
//                Font(R.font.emoji)
//            )
//        )
        Image(painter = painterResource(id = com.kimnlee.onboard.R.drawable.car_example), contentDescription = "차 사진", modifier = Modifier.weight(1f).fillMaxSize())

        Button(
            onClick = onNavigateVehicleRegistration,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "내 차를 등록할래요!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = onNavigateMemberInvite,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "다른 차량 멤버로 초대받을래요!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

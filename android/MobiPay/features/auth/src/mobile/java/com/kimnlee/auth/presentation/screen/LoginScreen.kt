package com.kimnlee.auth.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    val registrationButtonColor = Color(0xFF3182F6)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onNavigateToHome()
        }
    }

    MobiPayTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MobiBgWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "창문,",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 40.sp,
                        fontFamily = FontFamily(Font(R.font.psemibold)),
                        color = MobiTextDarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "내리지 마세요!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 40.sp,
                        fontFamily = FontFamily(Font(R.font.psemibold)),
                        color = MobiTextDarkGray
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 2.dp)
                    ) {
                        Text(
                            text = "모비페이",
                            style = MaterialTheme.typography.bodyMedium,
                            color = registrationButtonColor,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = " 회원이라면 누구나 간편하게",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "번호판 인식만으로 결제할 수 있어요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MobiTextAlmostBlack,
                        fontSize = 18.sp,
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))

                Image(
                    painter = painterResource(id = R.drawable.no_car_window),
                    contentDescription = "창문 내리지 마세요 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    contentScale = ContentScale.FillWidth
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kakao_login_large_wide),
                        contentDescription = "카카오 로그인",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.login(context as Activity) }
                    )
                }
            }
        }
    }
}
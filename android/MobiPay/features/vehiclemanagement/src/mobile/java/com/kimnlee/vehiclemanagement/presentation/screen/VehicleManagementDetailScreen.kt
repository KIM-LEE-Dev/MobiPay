package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel

//여기가 메인입니다(파일 이름 수정해야함, 안 바꾸고 합의해도 됨 아니면 여기 코드를 메인모듈로 옮기거나)

@Composable
fun VehicleManagementDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    val vehicle = viewModel.getVehicleById(vehicleId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        vehicle?.let {
            Text(
                text = "MobiPay",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = it.imageResId),
                contentDescription = "Vehicle Image",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = "차량 번호: ${it.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text( // 테스트용 실제 통신 구현시 확인 후 삭제 예정
                text = "차량 ID: ${it.id}",
                style = MaterialTheme.typography.bodyLarge
            )
        } ?: Text("차량을 찾을 수 없습니다.")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("멤버들의 프로필 이미지 공간") //추후 프로필 이미지를 넣을 공간(더하기 버튼을 통해 멤버추가 버튼도 만들어야함(멤버추가 모듈로 넘어가게 할듯))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(50.dp) // 추후 높이 조절 추가
        ) {
            Text("카드 프로필 이미지 공간", modifier = Modifier.align(Alignment.Center)) //추후 카드 이미지를 넣을 공간
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }
    }
}
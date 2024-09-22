package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import com.kimnlee.vehiclemanagement.presentation.viewmodel.Vehicle


@Composable
fun VehicleManagementScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    println("Vehicle list size: ${vehicles.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "차량 관리",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 누르면 각 차량의 상세 페이지로 이동하도록 추후 추가
        LazyColumn {
            items(vehicles) { vehicle ->
                VehicleItem(vehicle, onClick = { onNavigateToDetail(vehicle.id) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToRegistration) {
            Text("차량 등록")
        }

        Spacer(modifier = Modifier.height(8.dp))

//        Button(onClick = onNavigateToDetail) {
//            Text("상세 화면으로 이동")
//        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNavigateToHome) {
            Text("홈으로 돌아가기")
        }
    }
}

// 실제로는 DB에 요청해서 리스트를 받아옴
@Composable
fun VehicleItem(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally // 콘텐츠를 수평으로 중앙 정렬
        ) {
            // 이미지
            Image(
                painter = painterResource(id = vehicle.imageResId),
                contentDescription = "Vehicle Image",
                modifier = Modifier
                    .size(100.dp)
            )

            // 차량 이름
            Text(
                text = vehicle.name,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
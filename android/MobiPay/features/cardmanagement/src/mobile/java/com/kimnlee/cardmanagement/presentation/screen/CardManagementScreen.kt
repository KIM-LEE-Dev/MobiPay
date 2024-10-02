package com.kimnlee.cardmanagement.presentation.screen

import RegisteredCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.components.CardManagementBottomSheet
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.RegistratedCardState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateToOwnedCards: () -> Unit,
    viewModel: CardManagementViewModel = viewModel(),
) {
    val scrollState = rememberScrollState()
    val registeredCardState by viewModel.registratedCardState.collectAsState()
    val registeredCards by viewModel.registeredCards.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "등록된 카드 확인",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))
        when (val state = registeredCardState) {
            is RegistratedCardState.Loading -> {
                CircularProgressIndicator()
            }

            is RegistratedCardState.Success -> {
                if (registeredCards.isEmpty()) {
                    Text("등록된 카드가 없습니다.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(registeredCards) { card ->
                            CardItem(
                                card,
                                onNavigateToDetail,
                                painterResource(id = R.drawable.bc_baro),
                                "카드"
                            )
                        }
                    }
                }
                AddCardButton { viewModel.openBottomSheet() }
                if (showBottomSheet) {
                    CardManagementBottomSheet(
                        sheetState = sheetState,
                        scope = scope,
                        viewModel = viewModel,
                        onNavigateToRegistration = onNavigateToRegistration,
                        onNavigateToOwnedCards = onNavigateToOwnedCards
                    )
                }
            }

            is RegistratedCardState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.requestRegistrationCards() }) {
                    Text("다시 시도")
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: RegisteredCard,
    onNavigateToDetail: () -> Unit,
    painter: Painter,
    contentDescription: String,
) {
    var imageWidth by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onNavigateToDetail),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        // 이미지의 너비를 얻어 imageWidth에 저장
                        imageWidth = coordinates.size.width
                    },
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(text = "1일 한도")
                        Text(text = "${card.oneDayLimit}")
                    }
                    Row {
                        Text(text = "1회 한도")
                        Text(
                            text =
                            if (card.oneDayLimit != 0) {
                                "${card.oneDayLimit}원"
                            } else {
                                "0원"
                            }
                        )
                    }
                    Row {
                        Text(text = "자동 결제 여부")
                        if (card.autoPayStatus) {
                            Text(text = "0", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCardButton(openBottomSheet: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = openBottomSheet),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Card",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("카드 추가", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

package com.kimnlee.mobipay.presentation.components

import android.net.Uri
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.data.model.OwnedCard
import com.kimnlee.cardmanagement.presentation.screen.CardList
import com.kimnlee.cardmanagement.presentation.screen.LoadingState
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import com.kimnlee.mobipay.presentation.viewmodel.OnboardOwnedCardState

@Composable
fun OnBoardCardRegistrationList(
    viewModel: HomeViewModel,
    onNavigateToCardPage: () -> Unit,
    onNavigateToRegister: (List<OwnedCard>) -> Unit
) {
    val uiState by viewModel.cardState.collectAsState()
    val selectedCards by viewModel.selectedCards.collectAsState()
    val isAllSelected by viewModel.isAllSelected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is OnboardOwnedCardState.Loading -> LoadingState()
            is OnboardOwnedCardState.Success -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { isChecked ->
                            viewModel.toggleAllSelected(isChecked)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3182F6))
                    )
                    Text(
                        text = "전체 선택",
                        style = MaterialTheme.typography.titleMedium,
                        color = MobiTextAlmostBlack,
                        fontFamily = FontFamily(Font(R.font.pbold))
                    )
                }
                CardList(
                    cards = state.cards,
                    selectedCards = selectedCards,
                    onCardSelected = { card, isSelected ->
                        viewModel.toggleCardSelection(card, isSelected)
                    },
                    isCardRegistered = { viewModel.isCardRegistered(it.id) }
                )
            }

            is OnboardOwnedCardState.Error -> {
                // 에러 상태에서는 아무것도 표시하지 않음
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
//                val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
//                val json = Uri.encode(Gson().toJson(cardInfos))
//
//                onNavigateToRegister(cardInfos)
                onNavigateToRegister(selectedCards.toList())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedCards.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "등록하기",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
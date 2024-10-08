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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.cardmanagement.R as CardR
import com.kimnlee.common.R

@Composable
fun AppIntroduction(
    goOwnerPage : () -> Unit,
    goMemberPage : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(vertical = 16.dp),
    ) {
        HeaderSection()
        FeatureItem(FeatureInfo("차량을 등록해주세요.", "Track your daily movement", R.drawable.ray))
        Spacer(modifier = Modifier.padding(12.dp))
        FeatureItem(FeatureInfo("카드를 등록해주세요.", "Track all your workouts", CardR.drawable.bc_kully))
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = goOwnerPage, modifier = Modifier.fillMaxWidth()) {
            Text(text = "오너")
        }
        Button(onClick = goMemberPage, modifier = Modifier.fillMaxWidth()) {
            Text(text = "멤버")
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
        Text(
            text = "Welcome to MobiPay",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(vertical = 32.dp)
        )
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
            Image(
                painter = painterResource(id = feature.imageRes),
                contentDescription = feature.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.title,
                    color = MobiTextAlmostBlack,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = feature.description,
                    color = MobiTextDarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class FeatureInfo(
    val title: String,
    val description: String,
    val imageRes: Int,
)
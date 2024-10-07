package com.kimnlee.mobipay.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kimnlee.mobipay.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray

@Composable
fun OnBoardAppIntroduction() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(vertical = 16.dp),
    ) {
        HeaderSection()
        FeatureItem(FeatureInfo("차량을 등록해주세요.", "Track your daily movement", R.drawable.ray))
        Spacer(modifier = Modifier.padding(12.dp))
        FeatureItem(FeatureInfo("카드를 등록해주세요.", "Track all your workouts", com.kimnlee.cardmanagement.R.drawable.bc_kully))
        Spacer(modifier = Modifier.weight(1f))
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
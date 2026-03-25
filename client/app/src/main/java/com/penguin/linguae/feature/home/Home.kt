package com.penguin.linguae.feature.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.home.components.*
import com.penguin.linguae.feature.home.shapes.CurvedBottomShape

@Preview
@Composable
fun HomeScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f)
        ) {

            // ── 1. Header ──────────────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CurvedBottomShape(),
                    color = PurpleBlueTheme
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .align(Alignment.Center)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(vertical = 32.dp)
                        ) {
                            Greetings("Yunetrea")
                            StreakCard(7, 120)
                        }
                    }
                }
            }

            // ── 2. Tiến độ hôm nay ────────────────────────────────────
            item {
                TodayProgress(0.65f)
            }

            // ── 3. Tiêu đề "Học nhanh" ────────────────────────────────
            item {
                Column (
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppBackground,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)

                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.lightning),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Yellow
                                )
                                Text(
                                    text = "Học nhanh",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    QuickLearn(screenHeight)
                }
            }

        }
    }
}

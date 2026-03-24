package com.penguin.linguae.feature.learning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ✅ data class
data class Card(
    val word: String,
    val meaning: String
)

// ✅ data mẫu
val data = listOf(
    Card("Coffee", "Cà phê"),
    Card("Apple", "Táo"),
    Card("Book", "Cuốn sách"),
    Card("Dog", "Con chó"),
    Card("Cat", "Con mèo"),
    Card("Water", "Nước"),
    Card("House", "Ngôi nhà"),
    Card("Car", "Xe hơi"),
    Card("School", "Trường học"),
    Card("Teacher", "Giáo viên"),
    Card("Student", "Học sinh"),
    Card("Food", "Thức ăn")
)

@Composable
fun FlashcardScreen(
    onBack: () -> Unit
) {
    var showMeaning by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(1) }

    val index = (current - 1).coerceIn(0, data.lastIndex)

    // 🎯 animation quay
    val rotation by animateFloatAsState(
        targetValue = if (showMeaning) 180f else 0f,
        animationSpec = tween(400),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9E7F2))
            .padding(16.dp)
    ) {

        // 🔹 Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Flashcard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Progress
        Text(
            text = "$current / ${data.size} từ",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(80))
                .background(Color(0xFFD6D3E0))
        ) {
            LinearProgressIndicator(
                progress = current.toFloat() / data.size.toFloat(),
                color = Color(0xFFFF8A65),
                trackColor = Color.Transparent,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 🔥 FLASHCARD FLIP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                }
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF3F3F3))
                    .clickable { showMeaning = !showMeaning },
                contentAlignment = Alignment.Center
            ) {

                val isFront = rotation <= 90f

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        rotationY = if (isFront) 0f else 180f
                    }
                ) {

                    Text(
                        text = if (isFront) data[index].word else data[index].meaning,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isFront) "Nhấn để xem nghĩa" else "Nhấn để ẩn nghĩa",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 🔹 Button xem nghĩa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF7B6DFF)
                        )
                    )
                )
                .clickable { showMeaning = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👁 Xem nghĩa",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    if (current < data.size) {
                        current++
                        showMeaning = false
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF8D7DA)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("❌ Chưa nhớ", color = Color(0xFFD9534F))
            }

            Button(
                onClick = {
                    if (current < data.size) {
                        current++
                        showMeaning = false
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4EDDA)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("✔ Đã nhớ", color = Color(0xFF28A745))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() {
    FlashcardScreen(onBack = {})
}
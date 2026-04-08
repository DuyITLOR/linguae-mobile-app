package com.penguin.linguae.feature.profile.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurpleMid
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark

@Composable
fun ProfileLogoutDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFFFCFAFF),
        title = {
            Text(
                text = "Đăng xuất khỏi Linguae?",
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Phiên học hiện tại sẽ kết thúc. Bạn có thể đăng nhập lại bất cứ lúc nào.",
                color = PurpleMid
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
                    contentColor = Color.White
                )
            ) {
                Text("Đăng xuất")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = PurpleLight,
                    contentColor = PurplePrimary
                )
            ) {
                Text("Ở lại", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

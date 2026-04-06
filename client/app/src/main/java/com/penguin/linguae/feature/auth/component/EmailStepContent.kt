package com.penguin.linguae.feature.auth.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PrimaryPurple

@Composable
fun EmailStepContent(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onSendOtp: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
    ) {
        Text("EMAIL")

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = BorderGray,
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSendOtp,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Gửi mã OTP", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Quay lại đăng nhập",
            color = PrimaryPurple,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
                .padding(top = 8.dp)
                .clickable { onNavigateLogin() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailStepContentPreview() {
    EmailStepContent(
        email = "test@example.com",
        onEmailChange = {},
        isLoading = false,
        onSendOtp = {},
        onNavigateLogin = {}
    )
}

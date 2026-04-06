package com.penguin.linguae.feature.auth.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PrimaryPurple


@Composable
fun ResetPasswordStepContent(
    email: String,
    isLoading: Boolean,
    onReset: (String, String) -> Unit,
    onBack: () -> Unit,
    initialOtp: String = "",
    initialNewPassword: String = ""
) {
    var otp by remember { mutableStateOf(initialOtp) }
    var newPassword by remember { mutableStateOf(initialNewPassword) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
    ) {
        Text("MÃ OTP (ĐÃ GỬI ĐẾN $email)")

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = BorderGray,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("MẬT KHẨU MỚI")

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = BorderGray,
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onReset(otp, newPassword) },
            enabled = !isLoading && otp.length == 6 && newPassword.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = Color.White)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Xác nhận đổi mật khẩu", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Nhập lại email",
            color = PrimaryPurple,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
                .clickable { onBack() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPreview() {
    ResetPasswordStepContent(
        email = "lenhutduy@example.com",
        isLoading = false,
        onReset = { _, _ -> },
        onBack = {},
        initialOtp = "123456",
        initialNewPassword = "hunter2"
    )
}

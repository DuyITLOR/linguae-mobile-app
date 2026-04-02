package com.penguin.linguae.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.feature.auth.component.AuthHeader


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var email by remember{ mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            AuthHeader(
                title = "Quên mật khẩu",
                subtitle = "Nhập email để nhận mã OTP",
                height = 250.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                Text("EMAIL")

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    onClick = { /* UI only */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Gửi mã OTP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Quay lại đăng nhập",
                    color = PrimaryPurple,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )


            }

        }
    }
}
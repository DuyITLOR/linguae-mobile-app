package com.penguin.linguae.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.penguin.linguae.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.feature.auth.component.LoginHeader
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.feature.auth.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    // Added default values for viewModel and onNavigateHome to fix the NullPointerException during Preview rendering.
    viewModel: LoginViewModel = viewModel(),
    onNavigateHome: () -> Unit = {}
) {
    val navigateHome by viewModel.navigateHome.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(navigateHome) {
        if (navigateHome) {
            launch {
                snackbarHostState.showSnackbar("Đăng nhập thành công!")
            }

            onNavigateHome()
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LoginHeader()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(30.dp, 20.dp)
        ) {
            Text("EMAIL")

            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = BorderGray,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text("MẬT KHẨU")

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = BorderGray,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Quên mật khẩu ?",
                color =  PrimaryPurple,
                modifier = Modifier.align(Alignment.End),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button (
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                )
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text (
                        "Đăng nhập",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text(" Hoặc ")
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    2.dp,
                    if (viewModel.isLoading) BorderGray.copy(alpha = 0.5f) else BorderGray
                )
            ) {
                Icon (
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Tiếp tục với Google",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Chưa có tài khoản? ")
                Text(
                    "Đăng ký ngay",
                    color = PrimaryPurple
                )
            }
        }
    }

    }
}

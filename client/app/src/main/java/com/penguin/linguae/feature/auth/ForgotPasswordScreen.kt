package com.penguin.linguae.feature.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.feature.auth.component.AuthHeader
import com.penguin.linguae.feature.auth.viewmodel.ForgotPasswordViewModel
import com.penguin.linguae.feature.auth.viewmodel.RecoveryStep
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateLogin: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }

    val navigateLogin by viewModel.navigateLogin.collectAsState()
    val error by viewModel.error
    val isLoading by viewModel.isLoading
    val currentStep by viewModel.step

    // Lắng nghe sự kiện chuyển hướng khi thành công
    LaunchedEffect(navigateLogin) {
        if (navigateLogin) {
            snackbarHostState.showSnackbar("Đã gửi email khôi phục thành công!")
            onNavigateLogin()
            viewModel.resetNavigation()
        }
    }

    // Lắng nghe lỗi từ ViewModel
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    BackHandler(enabled = currentStep == RecoveryStep.RESET_PASSWORD) {
        viewModel.step.value = RecoveryStep.ENTER_EMAIL
    }

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
                subtitle = if (currentStep == RecoveryStep.ENTER_EMAIL) "Nhập email của bạn" else "Nhập mã OTP và mật khẩu mới",
                height = 250.dp
            )

            when (currentStep) {
                RecoveryStep.ENTER_EMAIL -> {
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
                            onClick = {
                                if (email.isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Vui lòng nhập email")
                                    }
                                    return@Button
                                }
                                viewModel.forgotPassword(email)
                            },
                            enabled = !isLoading,
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
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Gửi mã OTP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
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

                RecoveryStep.RESET_PASSWORD -> {

                }
            }

        }
    }
}

package com.penguin.linguae.feature.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.penguin.linguae.BuildConfig
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.feature.auth.component.AuthHeader
import com.penguin.linguae.feature.auth.viewmodel.LoginViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateHome: () -> Unit = {},
    onNavigateRegister: () -> Unit = {},
    onNavigateForgotPassword: () -> Unit = {}
) {
    val navigateHome by viewModel.navigateHome.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("GOOGLE_CONFIG", "Google client ID configured=${BuildConfig.GOOGLE_CLIENT_ID.isNotBlank()}")
    }

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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val googleSignInClient = remember {
        val googleClientId = BuildConfig.GOOGLE_CLIENT_ID.trim()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(googleClientId)
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(
            "GOOGLE_LOGIN",
            "ActivityResult resultCode=${result.resultCode}, hasData=${result.data != null}"
        )

        val data = result.data
        if (data == null) {
            viewModel.error = "Google trả về dữ liệu rỗng (intent null)"
            Log.e("GOOGLE_LOGIN", "Result OK but intent data is null")
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (!idToken.isNullOrBlank()) {
                viewModel.loginWithGoogle(idToken)
            } else {
                viewModel.error = "Không lấy được ID Token. Kiểm tra Web Client ID trong BuildConfig."
                Log.e("GOOGLE_LOGIN", "idToken is null or blank")
            }
        } catch (e: ApiException) {
            val code = e.statusCode
            Log.e("GOOGLE_LOGIN", "ApiException code=$code, message=${e.localizedMessage}", e)
            viewModel.error = when (code) {
                10 -> "Lỗi 10: Sai cấu hình (SHA-1/SHA-256 + Web Client ID)"
                12500 -> "Lỗi 12500: Cấu hình OAuth chưa đúng hoặc thiếu SHA"
                7 -> "Lỗi 7: Không có mạng"
                8 -> "Lỗi 8: Google services tạm thời lỗi, thử lại"
                12501 -> "Bạn đã hủy đăng nhập Google"
                12502 -> "Đăng nhập đang chạy, vui lòng thử lại sau vài giây"
                else -> "Lỗi Google ($code): ${e.localizedMessage ?: "Unknown"}"
            }
        } catch (t: Throwable) {
            Log.e("GOOGLE_LOGIN", "Unexpected error during Google sign-in", t)
            viewModel.error = "Lỗi không xác định: ${t.localizedMessage ?: "Unknown"}"
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        AuthHeader()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 20.dp)
                .padding(bottom = paddingValues.calculateBottomPadding())

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
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = BorderGray,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Quên mật khẩu ?",
                color =  PrimaryPurple,
                modifier = Modifier.align(Alignment.End).clickable{ onNavigateForgotPassword() },
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
                onClick = {
                    if (viewModel.isLoading) return@OutlinedButton

                    val playServiceStatus = GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context)
                    if (playServiceStatus != ConnectionResult.SUCCESS) {
                        viewModel.error = "Google Play Services chưa sẵn sàng trên thiết bị"
                        return@OutlinedButton
                    }

                    // Use signOut to ensure account picker appears without revoking the whole grant.
                    googleSignInClient.signOut().addOnCompleteListener {
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                },
                enabled = !viewModel.isLoading,
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
                    color = PrimaryPurple,
                    modifier = Modifier.clickable{
                        onNavigateRegister()
                    }
                )
            }
        }
    }

    }
}

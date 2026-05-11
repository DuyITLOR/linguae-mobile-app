package com.penguin.linguae.feature.admin.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.User
import com.penguin.linguae.data.model.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserScreen(
    onBack: () -> Unit,
    viewModel: AdminUserViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var userToDelete by remember { mutableStateOf<User?>(null) }
    var userDetailToShow by remember { mutableStateOf<User?>(null) }

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Quản lý người dùng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Tìm kiếm theo email hoặc tên...", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextGray)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextGray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = PurplePrimary,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PurplePrimary,
                    )
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                } else if (uiState.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error ?: "Lỗi", color = Color.Red)
                    }
                } else if (uiState.paginatedUsers.isEmpty() && uiState.searchQuery.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Không tìm thấy người dùng nào",
                                color = TextGray,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Thử tìm kiếm với từ khóa khác",
                                color = TextGray.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        items(uiState.paginatedUsers, key = { it.id }) { user ->
                            UserItemCard(
                                user = user,
                                onClick = { userDetailToShow = user },
                                onDeleteClick = { userToDelete = user },
                                onRoleToggle = {
                                    val newRole = if (user.role == UserRole.ADMIN) UserRole.USER else UserRole.ADMIN
                                    scope.launch {
                                        val err = viewModel.updateUserRole(user.id, newRole)
                                        if (err != null) snackbarHostState.showSnackbar(err)
                                        else snackbarHostState.showSnackbar("Đã thay đổi quyền thành công")
                                    }
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Pagination Controls
                    if (uiState.totalPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.setPage(uiState.currentPage - 1) },
                                enabled = uiState.currentPage > 1
                            ) {
                                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous")
                            }
                            Text(
                                text = "Trang ${uiState.currentPage} / ${uiState.totalPages}",
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            IconButton(
                                onClick = { viewModel.setPage(uiState.currentPage + 1) },
                                enabled = uiState.currentPage < uiState.totalPages
                            ) {
                                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next")
                            }
                        }
                    }
                }
            }

            // Action Loading Overlay
            if (uiState.isActionLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }

    // Delete Confirmation Dialog
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Xoá người dùng", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xoá người dùng ${userToDelete?.email} không?") },
            confirmButton = {
                TextButton(onClick = {
                    val id = userToDelete?.id
                    userToDelete = null
                    if (id != null) {
                        scope.launch {
                            val err = viewModel.deleteUser(id)
                            if (err != null) snackbarHostState.showSnackbar(err)
                            else snackbarHostState.showSnackbar("Đã xoá người dùng thành công")
                        }
                    }
                }) {
                    Text("Xoá", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Huỷ", color = TextDark)
                }
            }
        )
    }

    // Detail Dialog
    if (userDetailToShow != null) {
        AlertDialog(
            onDismissRequest = { userDetailToShow = null },
            title = { Text("Chi tiết người dùng", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ID: ${userDetailToShow?.id}", fontSize = 13.sp, color = TextGray)
                    Text("Tên: ${userDetailToShow?.fullName}", fontWeight = FontWeight.SemiBold)
                    Text("Email: ${userDetailToShow?.email}")
                    Text("Role: ${userDetailToShow?.role?.name}", color = PurplePrimary, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { userDetailToShow = null }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun UserItemCard(
    user: User,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRoleToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Default Avatar",
                        tint = TextGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1
                )
                Text(
                    text = user.email,
                    fontSize = 13.sp,
                    color = TextGray,
                    maxLines = 1
                )
                Text(
                    text = "Role: ${user.role.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (user.role == UserRole.ADMIN) Color(0xFFE53935) else PurplePrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRoleToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.role == UserRole.ADMIN) AppBackground else PurplePrimary,
                        contentColor = if (user.role == UserRole.ADMIN) PurplePrimary else Color.White
                    ),
                    modifier = Modifier.height(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (user.role == UserRole.ADMIN) "Make USER" else "Make ADMIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE53935).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

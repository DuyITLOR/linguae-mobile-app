package com.penguin.linguae.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.data.model.ProfileUiState
import com.penguin.linguae.feature.profile.component.ProfileEditContent
import com.penguin.linguae.feature.profile.viewmodel.ProfileViewModel

@Composable
fun ProfileEditScreen(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var draftFullName by rememberSaveable(uiState.fullName) {
        mutableStateOf(uiState.fullName)
    }
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    LaunchedEffect(viewModel.saveCompleted) {
        if (viewModel.saveCompleted) {
            viewModel.consumeSaveCompleted()
            onBack()
        }
    }

    val canSave = draftFullName.trim().isNotEmpty() &&
            (draftFullName.trim() != uiState.fullName || selectedAvatarUri != null)

    Scaffold(
        containerColor = PageBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        ProfileEditContent(
            uiState = uiState,
            draftFullName = draftFullName,
            selectedAvatarUri = selectedAvatarUri,
            isSaving = viewModel.isSaving,
            canSave = canSave,
            onFullNameChange = { draftFullName = it },
            onPickAvatar = { imagePickerLauncher.launch("image/*") },
            onCancel = onBack,
            onSave = {
                viewModel.updateProfile(
                    context = context,
                    fullName = draftFullName,
                    avatarUri = selectedAvatarUri,
                )
            },
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}

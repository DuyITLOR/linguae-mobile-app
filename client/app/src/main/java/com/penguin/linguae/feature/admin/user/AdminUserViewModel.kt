package com.penguin.linguae.feature.admin.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.User
import com.penguin.linguae.data.model.UserRole
import com.penguin.linguae.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

data class AdminUserUiState(
    val isLoading: Boolean = true,
    val allUsers: List<User> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val currentPage: Int = 1,
    val itemsPerPage: Int = 10,
    val isActionLoading: Boolean = false
) {
    private val filteredUsers: List<User>
        get() {
            if (searchQuery.isBlank()) return allUsers
            val q = searchQuery.lowercase()
            return allUsers.filter { it.email.lowercase().contains(q) || it.fullName.lowercase().contains(q) }
        }

    val totalPages: Int
        get() = maxOf(1, ceil(filteredUsers.size.toDouble() / itemsPerPage).toInt())

    val paginatedUsers: List<User>
        get() {
            val startIndex = (currentPage - 1) * itemsPerPage
            val endIndex = minOf(startIndex + itemsPerPage, filteredUsers.size)
            if (startIndex >= filteredUsers.size) return emptyList()
            return filteredUsers.subList(startIndex, endIndex)
        }
}

class AdminUserViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AdminUserUiState())
    val uiState: StateFlow<AdminUserUiState> = _uiState.asStateFlow()

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getAllUsers()
            result.onSuccess { users ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        allUsers = users,
                        // Ensure current page is valid
                        currentPage = if (state.currentPage > ceil(users.size.toDouble() / state.itemsPerPage).toInt()) 1 else state.currentPage
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Lỗi khi tải danh sách người dùng"
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
    }

    fun setPage(page: Int) {
        val totalPages = _uiState.value.totalPages
        val validPage = page.coerceIn(1, totalPages)
        _uiState.update { it.copy(currentPage = validPage) }
    }

    suspend fun updateUserRole(id: String, newRole: UserRole): String? {
        _uiState.update { it.copy(isActionLoading = true) }
        val result = userRepository.updateUserRole(id, newRole)
        _uiState.update { it.copy(isActionLoading = false) }
        
        return if (result.isSuccess) {
            // Update local list
            _uiState.update { state ->
                val updatedList = state.allUsers.map { user ->
                    if (user.id == id) user.copy(role = newRole) else user
                }
                state.copy(allUsers = updatedList)
            }
            null // no error
        } else {
            result.exceptionOrNull()?.message ?: "Có lỗi xảy ra khi cập nhật quyền"
        }
    }

    suspend fun deleteUser(id: String): String? {
        _uiState.update { it.copy(isActionLoading = true) }
        val result = userRepository.deleteUser(id)
        _uiState.update { it.copy(isActionLoading = false) }
        
        return if (result.isSuccess) {
            // Update local list
            _uiState.update { state ->
                val updatedList = state.allUsers.filter { it.id != id }
                val newTotalPages = maxOf(1, ceil(updatedList.size.toDouble() / state.itemsPerPage).toInt())
                val newPage = if (state.currentPage > newTotalPages) newTotalPages else state.currentPage
                state.copy(allUsers = updatedList, currentPage = newPage)
            }
            null // no error
        } else {
            result.exceptionOrNull()?.message ?: "Có lỗi xảy ra khi xoá người dùng"
        }
    }
}

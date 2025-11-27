package com.malikparteek.cumess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikparteek.cumess.data.MenuUpdate
import com.malikparteek.cumess.data.UpdatesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdatesViewModel(
    private val repo: UpdatesRepository = UpdatesRepository()
) : ViewModel() {

    private val _updates = MutableStateFlow<List<MenuUpdate>>(emptyList())
    val updates: StateFlow<List<MenuUpdate>> = _updates

    private val _unread = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unread

    private val _error = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            repo.observeUpdates().collectLatest { result ->
                result.fold(onSuccess = { list ->
                    _error.value = null
                    _updates.value = list
                    _unread.value = list.count { !it.isRead }
                }, onFailure = { ex ->
                    _updates.value = emptyList()
                    _unread.value = 0
                    _error.value = ex.message ?: "Unknown Firestore error"
                })
            }
        }
    }

    fun markAllAsRead() {
        val unreadIds = _updates.value.filter { !it.isRead }.map { it.id }
        if (unreadIds.isEmpty()) return
        viewModelScope.launch {
            try {
                repo.markAsRead(unreadIds)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun markOneAsRead(id: String) {
        viewModelScope.launch {
            try {
                repo.markAsRead(listOf(id))
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

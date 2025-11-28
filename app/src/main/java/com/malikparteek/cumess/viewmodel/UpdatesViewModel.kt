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

    private val _error = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            repo.observeUpdates().collectLatest { result ->
                result.fold(onSuccess = { list ->
                    _error.value = null
                    _updates.value = list
                }, onFailure = { ex ->
                    _updates.value = emptyList()
                    _error.value = ex.message ?: "Unknown Firestore error"
                })
            }
        }
    }

    // NOTE: No markAllAsRead(), no per-document isRead modifications.
    // This ViewModel simply exposes the updates list and any error.
}

package com.malikparteek.cumess.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.malikparteek.cumess.screen.Menu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class MenuViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _menu = MutableLiveData<Menu>()
    val menu: LiveData<Menu> = _menu

    private val _tomorrowMenu = MutableLiveData<Menu>()
    val tomorrowMenu: LiveData<Menu> = _tomorrowMenu

    // Overmorrow (day after tomorrow)
    private val _overmorrowMenu = MutableLiveData<Menu>()
    val overmorrowMenu: LiveData<Menu> = _overmorrowMenu

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _internationalMenu = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val internationalMenu: StateFlow<Map<String, List<String>>> = _internationalMenu

    private val _tomorrowInternationalMenu = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val tomorrowInternationalMenu: StateFlow<Map<String, List<String>>> = _tomorrowInternationalMenu

    fun fetchTodayMenu() {
        _isLoading.value = true
        val todayDate = getCurrentDate(0)
        fetchMenuFromDate("meals", todayDate) { menu ->
            _menu.value = menu
            _isLoading.value = false
        }
    }

    fun fetchTomorrowMenu() {
        _isLoading.value = true
        val tomorrowDate = getCurrentDate(1)
        fetchMenuFromDate("meals", tomorrowDate) { menu ->
            _tomorrowMenu.value = menu
            _isLoading.value = false
        }
    }

    fun fetchOvermorrowMenu() {
        _isLoading.value = true
        val overmorrowDate = getCurrentDate(2)
        fetchMenuFromDate("meals", overmorrowDate) { menu ->
            _overmorrowMenu.value = menu
            _isLoading.value = false
        }
    }

    fun fetchInternationalMenu(date: String) {
        fetchRawMenuList("international_meals", date) {
            _internationalMenu.value = it
        }
    }

    fun fetchTomorrowInternationalMenu(date: String = getCurrentDate(1)) {
        fetchRawMenuList("international_meals", date) {
            _tomorrowInternationalMenu.value = it
        }
    }

    private fun getCurrentDate(offsetDays: Int): String {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, offsetDays)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun fetchMenuFromDate(path: String, date: String, callback: (Menu) -> Unit) {
        firestore.collection(path).document(date).get()
            .addOnSuccessListener { document ->
                val menu = document.toObject<Menu>() ?: Menu()
                callback(menu)
            }
            .addOnFailureListener {
                callback(Menu())
            }
    }

    private fun fetchRawMenuList(path: String, date: String, callback: (Map<String, List<String>>) -> Unit) {
        firestore.collection(path).document(date).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dataMap = mutableMapOf<String, List<String>>()
                    document.data?.forEach { (key, value) ->
                        if (value is List<*>) {
                            dataMap[key] = value.filterIsInstance<String>()
                        }
                    }
                    callback(dataMap)
                } else {
                    callback(emptyMap())
                }
            }
            .addOnFailureListener {
                callback(emptyMap())
            }
    }
}

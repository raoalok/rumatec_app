package com.js_loop_erp.components.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DbViewModel(private val repository: DbRepository) : ViewModel() {
    fun insertData(data: EmpLocationData) {
        viewModelScope.launch {
            repository.insertData(data)
        }
    }

    fun getAllData() {
        viewModelScope.launch {
            val data = repository.getData()
            // Handle data in the UI
        }
    }
}
package com.ohieku.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Repositories return failures as ResourceState.Error, so errors are surfaced through UI state
    protected fun loadData(block: suspend () -> Unit) = viewModelScope.launch {
        block()
    }
}

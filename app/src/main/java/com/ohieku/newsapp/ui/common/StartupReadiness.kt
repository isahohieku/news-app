package com.ohieku.newsapp.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lets the splash screen wait for the first screen's content without the Activity
 * owning that screen's ViewModel. The first screen calls [markReady] once it has something to show.
 */
@Singleton
class StartupReadiness @Inject constructor() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun markReady() {
        _isReady.value = true
    }
}

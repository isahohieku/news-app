package com.ohieku.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.data.network.ApiException
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseViewModel() : ViewModel() {

    private val _error = MutableLiveData<Event<ApiException>>()
    val error: LiveData<Event<ApiException>>
        get() = _error

    protected fun loadData(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
        } catch (e: ApiException) {
            _error.postValue(Event(e))
        } catch (e: Exception) {
            if (e !is SocketTimeoutException && e !is UnknownHostException) {
                _error.postValue(Event(ApiException("Can't connect, please try again later")))
            } else {
                _error.postValue(Event(ApiException("No Internet, please check and try again")))
            }
        }
    }
}
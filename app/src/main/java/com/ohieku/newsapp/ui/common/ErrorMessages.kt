package com.ohieku.newsapp.ui.common

import androidx.annotation.StringRes
import com.ohieku.newsapp.R
import com.ohieku.newsapp.data.network.ErrorType

/** Short line for snackbars, page footers and the error title. */
@StringRes
fun ErrorType.messageRes(): Int = when (this) {
    ErrorType.NoInternet -> R.string.error_no_internet
    ErrorType.Timeout -> R.string.error_timeout
    ErrorType.CannotConnect -> R.string.error_cannot_connect
    ErrorType.Unavailable -> R.string.error_unavailable
    ErrorType.RateLimited -> R.string.error_rate_limited
    ErrorType.Server -> R.string.error_server
    ErrorType.BadRequest,
    ErrorType.NotFound,
    ErrorType.Unknown -> R.string.error_generic
}

/** Supporting line under the full-screen error title: what to do next. */
@StringRes
fun ErrorType.bodyRes(): Int = when (this) {
    ErrorType.NoInternet -> R.string.error_no_internet_body
    ErrorType.Timeout -> R.string.error_timeout_body
    ErrorType.Unavailable -> R.string.error_unavailable_body
    ErrorType.RateLimited -> R.string.error_rate_limited_body
    ErrorType.CannotConnect,
    ErrorType.Server,
    ErrorType.BadRequest,
    ErrorType.NotFound,
    ErrorType.Unknown -> R.string.error_generic_body
}

val ErrorType.isConnectivity: Boolean
    get() = this == ErrorType.NoInternet || this == ErrorType.Timeout || this == ErrorType.CannotConnect

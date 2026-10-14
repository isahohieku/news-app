package com.ohieku.newsapp.ui.common

import androidx.annotation.StringRes
import com.ohieku.newsapp.R
import com.ohieku.newsapp.data.network.ErrorType

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

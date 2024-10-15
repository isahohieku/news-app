package com.ohieku.newsapp.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

open class Event<out T>(private val content: T) {

    var hasBeenConsumed: Boolean = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotConsumed(): T? {
        return if (hasBeenConsumed) {
            null
        } else {
            hasBeenConsumed = true
            content
        }
    }

    /**
     *  Return the content, even if it has already been consumed.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenConsumed=$hasBeenConsumed)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Event<*>) {
            return false
        }

        return this.content == other.content
                && this.hasBeenConsumed == other.hasBeenConsumed
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + hasBeenConsumed.hashCode()
        return result
    }
}

inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit,
) {
    observe(owner) { event ->
        event?.getContentIfNotConsumed()
            ?.let(onEventUnhandledContent)
    }
}
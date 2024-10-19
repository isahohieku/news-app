package com.ohieku.newsapp.core.provider

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleProvider @Inject constructor() {
    fun getDefaultLocale(): Locale  = Locale.getDefault()
}
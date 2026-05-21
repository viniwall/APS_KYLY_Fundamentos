package br.com.kollectaops.collector.presentation.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeModule(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)

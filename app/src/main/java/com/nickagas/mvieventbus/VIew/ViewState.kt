package com.nickagas.mvieventbus.VIew

import android.view.View

sealed class ViewState {
    object LoadingState : ViewState()
    data class DataState(val greeting: String) : ViewState()
    data class ErrorState(val error: String) : ViewState()
}
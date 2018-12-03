package com.nickagas.mvieventbus.VIew

import icepick.State

interface IView {

    //intents
    fun sayHiBtnIntent()
    fun showLoadingIntent()
    fun clearIntent()

    fun render(state: ViewState)
}
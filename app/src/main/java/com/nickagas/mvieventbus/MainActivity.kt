package com.nickagas.mvieventbus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nickagas.mvieventbus.Model.disposable
import com.nickagas.mvieventbus.Model.wikiApiServe
import com.nickagas.mvieventbus.VIew.IView
import com.nickagas.mvieventbus.VIew.ViewState
import com.nickagas.mvieventbus.ViewsState.lastState
import icepick.Icepick
import icepick.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.EventBus







class MainActivity : AppCompatActivity(), IView {




//    var data by state<String?>(null) // This will be automatically saved and restored


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main)

        loadingBtn.setOnClickListener {
            showLoadingIntent()
        }
        clearBtn.setOnClickListener {
            clearIntent()
        }
        hiBtn.setOnClickListener {
            sayHiBtnIntent()
        }
        loadingProgressBar.visibility = View.GONE
        textView.visibility = View.GONE

        lastState?.run {
            render(this)
        }

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }


    override fun showLoadingIntent() {

        EventBus.getDefault().post(ViewState.LoadingState)

    }

    override fun clearIntent() {
        EventBus.getDefault().post(ViewState.ErrorState("Error"))
    }

    override fun sayHiBtnIntent() {
        beginSearch("trump")
        EventBus.getDefault().post(ViewState.DataState("Hi, greetings"))
    }



    override fun render(state: ViewState) {
        lastState = state
        when(state) {

            is ViewState.LoadingState -> renderLoadingState()
            is ViewState.DataState -> renderDataState(state)
            is ViewState.ErrorState -> renderErrorState(state)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: ViewState) {
        render(state)
    }

    private fun renderErrorState(state: ViewState.ErrorState) {
        textView.text = state.error.toString()
        loadingProgressBar.visibility = View.GONE
        textView.visibility = View.VISIBLE
    }

    private fun renderDataState(dataState: ViewState.DataState) {
       textView.text = dataState.greeting
        textView.visibility = View.VISIBLE
        loadingProgressBar.visibility = View.GONE

    }


    private fun renderLoadingState() {
        loadingProgressBar.visibility = View.VISIBLE
        textView.visibility = View.GONE

    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    private fun beginSearch(srsearch: String) {
        disposable =
                wikiApiServe.hitCountCheck("query", "json", "search", srsearch)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { render(ViewState.LoadingState) }
                    .subscribe(
                        { result -> render(ViewState.DataState("Hi, greetings"+result.query.searchinfo.totalhits ))  },
                        { error -> render(ViewState.ErrorState(error.message.toString())) }
                    )
    }
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}


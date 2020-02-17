package pl.lipov.eventslogger.common.utils

import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlin.math.roundToInt

class GestureDetectorUtils : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    val onEventReceived = MutableLiveData<String>()
    private var gestureDetector: GestureDetector? = null

    override fun onDown(
        event: MotionEvent
    ): Boolean {
        onEventReceived.postValue(event.getFormattedEvent("On down"))
        return super.onDown(event)
    }

    private fun MotionEvent.getFormattedEvent(
        eventName: String
    ) = "$eventName [X: ${x.roundToInt()}; Y: ${y.roundToInt()}]"

    override fun onLongPress(
        event: MotionEvent
    ) {
        onEventReceived.postValue(event.getFormattedEvent("On long press"))
    }

    override fun onDoubleTapEvent(
        event: MotionEvent
    ): Boolean {
        onEventReceived.postValue(event.getFormattedEvent("On double tap"))
        return super.onDoubleTap(event)
    }

    override fun onTouch(
        view: View,
        event: MotionEvent
    ): Boolean {
        view.performClick()
        return false
    }

    fun initGestureDetector(
        activity: Activity
    ) {
        gestureDetector = GestureDetector(activity, this)
    }

    fun onTouchEvent(
        event: MotionEvent
    ) {
        gestureDetector?.onTouchEvent(event)
    }
}

package pl.lipov.eventslogger.presentation

import android.app.Activity
import android.os.Handler
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pl.lipov.eventslogger.LogsRepository
import pl.lipov.eventslogger.R
import pl.lipov.eventslogger.common.rxEvents.ViewModelEvent
import pl.lipov.eventslogger.common.utils.FileUtils
import pl.lipov.eventslogger.common.utils.GestureDetectorUtils
import pl.lipov.eventslogger.common.utils.PermissionsUtils
import pl.lipov.eventslogger.common.utils.SensorEventsUtils
import java.io.IOException

class MainViewModel(
    private val permissionsUtils: PermissionsUtils,
    private val fileUtils: FileUtils,
    private val gestureDetectorUtils: GestureDetectorUtils,
    private val sensorEventsUtils: SensorEventsUtils,
    private val logsRepository: LogsRepository
) : ViewModel() {

    companion object {
        private const val HTTP_STATUS_CODE_SUCCESS = 200
    }

    val onGestureEventReceived: MutableLiveData<String> = gestureDetectorUtils.onEventReceived
    val onNoAccelerometerDetected: MutableLiveData<Unit> =
        sensorEventsUtils.onNoAccelerometerDetected
    val onSensorEventReceived: MutableLiveData<String> = sensorEventsUtils.onEventReceived
    val eventsLogsFileCreated = ViewModelEvent<Unit>()
    val eventsLogsReceived = ViewModelEvent<List<String>>()
    val eventsLogsSaved = ViewModelEvent<Unit>()
    val eventsLogsUploaded = ViewModelEvent<Unit>()
    val eventsLogsCleared = ViewModelEvent<Unit>()
    val onFoldIn = MutableLiveData<Unit>()
    val onFoldOut = MutableLiveData<Unit>()
    var menu: Menu? = null
    var storagePermissionsGranted = false
    var foldIn = true
    private val compositeDisposable = CompositeDisposable()

    fun onCreate(
        activity: Activity
    ) {
        sensorEventsUtils.initAccelerometer(activity)
        gestureDetectorUtils.initGestureDetector(activity)
        storagePermissionsGranted = permissionsUtils.hasExternalStoragePermissions()
        if (storagePermissionsGranted) {
            createEventsLogsFile()
        } else {
            Toast.makeText(activity, R.string.required_permissions_info, Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                permissionsUtils.requestExternalStoragePermissions(activity)
            }, 3000)
        }
    }

    private fun createEventsLogsFile() {
        compositeDisposable.add(
            fileUtils.createEventsLogsFile().doOnError {
                eventsLogsFileCreated.onError(it)
            }.subscribe()
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (permissionsUtils.isExternalStoragePermissions(requestCode)) {
            storagePermissionsGranted =
                permissionsUtils.externalStoragePermissionsGranted(grantResults)
            if (storagePermissionsGranted) {
                createEventsLogsFile()
            }
        }
    }

    fun logNewEventIntoFile(
        event: String
    ) {
        if (!storagePermissionsGranted) {
            return
        }
        compositeDisposable.add(
            fileUtils.logEventIntoFile(event).doOnError {
                eventsLogsSaved.onError(it)
            }.subscribe()
        )
    }

    fun uploadLogs() {
        uploadLogs(onSuccess = {
            eventsLogsUploaded.onSuccess(Unit)
        }, onError = {
            eventsLogsUploaded.onError(it)
        })
    }

    private fun uploadLogs(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        fileUtils.eventsLogsFile?.let { logsFile ->
            compositeDisposable.add(
                logsRepository.uploadLogs(logsFile).subscribe({
                    if (it.statusCode == HTTP_STATUS_CODE_SUCCESS) {
                        onSuccess()
                    } else {
                        eventsLogsUploaded.onError(Exception("Status code: ${it.statusCode}"))
                    }
                }, {
                    if (it is IOException) {
                        onError(Exception("Check internet connection."))
                    } else {
                        onError(it)
                    }
                })
            )
        }
    }

    fun clearLogs() {
        compositeDisposable.add(
            fileUtils.clearFileContent().subscribe({
                uploadEmptyLogs()
            }, {
                eventsLogsCleared.onError(it)
            })
        )
    }

    private fun uploadEmptyLogs() {
        uploadLogs(onSuccess = {
            eventsLogsCleared.onSuccess(Unit)
        }, onError = {
            eventsLogsCleared.onError(it)
        })
    }

    fun registerSensorEventListener() = sensorEventsUtils.registerSensorEventListener()

    fun getOnTouchListener(): View.OnTouchListener = gestureDetectorUtils

    fun onTouchEvent(
        event: MotionEvent
    ) {
        gestureDetectorUtils.onTouchEvent(event)
    }

    fun toggleFoldMode() {
        if (foldIn) {
            onFoldOut.postValue(Unit)
            getAllLogs()
        } else {
            onFoldIn.postValue(Unit)
        }
        foldIn = !foldIn
    }

    private fun getAllLogs() {
        compositeDisposable.add(
            fileUtils.getAllLogs().subscribe({
                eventsLogsReceived.onSuccess(it)
            }, {
                eventsLogsReceived.onError(it)
            })
        )
    }

    fun onDestroy() {
        compositeDisposable.clear()
        sensorEventsUtils.unregisterSensorEventListener()
    }
}

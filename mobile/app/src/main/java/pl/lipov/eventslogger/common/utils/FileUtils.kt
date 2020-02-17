package pl.lipov.eventslogger.common.utils

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


class FileUtils(
    private val context: Context
) {

    companion object {
        private const val EVENTS_LOGS_FILE_NAME = "logs.txt"
    }

    var eventsLogsFile: File? = null

    fun createEventsLogsFile(): Completable = Completable.create {
        try {
            eventsLogsFile = File(context.getExternalFilesDir(null), EVENTS_LOGS_FILE_NAME)
            eventsLogsFile?.let { file ->
                if (!file.exists()) {
                    file.createNewFile()
                }
            }
            it.onComplete()
        } catch (exception: Exception) {
            it.onError(exception)
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun logEventIntoFile(
        event: String
    ): Completable = Completable.create {
        try {
            eventsLogsFile?.also { file ->
                file.appendText("$event\n")
            }
            it.onComplete()
        } catch (exception: Exception) {
            it.onError(exception)
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getAllLogs(): Single<List<String>> = Single.create<List<String>> {
        try {
            if (eventsLogsFile == null) {
                it.onError(Exception("Cannot read logs."))
                return@create
            }
            eventsLogsFile?.also { file ->
                it.onSuccess(file.readText().split("\n"))
            }
        } catch (exception: Exception) {
            it.onError(exception)
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun clearFileContent(): Completable = Completable.create {
        try {
            eventsLogsFile?.printWriter()?.flush()
            it.onComplete()
        } catch (exception: Exception) {
            it.onError(exception)
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

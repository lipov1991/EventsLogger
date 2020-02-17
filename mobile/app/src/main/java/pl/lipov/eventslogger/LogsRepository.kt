package pl.lipov.eventslogger

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.lipov.eventslogger.api.Api
import pl.lipov.eventslogger.api.SaveLogsResponse
import java.io.File

class LogsRepository(
    private val api: Api
) {

    fun uploadLogs(
        logsFile: File
    ): Single<SaveLogsResponse> {
        val requestBody = RequestBody.create(MediaType.parse("*/*"), logsFile)
        val part = MultipartBody.Part.createFormData("logs", logsFile.name, requestBody)
        return api.uploadLogs(part, requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

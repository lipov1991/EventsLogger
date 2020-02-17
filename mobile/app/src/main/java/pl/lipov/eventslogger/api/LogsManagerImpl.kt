package pl.lipov.eventslogger.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LogsManagerImpl(
    private val api: Api
) : LogsManager {

    override fun saveLogs(
        file: MultipartBody.Part,
        logs: RequestBody
    ): Single<SaveLogsResponse> = api.uploadLogs(file, logs)
}

package pl.lipov.eventslogger.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface LogsManager {

    fun saveLogs(
        file: MultipartBody.Part,
        logs: RequestBody
    ): Single<SaveLogsResponse>
}

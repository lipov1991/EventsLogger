package pl.lipov.eventslogger.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {

    @POST("/EventsLogger/uploadLogs.php")
    @Multipart
    fun uploadLogs(
        @Part part: MultipartBody.Part,
        @Part("logs") requestBody: RequestBody
    ): Single<SaveLogsResponse>
}

package pl.lipov.eventslogger

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.lipov.eventslogger.api.Api
import pl.lipov.eventslogger.common.utils.FileUtils
import pl.lipov.eventslogger.common.utils.GestureDetectorUtils
import pl.lipov.eventslogger.common.utils.PermissionsUtils
import pl.lipov.eventslogger.common.utils.SensorEventsUtils
import pl.lipov.eventslogger.presentation.MainViewModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val API_ENDPOINT = "http://apka.targislubne.pl/"

val utilsModule = module {
    single { PermissionsUtils(context = get()) }
    single { FileUtils(context = get()) }
    single { GestureDetectorUtils() }
    single { SensorEventsUtils() }
}

val networkModule = module {
    factory { provideOkHttpClient() }
    single { provideGson() }
    single { provideRetrofit(okHttpClient = get(), gson = get()) }
    factory { provideApi(get()) }
}

private fun provideOkHttpClient(): OkHttpClient = OkHttpClient().newBuilder().build()

private fun provideGson(): Gson = GsonBuilder().serializeNulls().setLenient().create()

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gson: Gson
): Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(API_ENDPOINT)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

private fun provideApi(
    retrofit: Retrofit
): Api = retrofit.create(Api::class.java)

val repositoriesModule = module {
    factory { LogsRepository(api = get()) }
}

val viewModelsModule = module {
    viewModel {
        MainViewModel(
            permissionsUtils = get(),
            fileUtils = get(),
            gestureDetectorUtils = get(),
            sensorEventsUtils = get(),
            logsRepository = get()
        )
    }
}

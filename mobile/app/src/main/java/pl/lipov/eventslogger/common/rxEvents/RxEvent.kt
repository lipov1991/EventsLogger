package pl.lipov.eventslogger.common.rxEvents

interface RxEvent<T> {

    fun onSuccess(
            t: T
    )

    fun onError(
            throwable: Throwable
    )
}

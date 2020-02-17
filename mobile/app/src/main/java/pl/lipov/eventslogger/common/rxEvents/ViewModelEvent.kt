package pl.lipov.eventslogger.common.rxEvents

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class ViewModelEvent<T> : RxEvent<T> {

    private var errorRelay: PublishRelay<Throwable> = PublishRelay.create()
    private var successRelay: PublishRelay<T> = PublishRelay.create()

    fun getSuccessStream(): Flowable<T> = successRelay.toFlowable(BackpressureStrategy.LATEST)

    fun getErrorStream(): Flowable<Throwable> = errorRelay.toFlowable(BackpressureStrategy.LATEST)

    override fun onSuccess(
        t: T
    ) = successRelay.accept(t)

    override fun onError(
        throwable: Throwable
    ) = errorRelay.accept(throwable)
}

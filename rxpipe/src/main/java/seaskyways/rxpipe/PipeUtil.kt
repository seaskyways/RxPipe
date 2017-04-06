package seaskyways.rxpipe

import io.reactivex.*
import io.reactivex.subjects.AsyncSubject
import seaskyways.rxpipe.spec.*
import java.lang.ref.SoftReference
import kotlin.reflect.KProperty

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */

fun <T> T.pipeStartPoint(namespace: String, volatility: Int = 1): PipeStartPoint<T> {
    var valRef = SoftReference(this)
    return object : PipeStartPoint<T> {
        private var mDisposed = false
        private val mDisposalSubject = AsyncSubject.create<Nothing>()
        
        override var volatility: Int = volatility
        
        override fun isDisposed() = mDisposed
        
        override val disposalObservable: Observable<Nothing> = mDisposalSubject.hide()
        
        override fun dispose() {
            mDisposalSubject.onComplete()
            valRef = SoftReference(null)
            mDisposed = true
        }
        
        override val namespace: String = namespace
        
        override fun provide(): Single<T> {
            if ((mDisposed && valRef.get() != null)) {
                throw PipeEmptyException()
            } else {
                this.volatility--
                val tempValue = valRef.get()
                if (this.volatility == 0) {
                    dispose()
                }
                return Single.just(tempValue)
            }
        }
    }
}

fun <T> pipeEndPoint(namespace: String, volatility: Int = 1): PipeEndPoint<T> {
    return object : PipeEndPoint<T> {
        override var value: T? = null
        private var mDisposed = false
        private val mDisposalSubject = AsyncSubject.create<Nothing>()
        
        override fun isDisposed() = mDisposed
        
        override val disposalObservable: Observable<Nothing> = mDisposalSubject.hide()
        
        override var volatility: Int = volatility
        
        override fun dispose() {
            mDisposalSubject.onComplete()
            mDisposed = true
        }
        
        override val namespace: String = namespace
        
        override fun request(emitter: Single<T>) {
            emitter.subscribe { emitted -> value = emitted }
        }
    }
}

operator fun <T> PipeEndPoint<T>.getValue(thisRef: Any?, prop: KProperty<*>): T? {
    return value
}

operator fun <T> PipeEndPoint<T>.component0() = this
operator fun <T> PipeEndPoint<T>.component1() = value
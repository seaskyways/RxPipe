package seaskyways.rxpipe

import io.reactivex.*
import io.reactivex.subjects.AsyncSubject
import seaskyways.rxpipe.spec.*
import java.lang.ref.SoftReference
import kotlin.reflect.KProperty

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */

fun <T> T.pipeStartPoint(namespace: String, initialVolatility: Int = 1, registerNow: Boolean = true): PipeStartPoint<T> {
    var valRef = SoftReference(this)
    val pipePoint = object : PipeStartPoint<T> {
        private var mDisposed = false
        private val mDisposalSubject = AsyncSubject.create<Nothing>()
        
        override var volatility: Int = initialVolatility
        
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
    if (registerNow) RxPipeManager.register<T>(pipePoint)
    return pipePoint
}

fun <T> pipeEndPoint(namespace: String, initialVolatility: Int = 1, registerNow: Boolean = true): PipeEndPoint<T> {
    val pipePoint = object : PipeEndPoint<T> {
        override var value: T? = null
        private var mDisposed = false
        private val mDisposalSubject = AsyncSubject.create<Nothing>()
        
        override fun isDisposed() = mDisposed
        
        override val disposalObservable: Observable<Nothing> = mDisposalSubject.hide()
        
        override var volatility: Int = initialVolatility
        
        override fun dispose() {
            mDisposalSubject.onComplete()
            print("disposal subject complete : ${mDisposalSubject.hasComplete()}")
            mDisposed = true
        }
        
        override val namespace: String = namespace
        
        override fun request(emitter: Single<T>) {
            checkVolatile {
                emitter.subscribe { emitted -> value = emitted }
            }
        }
        
        fun checkVolatile(notVolatile: () -> Unit) {
            disposeIfVolatile()
            if (!mDisposed) {
                this.volatility--
                notVolatile()
                disposeIfVolatile()
            }
        }
        
        fun disposeIfVolatile() {
            if (this.volatility == 0) {
                this.dispose()
            }
        }
    }
    if (registerNow) RxPipeManager.register<T>(pipePoint)
    return pipePoint
}

class QuickEndPipe<out T> {
    private var pipeRef: SoftReference<PipeEndPoint<T>?> = SoftReference(null)
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T? {
        return pipeRef.get()?.value ?: (pipeEndPoint<T>(prop.name).also { pipeRef = SoftReference(it) }.run { value })
    }
}

inline fun <T> quickEndPipe() = QuickEndPipe<T>()
fun <T> T.quickStartPipe(prop: KProperty<T>) = pipeStartPoint(prop.name)

fun <T> KProperty<T>.pipeEndPoint(volatility: Int = 1, registerNow: Boolean = true): PipeEndPoint<T>
        = pipeEndPoint(this.name, volatility, registerNow)

operator fun <T> PipeEndPoint<T>.getValue(thisRef: Any?, prop: KProperty<*>): T? {
    return value
}

operator fun <T> PipeEndPoint<T>.component0() = this
operator fun <T> PipeEndPoint<T>.component1() = value
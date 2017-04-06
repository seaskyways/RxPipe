package seaskyways.rxpipe.spec

import io.reactivex.disposables.Disposable
import seaskyways.rxpipe.PipeNotFoundException

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
class Pipe<T>(
        val startPoint: PipeStartPoint<T>,
        val endPoint: PipeEndPoint<T>
) : Disposable {
    val namespace: String = run {
        if (startPoint.namespace != startPoint.namespace) throw PipeNotFoundException("Invalid pipe has been made")
        return@run startPoint.namespace
    }
    
    override fun dispose() {
        startPoint.dispose()
        endPoint.dispose()
    }
    
    override fun isDisposed() = startPoint.isDisposed && endPoint.isDisposed
}

fun <T> Pipe<T>.send() {
    endPoint.request(startPoint.provide())
}
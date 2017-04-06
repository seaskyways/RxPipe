package seaskyways.rxpipe.spec

import io.reactivex.Single

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
interface PipeEndPoint<T> : PipePoint {
    var value: T?
    fun request(emitter: Single<T>)
}
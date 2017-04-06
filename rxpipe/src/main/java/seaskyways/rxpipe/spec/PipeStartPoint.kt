package seaskyways.rxpipe.spec

import io.reactivex.Single

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
interface PipeStartPoint<T> : PipePoint {
    fun provide(): Single<T>
}
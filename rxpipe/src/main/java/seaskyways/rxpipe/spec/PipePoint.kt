package seaskyways.rxpipe.spec

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
interface PipePoint : Disposable {
    val namespace: String
    var volatility: Int
    val disposalObservable: Observable<Nothing>
}
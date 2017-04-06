package seaskyways.rxpipe

import seaskyways.rxpipe.spec.*
import java.lang.ref.WeakReference

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
typealias WeakList<T> = MutableList<WeakReference<T>>

object RxPipeManager : PipeManager {
    private val pipePointsRegistry: WeakList<PipePoint> = mutableListOf()
        @Synchronized get
    
    override fun <T> register(pipePoint: PipePoint) {
        registerForDisposal(pipePoint)
        pipePointsRegistry.add(WeakReference(pipePoint))
        sendToPipe<T>(pipePoint.namespace)
    }
    
    @Throws(PipeNotFoundException::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T> sendToPipe(namespace: String): Boolean {
        val pipesInNamespace = pipePointsRegistry.filter { it.get()?.namespace == namespace }
        val startPoints = pipesInNamespace.filter { it.get() is PipeStartPoint<*> }
        val endPoints = pipesInNamespace.filter { it.get() is PipeEndPoint<*> }
        
        if (startPoints.size > 1) {
            throw PipeStartPointOverflow(namespace)
        }
        if (startPoints.size == 1) {
            val startPoint = startPoints[0]
            
            if (endPoints.isNotEmpty()) {
                endPoints.forEach {
                    (it.get() as PipeEndPoint<T>).request((startPoint.get() as PipeStartPoint<T>).provide())
                }
            }
        }

//    private fun defer(startPointRef: WeakReference<PipePoint>) {
//        waitingList.add(startPointRef)
//    }
//
//    private fun <T> checkMatchesDeferred(endPoint: PipeEndPoint<*>): Boolean {
//        return waitingList
//                .filter { it.get()?.namespace == endPoint.namespace }
//                .firstOrNull()
//                ?.run { sendToPipe<T>(endPoint.namespace) } ?: false
        
        return true
    }
    
    private fun registerForDisposal(pipePoint: PipePoint) {
        pipePoint.disposalObservable
                .ignoreElements()
                .subscribe {
                    pipePointsRegistry
                            .map { it.get() }
                            .indexOfFirst { it == pipePoint }
                            .takeIf { it != -1 }
                            ?.let {
//                                print("Removed pipe at index : $it\n")
                                pipePointsRegistry.removeAt(it)
                            }
                }
    }
}
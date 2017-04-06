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
    
    private val waitingList: WeakList<PipePoint> = mutableListOf()
        @Synchronized get
    
    override fun <T> register(pipePoint: PipePoint) {
        pipePointsRegistry.add(WeakReference(pipePoint))
        registerForDisposal(pipePoint)
        if (pipePoint is PipeEndPoint<*>) {
            checkMatchesDeferred<T>(pipePoint)
        } else {
            sendToPipe<T>(pipePoint.namespace)
        }
    }
    
    @Throws(PipeNotFoundException::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T> sendToPipe(namespace: String): Boolean {
        val pipesInNamespace = pipePointsRegistry.filter { it.get()?.namespace == namespace }
        val startPoint = pipesInNamespace.filter { it.get() is PipeStartPoint<*> }.firstOrNull()
        val endPoints = pipesInNamespace.filter { it.get() is PipeEndPoint<*> }
        
        if (startPoint?.get() == null) {
            throw PipeNotFoundException("No providers for namespace '$namespace' found")
        }
        if (endPoints.isEmpty()) {
            defer(startPoint)
        } else {
            endPoints.forEach {
                (it.get() as PipeEndPoint<T>).request((startPoint.get() as PipeStartPoint<T>).provide())
            }
        }
        return pipesInNamespace.size == 2
    }
    
    private fun defer(startPointRef: WeakReference<PipePoint>) {
        waitingList.add(startPointRef)
    }
    
    private fun <T> checkMatchesDeferred(endPoint: PipeEndPoint<*>) {
        waitingList
                .filter { it.get()?.namespace == endPoint.namespace }
                .forEach { sendToPipe<T>(endPoint.namespace) }
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
                                print("Removed pipe at index : $it\n")
                                pipePointsRegistry.removeAt(it)
                            }
                }
    }
}
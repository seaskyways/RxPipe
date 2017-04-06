package seaskyways.rxpipe.spec

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
interface PipeManager {
    fun <T> register(pipePoint: PipePoint)
    
    fun <T> sendToPipe(namespace: String): Boolean
}
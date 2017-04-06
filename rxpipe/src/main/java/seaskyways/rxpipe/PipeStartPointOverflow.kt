package seaskyways.rxpipe

/**
 * Created by Ahmad on 06/04 Apr/2017.
 */
class PipeStartPointOverflow(namespace: String) :
        Exception("Too many pipes registered to namespace '$namespace'")
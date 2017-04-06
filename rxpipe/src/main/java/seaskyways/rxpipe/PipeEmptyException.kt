package seaskyways.rxpipe

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
class PipeEmptyException(
        message: String = "Pipe has been disposed and can't provide anymore, if you need it to serve longer increase volatility"
) : Exception(message)
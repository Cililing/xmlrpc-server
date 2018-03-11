import org.apache.xmlrpc.AsyncCallback
import java.util.Vector
import org.apache.xmlrpc.XmlRpcClient
import java.net.URL

class Client(private val server: XmlRpcClient) {

    fun add(x: Int, y: Int, callback: AsyncCallback? = null) : Any {
        return callAny("server.add", callback, x, y)
    }

    fun contactString(x: String, y: String, callback: AsyncCallback? = null): Any {
        return callAny("server.contactString", callback, x, y)
    }

    fun checkStatus(callback: AsyncCallback? = null) : Any {
        return callAny("server.checkStatus", callback)
    }

    fun help(callback: AsyncCallback? = null) : Any {
        return callAny("server.help", callback)
    }

    fun repeatString(x: Int, msg: String, callback: AsyncCallback? = null) : Any {
        return callAny("server.repeatString", callback, x, msg)
    }

    fun printIntervals(msg: String, times: Int, inv: Int, caller: String, callback: AsyncCallback?) : Any {
        return callAny("server.printIntervals", callback, msg, times, inv, caller)
    }

    fun callAny(command: String, callback: AsyncCallback?, vararg params: Any) : Any {
        val mappedParams = Vector<Any>()
        params.forEach(mappedParams::addElement)

        return execute(command, mappedParams, callback)
    }

    private fun <T> execute(command: String, params: Vector<T>, callback: AsyncCallback? = null) : Any = when (callback) {
        null -> server.execute(command, params)
        else -> server.executeAsync(command, params, callback)
    }
}


fun main(args: Array<String>) {
    try {
        val server = XmlRpcClient("http://localhost:80")
        val clientHandler = Client(server)

        println(clientHandler.checkStatus())

        // Example of help. Returns all methods with annotation ServerMethod.
        println(clientHandler.help())

        // Example of adding two same-type objects
        println(clientHandler.add(12, 11))
        println(clientHandler.contactString("x..", "..y"))

        // And two different types.
        println(clientHandler.repeatString(3, "XYX_"))

        // Now we check async call. Should print sth on console after long, long time.
        println(clientHandler.checkStatus(object : AsyncCallback {
            override fun handleResult(p0: Any?, p1: URL?, p2: String?) {
                print(p0)
            }

            override fun handleError(p0: java.lang.Exception?, p1: URL?, p2: String?) {
                print(p0)
            }
        }))

        // Example of calling any method in runtime
        println(clientHandler.callAny("server.repeatString", null, 3, "Msg..."))

        // And now two calls at one time. --> Sync!
        print(clientHandler.printIntervals("Loading...", 5, 1000, "caller_one", null))
        print(clientHandler.printIntervals("Deloading...", 5, 1000, "caller_two", null))


        // And now two calls at one time. --> Asynch!
        print(clientHandler.printIntervals("Loading...", 5, 1000, "caller_one", object : AsyncCallback {
            override fun handleResult(p0: Any?, p1: URL?, p2: String?) {
            }

            override fun handleError(p0: java.lang.Exception?, p1: URL?, p2: String?) {
            }

        }))
        print(clientHandler.printIntervals("Deloading...", 5, 1000, "caller_two", object : AsyncCallback {
            override fun handleResult(p0: Any?, p1: URL?, p2: String?) {
            }

            override fun handleError(p0: java.lang.Exception?, p1: URL?, p2: String?) {
            }
        }))

    } catch (exception: Exception) {
        System.err.println("JavaClient: " + exception)
    }

}
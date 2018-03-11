import org.apache.xmlrpc.WebServer
import kotlin.reflect.full.memberFunctions

class Server(private val port: Int) {

    @ServerMethod("Add two Ints.")
    fun add(x: Int, y: Int): Int {
        return x + y
    }

    @ServerMethod
    fun contactString(x: String, y: String): String {
        return x + y
    }

    @ServerMethod
    fun checkStatus() : String {
        try {
            // thread to sleep for 1000 milliseconds
            Thread.sleep(1000)
        } catch (e: Exception) {
            println(e)
        }
        return "Server is running on port: $port"
    }

    @ServerMethod
    fun repeatString(x: Int, msg: String) : String {
        return msg.repeat(x)
    }

    @ServerMethod
    fun kill() {
        println("Shutting server down...")
        System.exit(0)
    }

    @ServerMethod
    fun help() : String {
        return this.javaClass.kotlin.memberFunctions
                .filter { it.annotations.any { annotation -> annotation is ServerMethod } }
                .map { method ->
                    val annotationDescription = ( method.annotations.find { annotation -> annotation is ServerMethod }!! as ServerMethod).description

                    method.toString()
                            .plus("\t")
                            .plus(annotationDescription)
                            .plus("\n")
                }.fold("", { acc, x -> acc + x})
    }

    @ServerMethod
    fun printIntervals(msg: String, times: Int, inv: Int = 10000, caller: String? = null) : String {
        var counter = times
        try {
            while (counter-- > 0) {
                println("$caller: $msg")
                Thread.sleep(inv.toLong())
            }
        } catch (e: Exception) {
            println(e)
        }

        return "$caller finished his request."
    }

}

fun main(args: Array<String>) {
    try {
        println("Attempting to start XML-RPC Server...")

        val server = WebServer(80)
        server.addHandler("server", Server(80))
        server.start()

        println("Started successfully.")
        println("Accepting requests. Server runs on port 80.")

    } catch (exception: Exception) {
        System.err.println("JavaServer: " + exception)
    }
}

package com.example.androidrpctest

//import androidx.compose.ui.tooling.data.EmptyGroup.data
//import io.ktor.http.ContentType.Application.Json
//import io.ktor.serialization.kotlinx.json.json
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidrpctest.ui.theme.AndroidRpcTestTheme
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AndroidRpcTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        thread {

            embeddedServer(Netty, port = 8080) {
//                install(ContentNegotiation){ json()}

                routing {
                    get("/") {
                        Log.e("", "68")
                        call.respond(10)
                    }

                    get("/screenshot") {
                        val img =
                            Bitmap.createBitmap(
                                1080,
                                1920,
                                Bitmap.Config.ARGB_8888
                            )
                        val canvas = Canvas(img)
                        val buf = ByteBuffer.allocate(img.byteCount)

                        // make some random color for canvas draw
                        val R = (Math.random() * 256).toInt()
                        val G = (Math.random() * 256).toInt()
                        val B = (Math.random() * 256).toInt()
                        canvas.drawARGB(255, R, G, B)
                        // move buf position
                        buf.position(0)
                        // copy pixels to buffer
                        img.copyPixelsToBuffer(buf)
                        img.recycle()
//                        Log.e("", "change buf value in kotlin $R, $G, $B")

                        @Serializable
                        data class Msg(
                            val width: Int,
                            val height: Int,
                            val data: UByteArray
                        )
                        Log.e("", "screenshot convert to UByteArray")

                        val d1 = Msg(
                            data = buf.array().asUByteArray(),
                            width = 1920,
                            height = 1080
                        )
                        val stream = ByteArrayOutputStream()
                        Log.e("", "screenshot start encode")

                        Json.encodeToStream(d1, stream)
                        val d = stream.toByteArray()
                        Log.e("", "screenshot respondBytes")
//                        Log.e("","content: $d1")
                        call.respondBytes(d)
                    }
                    post("/click") {

                        val point = call.receive<Point>()
                        Log.e("", "click point $point")
                    }
                }
            }.start(wait = true)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidRpcTestTheme {
        Greeting("Android")
    }
}
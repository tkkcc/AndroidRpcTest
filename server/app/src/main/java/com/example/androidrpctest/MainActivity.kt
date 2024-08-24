package com.example.androidrpctest

//import androidx.compose.ui.tooling.data.EmptyGroup.data
//import io.ktor.http.ContentType.Application.Json
//import io.ktor.serialization.kotlinx.json.json
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDecay
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidrpctest.ui.theme.AndroidRpcTestTheme
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.Identity.encode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import kotlin.io.encoding.ExperimentalEncodingApi


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalEncodingApi::class)
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
//                install(ContentNegotiation){ gson()}

                routing {
                    get("/") {
                        Log.e("", "68")
                        call.respond(10)
                    }

                    get("/screenshot") {
//                        val img =
//                            Bitmap.createBitmap(
//                                1080,
//                                1920,
//                                Bitmap.Config.ARGB_8888
//                            )
                        // use bitmap in res/drawable with 1080p sample  size
                        val x=R.drawable.bc6bfeed79fd1e3765d5afb8bf3b54ee
                        Log.e("","85")
                        var img = BitmapFactory.decodeResource(resources, x, BitmapFactory.Options().apply {
                            inSampleSize = 4
                        })
                        Log.e("","86")

                        // convert bitmap to RGBA_888
                        img = img.copy(Bitmap.Config.ARGB_8888, false)
                        Log.e("","87")


//                        val canvas = Canvas(img)
                        val buf = ByteBuffer.allocateDirect(img.byteCount)
                        Log.e("","88")
//                        // make some random color for canvas draw
//                        val R = (Math.random() * 256).toInt()
//                        val G = (Math.random() * 256).toInt()
//                        val B = (Math.random() * 256).toInt()
//                        canvas.drawARGB(255, R, G, B)
//                        // move buf position
//                        buf.position(0)
                        // copy pixels to buffer
                        img.copyPixelsToBuffer(buf)
//                        Log.e("", "change buf value in kotlin $R, $G, $B")
                        Log.e("","89")

                        @Serializable
                        data class Msg(
                            val width: Int,
                            val height: Int,
                            val data: String
                        )
                        Log.e("", "screenshot convert start: png + byte")
                        // base64 encode buf
//                        var stream = ByteArrayOutputStream(img.byteCount)
//                        img.compress(Bitmap.CompressFormat.PNG, 100, stream)

                        Log.e("", "screenshot png encode finish")

//                        val s = java.util.Base64.getEncoder().encodeToString(stream.toByteArray())
                        val s = kotlin.io.encoding.Base64.encode(buf.array())
//                        val s = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT)


                        val d1 = Msg(
//                            data = stream.toByteArray().asUByteArray(),
//                            data = buf.array().asUByteArray(),
                            data = s,
                            width = 1920,
                            height = 1080
                        )
                        Log.e("", "screenshot start encode")
                        val stream = ByteArrayOutputStream()
//
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
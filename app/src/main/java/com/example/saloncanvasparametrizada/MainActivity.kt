package com.example.saloncanvasparametrizada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.saloncanvasparametrizada.ui.theme.SalonCanvasParametrizadaTheme
import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalonCanvasParametrizadaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GalleryCanvasScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryCanvasScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rooms = remember { mutableListOf<Room>() }
    val pictures = remember { mutableListOf<Picture>() }

    try {
        readDataFromCsv(context, rooms, pictures)
    } catch (e: IOException) {
        // Handle the exception
        e.printStackTrace()
    }

    GalleryCanvas(
        context = context,
        attrs = null,
        rooms = rooms,
        pictures = pictures
    )
}

private fun readDataFromCsv(
    context: Context,
    rooms: MutableList<Room>,
    pictures: MutableList<Picture>
) {
    val inputStream = context.assets.open("data.cvs")
    val reader = BufferedReader(InputStreamReader(inputStream))

    var line: String?
    var currentRoom: Room? = null
    var currentPicture: Picture? = null

    while (reader.readLine().also { line = it } != null) {
        if (line!!.startsWith("ROOM")) {
            currentRoom = Room(line!!.split(" ")[1])
            rooms.add(currentRoom)
        } else if (line!!.startsWith("PICTURE")) {
            currentPicture = Picture(line!!.split(" ")[1])
            pictures.add(currentPicture)
        } else {
            val points = line!!.split(",").map {
                val (x, y) = it.trim().split(".")
                Point(x.toFloat(), y.toFloat())
            }
            if (currentRoom != null) {
                currentRoom.points.addAll(points)
            } else if (currentPicture != null) {
                currentPicture.points.addAll(points)
            }
        }
    }

    reader.close()
}

@Preview(showBackground = true)
@Composable
fun GalleryCanvasScreenPreview() {
    SalonCanvasParametrizadaTheme {
        GalleryCanvasScreen()
    }
}
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            SalonCanvasParametrizadaTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SalonCanvasParametrizadaTheme {
//        Greeting("Android")
//    }
//}



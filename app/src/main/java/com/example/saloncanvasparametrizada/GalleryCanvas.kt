package com.example.saloncanvasparametrizada

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlin.random.Random

class GalleryCanvas(
    context: Context,
    attrs: AttributeSet?,
    private val rooms: List<Room>,
    private val pictures: List<Picture>
) : View(context, attrs) {
    private val canvasWidth = 1000f
    private val canvasHeight = 1000f

    private val roomPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val picturePaint = Paint().apply {
        style = Paint.Style.FILL
        textSize = 24f
        color = Color.BLACK
    }
    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        drawGrid(canvas)
        drawRooms(canvas)
        drawPictures(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x
                val touchY = event.y
                handleRoomClick(touchX, touchY)
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    private fun handleRoomClick(touchX: Float, touchY: Float) {
        rooms.forEach { room ->
            if (isPointInPolygon(room.points, touchX, touchY)) {
                showRoomPopup(room)
                return
            }
        }
    }

    private fun showRoomPopup(room: Room) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(room.name)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun isPointInPolygon(points: List<Point>, x: Float, y: Float): Boolean {
        var inside = false
        var i: Int
        var j: Int = points.size - 1
        for (i in 0 until points.size) {
            if ((points[i].y > y) != (points[j].y > y) &&
                (x < (points[j].x - points[i].x) * (y - points[i].y) / (points[j].y - points[i].y) + points[i].x)
            ) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    private fun drawGrid(canvas: Canvas) {
        val gridSize = 100f
        for (x in 0..canvasWidth.toInt() step gridSize.toInt()) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), canvasHeight, gridPaint)
        }
        for (y in 0..canvasHeight.toInt() step gridSize.toInt()) {
            canvas.drawLine(0f, y.toFloat(), canvasWidth, y.toFloat(), gridPaint)
        }
    }

    private fun drawRooms(canvas: Canvas) {
        rooms.forEach { room ->
            val path = Path()
            path.moveTo(room.points[0].x * canvasWidth / 1000f, room.points[0].y * canvasHeight / 1000f)
            for (i in 1 until room.points.size) {
                path.lineTo(room.points[i].x * canvasWidth / 1000f, room.points[i].y * canvasHeight / 1000f)
            }
            path.close()

            // Generar un color aleatorio para cada sala
            roomPaint.color = Color.rgb(
                Random.nextInt(256),
                Random.nextInt(256),
                Random.nextInt(256)
            )
            canvas.drawPath(path, roomPaint)

            // Dibujar el nombre de la habitación
            picturePaint.color = Color.WHITE
            val centerX = room.points.map { it.x * canvasWidth / 1000f }.average().toFloat()
            val centerY = room.points.map { it.y * canvasHeight / 1000f }.average().toFloat()
            canvas.drawText(room.name, centerX - (room.name.length * 12f / 2), centerY, picturePaint)
        }
    }

    private fun drawPictures(canvas: Canvas) {
        pictures.forEach { picture ->
            picturePaint.color = Color.YELLOW
            picture.points.forEach { point ->
                canvas.drawCircle(point.x * canvasWidth / 1000f, point.y * canvasHeight / 1000f, 20f, picturePaint)
            }
            picturePaint.color = Color.BLACK
            picture.points.forEach { point ->
                canvas.drawText(picture.title, point.x * canvasWidth / 1000f - 50f, point.y * canvasHeight / 1000f + 30f, picturePaint)
            }
        }
    }
}

package com.example.cypher_vault.view.resources

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class FaceOverlayView(context: Context) : View(context) {
    var boundingBox: Rect? = null
    var imageWidth: Int = 1
    var imageHeight: Int = 1

    var targetBox: Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        // Crea el targetBox aquí, donde width y height tienen los valores correctos
        targetBox = Rect(width / 8, height / 4, width * 7 / 8, height * 3 / 4)

        val outerPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), targetBox!!.top.toFloat(), outerPaint) // Parte superior
        canvas.drawRect(0f, targetBox!!.top.toFloat(), targetBox!!.left.toFloat(), targetBox!!.bottom.toFloat(), outerPaint) // Parte izquierda
        canvas.drawRect(targetBox!!.right.toFloat(), targetBox!!.top.toFloat(), width.toFloat(), targetBox!!.bottom.toFloat(), outerPaint) // Parte derecha
        canvas.drawRect(0f, targetBox!!.bottom.toFloat(), width.toFloat(), height.toFloat(), outerPaint) // Parte inferior

        // Dibuja el targetBox en verde
        val targetPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(targetBox!!, targetPaint)



        boundingBox?.let {
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f

            val left = width - it.right * width.toFloat()  / imageHeight
            val top = it.top * height.toFloat() / imageWidth
            val right = width - it.left * width.toFloat()  / imageHeight
            val bottom = it.bottom * height.toFloat() / imageWidth

            canvas.drawRect(left, top, right, bottom, paint)
        }
    }

    fun isBoundingBoxInsideTarget(): Boolean {
        val transformedBoundingBox = boundingBox?.let { transformBoundingBox(it) }
        return transformedBoundingBox?.let { targetBox?.contains(it) } ?: false
    }

    private fun transformBoundingBox(boundingBox: Rect): Rect {
        val left = width - boundingBox.right * width.toFloat() / imageHeight
        val top = boundingBox.top * height.toFloat() / imageWidth
        val right = width - boundingBox.left * width.toFloat() / imageHeight
        val bottom = boundingBox.bottom * height.toFloat() / imageWidth

        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }

}


package com.example.cypher_vault.view.resources

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.example.cypher_vault.R

class FaceOverlayView(context: Context) : View(context) {
    var boundingBox: Rect? = null
    var imageWidth: Int = 1
    var imageHeight: Int = 1

    private var minTargetBox: Rect? = null
    private var maxTargetBox: Rect? = null
    var message: String? = null

    private var inside: Boolean = false

    private val silhouette: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.siluetabien)
    private val silhouetteError: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.siluetaerror)

    private var currentOrientation: String = ""
    private var timerValue: Int = 0
    private var eyesOpens: Int = 0

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(message==null) {
            when (currentOrientation) {
                "smile" -> {
                    drawTextCentered(
                        canvas,
                        "Por favor sonría",
                        width / 2f,
                        height * 0.9f
                    )
                }

                "blink1" -> {
                    drawTextCentered(
                        canvas,
                        "Por favor, pestañee. Restantes $eyesOpens",
                        width / 2f,
                        height * 0.9f
                    )
                }

                "blink2" -> {
                    drawTextCentered(
                        canvas,
                        "Por favor, pestañee. Restantes $eyesOpens",
                        width / 2f,
                        height * 0.9f
                    )
                }

                "blink3" -> {
                    drawTextCentered(
                        canvas,
                        "Por favor, pestañee. Restantes $eyesOpens",
                        width / 2f,
                        height * 0.9f
                    )
                }

                "front" -> {
                    if (timerValue > 0) {
                        drawTextCentered(
                            canvas,
                            "$timerValue",
                            width / 2f,
                            height * 0.85f
                        )
                        drawTextCentered(
                            canvas,
                            "Mire hacia la cámara.",
                            width / 2f,
                            height * 0.9f
                        )
                    }
                }
            }
        }


    // Define el tamaño mínimo y máximo del targetBox
        val minBoxWidth = width * 5 / 8 + 100
        val minBoxHeight = height * 3 / 8 + 250
        val maxBoxWidth = width * 3 / 4 + 100
        val maxBoxHeight = height * 1 / 2 + 250

        // Calcula las posiciones del targetBox mínimo y máximo
        minTargetBox = Rect(
            (width / 2 - minBoxWidth / 2),
            (height / 2 - minBoxHeight / 2 - 100),  // Mueve el minTargetBox un poco hacia arriba
            (width / 2 + minBoxWidth / 2),
            (height / 2 + minBoxHeight / 2 - 100)   // Ajusta la posición inferior acorde a la nueva altura y movimiento
        )

        maxTargetBox = Rect(
            (width - maxBoxWidth) / 2,
            (height / 2 - maxBoxHeight / 2 - 100),  // Mueve el maxTargetBox un poco hacia arriba
            (width + maxBoxWidth) / 2,
            (height / 2 + maxBoxHeight / 2 - 100)   // Ajusta la posición inferior acorde a la nueva altura y movimiento
        )

        // Dibuja el targetBox mínimo en azul
        val minTargetPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
       // canvas.drawRect(minTargetBox!!, minTargetPaint)

        // Dibuja el targetBox máximo en verde
        val maxTargetPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        //canvas.drawRect(maxTargetBox!!, maxTargetPaint)

        // Dibuja el boundingBox en rojo
        boundingBox?.let {
            val paint = Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }

            val left = width - it.right * width.toFloat() / imageHeight
            val top = it.top * height.toFloat() / imageWidth
            val right = width - it.left * width.toFloat() / imageHeight
            val bottom = it.bottom * height.toFloat() / imageWidth

            canvas.drawRect(left, top, right, bottom, paint)
        }

        // Dibuja la silueta correcta dependiendo del estado
        val silhouetteBitmap = if (inside) silhouette else silhouetteError
        val aspectRatio = silhouetteBitmap.width.toFloat() / silhouetteBitmap.height.toFloat()
        val silhouetteHeight = height
        val silhouetteWidth = (silhouetteHeight * aspectRatio).toInt()
        val left = (width - silhouetteWidth) / 2
        val top = 0
        val right = left + silhouetteWidth

        canvas.drawBitmap(silhouetteBitmap, null, Rect(left, top, right, bottom), null)

        message?.let {
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 64f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(it, (width / 2).toFloat(), (height * 0.9).toFloat(), textPaint)
        }
    }


    private fun drawTextCentered(canvas: Canvas, text: String, x: Float, y: Float) {
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 64f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(text, x, y, textPaint)
    }

// Methods to update state
    fun updateState(orientation: String, timer: Int = 0, eyesOpen: Int = 0) {
        currentOrientation = orientation
        timerValue = timer
        eyesOpens = eyesOpen
        invalidate()
    }

    fun isBoundingBoxInsideTarget(): Boolean {
        val transformedBoundingBox = boundingBox?.let { transformBoundingBox(it) }
        return transformedBoundingBox?.let {
            maxTargetBox?.contains(it) == true && minTargetBox?.contains(it) == false
        } ?: false
    }

    private fun transformBoundingBox(boundingBox: Rect): Rect {
        val left = width - boundingBox.right * width.toFloat() / imageHeight
        val top = boundingBox.top * height.toFloat() / imageWidth
        val right = width - boundingBox.left * width.toFloat() / imageHeight
        val bottom = boundingBox.bottom * height.toFloat() / imageWidth

        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }

    fun updateMessage() {
        val transformedBoundingBox = boundingBox?.let { transformBoundingBox(it) }
        message = when {
            transformedBoundingBox == null -> null
            minTargetBox?.contains(transformedBoundingBox) == true -> "Acérquese un poco"
            maxTargetBox?.contains(transformedBoundingBox) == false -> "Aléjese un poco"
            else -> null
        }
        invalidate()
    }

    fun updateInside() {
        val transformedBoundingBox = boundingBox?.let { transformBoundingBox(it) }
        inside = when {
            transformedBoundingBox == null -> false
            minTargetBox?.contains(transformedBoundingBox) == true -> false
            maxTargetBox?.contains(transformedBoundingBox) == false -> false
            else -> true
        }
        invalidate()
    }




}

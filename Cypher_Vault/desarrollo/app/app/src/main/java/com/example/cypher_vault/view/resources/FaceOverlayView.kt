package com.example.cypher_vault.view.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.core.content.res.ResourcesCompat
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

    val textPaint = Paint().apply {
        color = Color.parseColor("#02a6c3")
        textSize = 18f.spToPx()
        typeface = ResourcesCompat.getFont(context, R.font.expandedconsolabold)
        textAlign = Paint.Align.CENTER
    }

    val backgroundPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f.dpToPx()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (message == null) {
            when (currentOrientation) {
                "smile" -> {
                    drawTextWithBackground(
                        canvas,
                        "Por favor sonría",
                        width / 2f,
                        height * 0.1f,
                        textPaint,
                        backgroundPaint,
                    )
                }
                "blink1", "blink2", "blink3" -> {
                    drawTextWithBackground(
                        canvas,
                        "Por favor, pestañee. Restantes $eyesOpens",
                        width / 2f,
                        height * 0.1f,
                        textPaint,
                        backgroundPaint
                    )
                }
                "front" -> {
                    if (timerValue > 0) {
                        drawTextWithBackground(
                            canvas,
                            "$timerValue",
                            width / 2f,
                            height * 0.05f,
                            textPaint,
                            backgroundPaint,
                        )
                        drawTextWithBackground(
                            canvas,
                            "Mire hacia la cámara.",
                            width / 2f,
                            height * 0.1f,
                            textPaint,
                            backgroundPaint,
                        )
                    }
                }
            }
        } else {
            // Aplica la fuente personalizada al mensaje generado por updateMessage
            drawTextWithBackground(
                canvas,
                message!!,
                width / 2f,
                height * 0.1f,
                textPaint,
                backgroundPaint
            )
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

        // Dibuja la silueta correcta dependiendo del estado
        val silhouetteBitmap = if (inside) silhouette else silhouetteError
        val aspectRatio = silhouetteBitmap.width.toFloat() / silhouetteBitmap.height.toFloat()
        val silhouetteHeight = height
        val silhouetteWidth = (silhouetteHeight * aspectRatio).toInt()
        val left = (width - silhouetteWidth) / 2
        val top = 0
        val right = left + silhouetteWidth

        canvas.drawBitmap(silhouetteBitmap, null, Rect(left, top, right, bottom), null)

    }


    private fun drawTextCentered(canvas: Canvas, text: String, x: Float, y: Float) {
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 50f
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

    private fun drawTextWithBackground(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        textPaint: Paint,
        backgroundPaint: Paint,
        cornerRadius: Float = 24f
    ) {
        val textWidth = textPaint.measureText(text)
        val textHeight = textPaint.descent() - textPaint.ascent()

        val paddingX = canvas.width * 0.05f
        val paddingY = textHeight * 0.2f

        val rectLeft = x - textWidth / 2 - paddingX
        val rectTop = y + textPaint.ascent() - paddingY
        val rectRight = x + textWidth / 2 + paddingX
        val rectBottom = y + textPaint.descent() + paddingY

        val rectF = RectF(rectLeft, rectTop, rectRight, rectBottom)

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
        canvas.drawText(text, x, y, textPaint)
    }


    private fun Float.spToPx(): Float = this * Resources.getSystem().displayMetrics.scaledDensity
    private fun Float.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density


}

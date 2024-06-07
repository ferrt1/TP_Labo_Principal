package com.example.cypher_vault.model.mtcnn

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.PI

object Align {

    @JvmStatic
    fun face_align(bitmap: Bitmap, landmarks: Array<Point>): Bitmap {
        val diffEyeX = (landmarks[1].x - landmarks[0].x).toFloat()
        val diffEyeY = (landmarks[1].y - landmarks[0].y).toFloat()

        val fAngle = if (abs(diffEyeY) < 1e-7) {
            0f
        } else {
            (atan((diffEyeY / diffEyeX).toDouble()) * 180.0f / PI).toFloat()
        }

        val matrix = Matrix()
        matrix.setRotate(-fAngle)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

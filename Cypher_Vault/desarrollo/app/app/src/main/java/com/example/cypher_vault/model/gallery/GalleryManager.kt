package com.example.cypher_vault.model.gallery
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryManager {

    fun capitalizarPrimeraLetra(palabra: String): String {
        if (palabra.isEmpty()) {
            return palabra
        }
        return palabra.substring(0, 1).uppercase(Locale.getDefault())
    }

    fun procesarString(texto: String): String {
        if (texto.length <= 1) {
            return texto
        }

        val minusculas = texto.substring(1).lowercase(Locale.getDefault())
        return if (minusculas.length <= 16) {
            minusculas
        } else {
            minusculas.substring(0, 14) + ".."
        }
    }

    fun formatIncomeDate(income: Long?): String {
        return if (income != null) {
            val date = Date(income)
            val formatter = SimpleDateFormat("HH:mm - dd MMM yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Fecha no disponible"
        }
    }

    fun reduceImageSize(imageBitmap: ImageBitmap, maxMegapixels: Float): ImageBitmap {
        val bitmap = imageBitmap
        val reducedBitmap = reduceImageSize(bitmap, maxMegapixels)

        // Create a new ImageBitmap from the reduced Bitmap
        val config : ImageBitmapConfig = bitmap.config
        val newImageBitmap = ImageBitmap(reducedBitmap.width, reducedBitmap.height, config)

        // Copy the pixels from the reduced Bitmap to the new ImageBitmap
        val canvas = Canvas(newImageBitmap)
        canvas.drawImage(reducedBitmap, Offset.Zero, Paint())

        return newImageBitmap
    }

    private fun reduceImageSize(bitmap: Bitmap, maxMegapixels: Float): Bitmap {
        val megapixels = (bitmap.width * bitmap.height) / 1000000f

        if (megapixels <= maxMegapixels) {
            return bitmap
        }

        val scale = Math.sqrt((maxMegapixels / megapixels).toDouble())

        val matrix = Matrix()
        matrix.postScale(scale.toFloat(), scale.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


}
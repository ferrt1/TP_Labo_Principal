package com.example.cypher_vault.model.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import com.example.cypher_vault.model.mtcnn.Box
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min

object MyUtil {
    fun readFromAssets(context: Context, filename: String): Bitmap? {
        val asm = context.assets
        return try {
            val inputStream: InputStream = asm.open(filename)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun rectExtend(bitmap: Bitmap, rect: Rect, marginX: Int, marginY: Int) {
        rect.left = max(0, rect.left - marginX / 2)
        rect.right = min(bitmap.width - 1, rect.right + marginX / 2)
        rect.top = max(0, rect.top - marginY / 2)
        rect.bottom = min(bitmap.height - 1, rect.bottom + marginY / 2)
    }

    fun rectExtend(bitmap: Bitmap, rect: Rect) {
        val width = rect.right - rect.left
        val height = rect.bottom - rect.top
        val margin = (height - width) / 2
        rect.left = max(0, rect.left - margin)
        rect.right = min(bitmap.width - 1, rect.right + margin)
    }

    @Throws(IOException::class)
    fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun normalizeImage(bitmap: Bitmap): Array<Array<FloatArray>> {
        val h = bitmap.height
        val w = bitmap.width
        val floatValues = Array(h) { Array(w) { FloatArray(3) } }

        val imageMean = 127.5f
        val imageStd = 128f

        val pixels = IntArray(h * w)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                val value = pixels[i * w + j]
                val r = ((value shr 16 and 0xFF) - imageMean) / imageStd
                val g = ((value shr 8 and 0xFF) - imageMean) / imageStd
                val b = ((value and 0xFF) - imageMean) / imageStd
                floatValues[i][j] = floatArrayOf(r, g, b)
            }
        }
        return floatValues
    }

    fun bitmapResize(bitmap: Bitmap, scale: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    fun transposeImage(`in`: Array<Array<FloatArray>>): Array<Array<FloatArray>> {
        val h = `in`.size
        val w = `in`[0].size
        val channel = `in`[0][0].size
        val out = Array(w) { Array(h) { FloatArray(channel) } }
        for (i in 0 until h) {
            for (j in 0 until w) {
                out[j][i] = `in`[i][j]
            }
        }
        return out
    }

    fun transposeBatch(`in`: Array<Array<Array<FloatArray>>>): Array<Array<Array<FloatArray>>> {
        val batch = `in`.size
        val h = `in`[0].size
        val w = `in`[0][0].size
        val channel = `in`[0][0][0].size
        val out = Array(batch) { Array(w) { Array(h) { FloatArray(channel) } } }
        for (i in 0 until batch) {
            for (j in 0 until h) {
                for (k in 0 until w) {
                    out[i][k][j] = `in`[i][j][k]
                }
            }
        }
        return out
    }

    fun cropAndResize(bitmap: Bitmap, box: Box, size: Int): Array<Array<FloatArray>> {
        val matrix = Matrix()
        val scaleW = 1.0f * size / box.width()
        val scaleH = 1.0f * size / box.height()
        matrix.postScale(scaleW, scaleH)
        val rect = box.transform2Rect()
        val croped = Bitmap.createBitmap(bitmap, rect.left, rect.top, box.width(), box.height(), matrix, true)
        return normalizeImage(croped)
    }

    fun crop(bitmap: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top)
    }

    fun l2Normalize(embeddings: Array<FloatArray>, epsilon: Double) {
        for (i in embeddings.indices) {
            var squareSum = 0f
            for (j in embeddings[i].indices) {
                squareSum += Math.pow(embeddings[i][j].toDouble(), 2.0).toFloat()
            }
            val xInvNorm = Math.sqrt(Math.max(squareSum.toDouble(), epsilon)).toFloat()
            for (j in embeddings[i].indices) {
                embeddings[i][j] = embeddings[i][j] / xInvNorm
            }
        }
    }

    fun convertGreyImg(bitmap: Bitmap): Array<IntArray> {
        val w = bitmap.width
        val h = bitmap.height

        val pixels = IntArray(h * w)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val result = Array(h) { IntArray(w) }
        val alpha = 0xFF shl 24
        for (i in 0 until h) {
            for (j in 0 until w) {
                val value = pixels[w * i + j]

                val red = (value shr 16 and 0xFF)
                val green = (value shr 8 and 0xFF)
                val blue = (value and 0xFF)

                var grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                result[i][j] = grey
            }
        }
        return result
    }

}

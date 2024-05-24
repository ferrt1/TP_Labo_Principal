package com.example.cypher_vault.model.authentication

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt

class FaceRecognitionModel(assetManager: AssetManager, modelPath: String) {
    private val interpreter: Interpreter = Interpreter(loadModelFile(assetManager, modelPath))

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun extractFeatures(faceImage: Bitmap): FloatArray {
        val faceFeatures = Array(1) { FloatArray(192) }
        val faceImageBuffer = convertBitmapToBuffer(faceImage)
        interpreter.run(faceImageBuffer, faceFeatures)
        return faceFeatures[0]
    }

    private fun convertBitmapToBuffer(bitmap: Bitmap): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height
        val channels = 3
        val pixelSize = 4
        val bufferSize = width * height * channels * pixelSize
        val imgData = ByteBuffer.allocateDirect(bufferSize)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(width * height)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until width) {
            for (j in 0 until height) {
                val pixelValue = intValues[j * width + i]
                imgData.putFloat(((pixelValue shr 16 and 0xFF) - 127.5f) / 127.5f)
                imgData.putFloat(((pixelValue shr 8 and 0xFF) - 127.5f) / 127.5f)
                imgData.putFloat(((pixelValue and 0xFF) - 127.5f) / 127.5f)
            }
        }
        return imgData
    }

    // Calcular la distancia euclidiana entre dos vectores de características
    fun compareFaceFeatures(features1: FloatArray, features2: FloatArray): Float {
        Log.d("Imagenes", "entro aca compareFace")
        var sum = 0.0f
        for (i in features1.indices) {
            sum += (features1[i] - features2[i]).pow(2)
        }

        val result = sqrt(sum)
        Log.d("Imagenes", "el resultado es: $result" )

        return result
    }

}

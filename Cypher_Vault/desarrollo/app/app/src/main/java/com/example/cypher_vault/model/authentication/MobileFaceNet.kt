package com.example.cypher_vault.model.authentication
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt

class MobileFaceNet(assetManager: AssetManager, modelPath: String = "MobileFaceNetv2.tflite") {
    companion object {
        const val INPUT_IMAGE_SIZE = 112
        const val THRESHOLD = 0.35f
    }

    private val interpreter: Interpreter
    private var lastDistance: Float = 0.0f

    init {
        val options = Interpreter.Options()
        options.setNumThreads(4)
        interpreter = Interpreter(loadModelFile(assetManager, modelPath), options)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun compare(bitmap1: Bitmap, bitmap2: Bitmap): Float {
        val bitmapScale1 = Bitmap.createScaledBitmap(bitmap1, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, true)
        val bitmapScale2 = Bitmap.createScaledBitmap(bitmap2, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, true)

        val datasets = getTwoImageDatasets(bitmapScale1, bitmapScale2)
        val embeddings = Array(2) { FloatArray(192) }
        interpreter.run(datasets, embeddings)

        l2Normalize(embeddings, 1e-10)
        return evaluate(embeddings)
    }

    private fun getTwoImageDatasets(bitmap1: Bitmap, bitmap2: Bitmap): Array<Array<Array<FloatArray>>> {
        val bitmaps = arrayOf(bitmap1, bitmap2)
        val datasets = Array(bitmaps.size) { Array(INPUT_IMAGE_SIZE) { Array(INPUT_IMAGE_SIZE) { FloatArray(3) } } }

        for (i in bitmaps.indices) {
            datasets[i] = normalizeImage(bitmaps[i])
        }
        return datasets
    }

    private fun normalizeImage(bitmap: Bitmap): Array<Array<FloatArray>> {
        val dataset = Array(INPUT_IMAGE_SIZE) { Array(INPUT_IMAGE_SIZE) { FloatArray(3) } }
        val intValues = IntArray(INPUT_IMAGE_SIZE * INPUT_IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in 0 until INPUT_IMAGE_SIZE) {
            for (j in 0 until INPUT_IMAGE_SIZE) {
                val pixel = intValues[i * INPUT_IMAGE_SIZE + j]
                dataset[i][j][0] = ((pixel shr 16 and 0xFF) - 128f) / 128f
                dataset[i][j][1] = ((pixel shr 8 and 0xFF) - 128f) / 128f
                dataset[i][j][2] = ((pixel and 0xFF) - 128f) / 128f
            }
        }
        return dataset
    }

    private fun l2Normalize(embeddings: Array<FloatArray>, epsilon: Double) {
        for (embedding in embeddings) {
            var squareSum = 0.0
            for (value in embedding) {
                squareSum += value * value
            }
            val norm = sqrt(squareSum + epsilon)
            for (i in embedding.indices) {
                embedding[i] = (embedding[i] / norm).toFloat()
            }
        }
    }

    private fun evaluate(embeddings: Array<FloatArray>): Float {
        val dist = embeddings[0].zip(embeddings[1]) { a, b -> (a - b).pow(2) }.sum()
        Log.d("Image", "$dist")
        lastDistance = dist
        return if (dist < THRESHOLD) 1.0f else 0.0f
    }

    fun getLastDistance(): Float {
        return lastDistance
    }

}

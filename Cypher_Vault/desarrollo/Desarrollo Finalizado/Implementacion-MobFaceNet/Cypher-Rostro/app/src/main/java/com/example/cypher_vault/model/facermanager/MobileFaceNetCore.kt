package com.example.cypher_vault.model.facermanager

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cypher_vault.ml.MobileFaceNet
import com.example.cypher_vault.model.facermanager.MobileFaceNetManager.THRESHOLD
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import kotlin.math.pow
import kotlin.math.sqrt


class MobileFaceNetCore {

    @RequiresApi(Build.VERSION_CODES.O)
    fun inferencia(imagen1: Bitmap, imagen2: Bitmap, context: Context): Boolean {
        try {
            // Cargar el modelo MobileFaceNet

            val model = MobileFaceNet.newInstance(context)
            Log.d("faceDetection", "Inicio el modelo")


            // Preprocesar las imágenes y obtener las características faciales
            val image1: TensorImage =
                preprocessImage(imagen1) // Suponiendo que tienes las imágenes como Bitmaps
            val image2: TensorImage = preprocessImage( imagen2)
            Log.d("faceDetection", "Proceso imagenes a tensorImages")
            // Pasar las imágenes al modelo para obtener las características faciales
            val inputFeature1: TensorBuffer = loadImageIntoTensorBuffer(image1)
            val inputFeature2: TensorBuffer = loadImageIntoTensorBuffer(image2)
            Log.d("faceDetection", "Proceso imagenes a tensorBuffer")
            val outputs1: MobileFaceNet.Outputs = model.process(inputFeature1)
            val outputs2: MobileFaceNet.Outputs = model.process(inputFeature2)
            Log.d("faceDetection", "Proceso MobileFaceNet.Outputs")

            // Obtener las características faciales como arrays de floats
            val embeddings1: FloatArray = outputs1.getOutputFeature0AsTensorBuffer().floatArray
            val embeddings2: FloatArray = outputs2.getOutputFeature0AsTensorBuffer().floatArray
            Log.d("faceDetection", "Proceso floatArray")

            // Comparar las características faciales (puedes usar alguna métrica de distancia)
            val distance: Float = calculateDistance(embeddings1, embeddings2)
            Log.e("faceDetection", "Valor de return : $distance")
            // Si la distancia es menor que un cierto umbral, se considera que son el mismo rostro
            if (distance < THRESHOLD) {
                Log.e("faceDetection", "paso")
                return true
            } else {
                Log.e("faceDetection", "no paso")
                return false
            }

            // Liberar recursos del modelo
            model.close()
        } catch (e: IOException) {
            // Manejar la excepción
            return false
        }
    }

    // Preprocesar una imagen en un TensorImage
    fun preprocessImage(bitmap: Bitmap?): TensorImage {
        // Cargar la imagen en un TensorImage
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        // Realizar cualquier preprocesamiento adicional aquí (por ejemplo, redimensionar, normalizar, etc.)
        return tensorImage
    }


    // Cargar una imagen preprocesada en un TensorBuffer
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadImageIntoTensorBuffer(tensorImage: TensorImage): TensorBuffer {
        // Obtener el TensorBuffer correspondiente a la imagen
        val bitmap = tensorImage.bitmap
        val byteBuffer = tensorImage.buffer
        val numChannels: Int = 3
        val tensorBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, bitmap.height, bitmap.width, numChannels),
            DataType.FLOAT32
        )
        tensorBuffer.loadBuffer(byteBuffer)
        return tensorBuffer
    }

    // Calcular la distancia euclidiana entre dos vectores de características faciales
    fun calculateDistance(embeddings1: FloatArray, embeddings2: FloatArray): Float {
        // Asumiendo que los vectores embeddings1 y embeddings2 tienen la misma longitud
        var sum = 0.0f
        for (i in embeddings1.indices) {
            sum += (embeddings1[i] - embeddings2[i]).pow(2)
        }
        return sqrt(sum.toDouble()).toFloat()
    }
}

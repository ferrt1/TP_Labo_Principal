package com.example.cypher_vault.model.facermanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cypher_vault.ml.MobileFaceNet
import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.TensorProcessor.Builder
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MobileFaceNetCore {

    fun inferencia(imagen1: Bitmap, imagen2 : Bitmap){
    // Carga el modelo MobileFaceNet previamente entrenado
    val model = MobileFaceNet.newInstance(context)

    // Crea un TensorImage para procesar las imágenes de entrada
    var inputImageBuffer = TensorImage(DataType.FLOAT32)
    val inputShape = model.getInputTensor(0).shape()
    inputImageBuffer.load(imagen1)
    inputImageBuffer = inputImageBuffer.resize(inputShape[1], inputShape[2])

    // Realiza la inferencia
    val outputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
    model.run(inputImageBuffer.buffer, outputBuffer.buffer)

    // Calcula la distancia euclidiana entre los vectores de características
    val featureVector1 = outputBuffer.floatArray
    val featureVector2 = getFeatureVectorFromSecondImage() // Obtén el vector de características de la segunda imagen
    val distance = calculateEuclideanDistance(featureVector1, featureVector2)

    // Define un umbral para determinar si los rostros son similares
    val threshold = 0.5
    if (distance < threshold) {
        println("Los rostros son similares.")
    } else {
        println("Los rostros no son similares.")
    }
    }
}

package com.example.cypher_vault.model.facermanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cypher_vault.ml.MobileFaceNet
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class TfliteManager {

    @Composable
    fun buildTfModel(){
        val model = MobileFaceNet.newInstance(LocalContext.current)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 112, 112, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Releases model resources if no longer used.
        model.close()
    }

}
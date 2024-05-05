package com.example.cypher_vault.model.facermanager

import android.graphics.PointF
import android.util.Log
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.database.Converters
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.suspendCoroutine
import kotlin.math.sqrt

class FaceDetectionTools<ImagesRegister> {

    val thresholdContours = 20.0
    val thresholdLandmarks = 20.0

    fun calculateEuclideanDistance(points1: List<PointF>, points2: List<PointF>): Double {
        require(points1.size == points2.size) { "Lists must have the same size" }

        var distanceSquared = 0.0
        for (i in points1.indices) {
            val dx = points1[i].x - points2[i].x
            val dy = points1[i].y - points2[i].y
            distanceSquared += dx * dx + dy * dy
        }

        return Math.sqrt(distanceSquared)
    }

    fun calcularDistanciaEuclidiana(lista1: List<PointF>, lista2: List<PointF>): Double {
        if (lista1.size != lista2.size) {
            throw IllegalArgumentException("Las listas deben tener el mismo tamaño")
        }

        return lista1.zip(lista2) { punto1, punto2 ->
            val dx = punto1.x - punto2.x
            val dy = punto1.y - punto2.y
            sqrt(dx * dx + dy * dy.toDouble())
        }.sum()
    }

    fun areFacesSimilar(
        face1Points: List<PointF>,
        face2Points: List<PointF>,
        threshold: Double
    ): Boolean {
        val distance = calcularDistanciaEuclidiana(face1Points, face2Points)
        return distance <= threshold
    }

    // Función para extraer los puntos de contorno de la cadena de datos y devolver una lista de PointF
    fun extractPoints(contoursData: String): List<PointF> {
        val pattern = Regex("PointF\\((\\d+\\.\\d+), (\\d+\\.\\d+)\\)")
        val matches = pattern.findAll(contoursData)
        val points = matches.map {
            val x = it.groupValues[1].toFloat()
            val y = it.groupValues[2].toFloat()
            PointF(x, y)
        }.toList()
        return points
    }

    // Datos proporcionados
    val contoursData1 =
        "FaceContour{type=1, points=[PointF(716.0, 301.0), PointF(739.0, 302.0), PointF(781.0, 309.0), PointF(813.0, 323.0), PointF(836.0, 344.0), PointF(855.0, 371.0), PointF(864.0, 399.0), PointF(868.0, 430.0), PointF(868.0, 460.0), PointF(867.0, 492.0), PointF(862.0, 525.0), PointF(854.0, 558.0), PointF(839.0, 587.0), PointF(821.0, 606.0), PointF(802.0, 624.0), PointF(783.0, 636.0), PointF(764.0, 645.0), PointF(738.0, 650.0), PointF(718.0, 652.0), PointF(699.0, 649.0), PointF(677.0, 642.0), PointF(661.0, 632.0), PointF(647.0, 620.0), PointF(632.0, 602.0), PointF(620.0, 583.0), PointF(608.0, 556.0), PointF(600.0, 522.0), PointF(597.0, 489.0), PointF(596.0, 458.0), PointF(597.0, 429.0), PointF(598.0, 399.0), PointF(603.0, 372.0), PointF(614.0, 347.0), PointF(631.0, 325.0), PointF(656.0, 311.0), PointF(694.0, 303.0)]}"
    val contoursData2 =
        "FaceContour{type=1, points=[PointF(503.0, 302.0), PointF(524.0, 302.0), PointF(564.0, 309.0), PointF(593.0, 321.0), PointF(614.0, 341.0), PointF(632.0, 365.0), PointF(639.0, 390.0), PointF(643.0, 420.0), PointF(643.0, 450.0), PointF(642.0, 480.0), PointF(639.0, 512.0), PointF(632.0, 545.0), PointF(619.0, 572.0), PointF(603.0, 591.0), PointF(586.0, 608.0), PointF(569.0, 620.0), PointF(552.0, 629.0), PointF(528.0, 634.0), PointF(510.0, 636.0), PointF(493.0, 634.0), PointF(471.0, 628.0), PointF(455.0, 620.0), PointF(440.0, 609.0), PointF(424.0, 591.0), PointF(410.0, 574.0), PointF(397.0, 547.0), PointF(387.0, 515.0), PointF(382.0, 483.0), PointF(380.0, 453.0), PointF(380.0, 424.0), PointF(382.0, 395.0), PointF(387.0, 369.0), PointF(400.0, 345.0), PointF(418.0, 324.0), PointF(443.0, 311.0), PointF(481.0, 303.0)]}"
    val contoursData3 =
        "FaceContour{type=1, points=[PointF(508.0, 294.0), PointF(529.0, 296.0), PointF(569.0, 304.0), PointF(597.0, 317.0), PointF(617.0, 339.0), PointF(634.0, 364.0), PointF(641.0, 391.0), PointF(643.0, 421.0), PointF(643.0, 450.0), PointF(641.0, 480.0), PointF(637.0, 512.0), PointF(629.0, 544.0), PointF(615.0, 571.0), PointF(600.0, 588.0), PointF(583.0, 605.0), PointF(566.0, 616.0), PointF(548.0, 623.0), PointF(525.0, 629.0), PointF(507.0, 630.0), PointF(489.0, 628.0), PointF(467.0, 622.0), PointF(451.0, 614.0), PointF(437.0, 603.0), PointF(421.0, 586.0), PointF(408.0, 568.0), PointF(395.0, 542.0), PointF(387.0, 510.0), PointF(382.0, 478.0), PointF(381.0, 448.0), PointF(382.0, 418.0), PointF(384.0, 389.0), PointF(390.0, 362.0), PointF(404.0, 337.0), PointF(422.0, 316.0), PointF(448.0, 303.0), PointF(486.0, 295.0)]}"

    // Crear variables contours1, contours2 y contours3 como listas de PointF
    val contours1 = extractPoints(contoursData1)
    val contours2 = extractPoints(contoursData2)
    val contours3 = extractPoints(contoursData3)

    fun testDeContornosHardCodeados() {
        // Compara las muestras
        val areSimilar12 = areFacesSimilar(contours1, contours2, thresholdContours)
        val areSimilar23 = areFacesSimilar(contours2, contours3, thresholdContours)
        val areSimilar13 = areFacesSimilar(contours1, contours3, thresholdContours)

        // Imprime los resultados
        Log.d("faceDetection", "¿La primera y la segunda muestra son similares? $areSimilar12")
        Log.d("faceDetection", "¿La segunda y la tercera muestra son similares? $areSimilar23")
        Log.d("faceDetection", "¿La primera y la tercera muestra son similares? $areSimilar13")
    }

//    fun similitudDeCapturas(imagesRegister: List<ImagesRegister?>?, faceContours: List<FaceContour>, faceLandMarks: List<FaceLandmark>): Boolean {
//        val convertImagesRegisterContour : List<FaceContour> = convertirJsonToListContour(imagesRegister)
//        val convertImagesRegisterLandmark : List<FaceLandmark> = convertirJsonToListLandmark(imagesRegister)
//        val ret : Boolean = false
//        return ret
//    }

    fun similitudDeCapturas(
        authenticationController: AuthenticationController,
        userId: String,
        faceContours: List<FaceContour>,
        faceLandMarks: List<FaceLandmark>
    ): Boolean = runBlocking {
        Log.d("faceDetection", "antes de capturar las listas")
        val imagesRegisterContourDeferred = async { authenticationController.obtenerContour(userId) }
        val imagesRegisterLandmarkDeferred = async { authenticationController.obtenerLandMark(userId) }
        Log.d("faceDetection", "medio de capturar las listas")
        val imagesRegisterContour = imagesRegisterContourDeferred.await()
        val imagesRegisterLandmark = imagesRegisterLandmarkDeferred.await()
        Log.d("faceDetection", "termino de capturar las listas")

        // Calcular la cantidad de elementos necesarios para cumplir el 50% del total
        val requiredSimilarityCount = (imagesRegisterContour.size * 0.5).toInt()


        val tamanioListaContour = imagesRegisterContour.size
        val tamanioListaLandmark = imagesRegisterLandmark.size
        Log.d("faceDetection", "sizeContour: $tamanioListaContour  sizeLandmark: $tamanioListaLandmark")

        // Contador de similitudes
        var contourSimilarityCount = 0
        var landmarkSimilarityCount = 0
        Log.d("faceDetection", "la lista Contours:  $faceContours")
        // Comparar los puntos uno a uno y calcular la similitud
        for (i in 0 until faceContours.size) {
            Log.d("faceDetection", "Loop $i  de Contours: ")
            val printLista1 = imagesRegisterContour[i]
            val printLista2 = faceContours[i]
            Log.d("faceDetection", "Loop $i  de Contours: Lista1: $printLista1 Lista2: $printLista2")
            val irc = convertStringFormat(convertirAStringC(imagesRegisterContour[i]))
            val fc = convertirAStringC(faceContours[i])
            Log.d("faceDetection", "///////// Loop $i  de Contours: Lista1: $irc Lista2: $fc")
            val ircP = extractPoints(irc)
            val fcP = extractPoints(fc)
            val tamano1 = ircP.size
            val tamano2 = fcP.size
            var contourSimilar : Boolean = false
            if(ircP.size == fcP.size){
                contourSimilar = areFacesSimilar(ircP, fcP, thresholdContours)
                Log.d("faceDetection", "El size de la lista Contours:  TRUE")
            }else{
                Log.e("faceDetection", "El size de la lista Contours:  es diferente : 1: $tamano1 2: $tamano2")
            }

            if (contourSimilar) contourSimilarityCount++
        }
        // Comparar los puntos uno a uno y calcular la similitud
        Log.e("faceDetection", "la lista LandMarks:  $faceLandMarks")
        for (i in 0 until faceLandMarks.size) {
            val irl = convertirAStringL(imagesRegisterLandmark[i])
            val fl = convertirAStringL(faceLandMarks[i])
            Log.e("faceDetection", "// Como estan las listas: Lista 1: $irl Lista 2: $fl")
            val irlP = extractPoints(irl)
            val flP = extractPoints(fl)
            Log.d("faceDetection", "// Puntos Extraidos: Lista 1: $irlP Lista 2: $flP")

            val landmarkSimilar = areFacesSimilar(irlP, flP, thresholdLandmarks)
            if (landmarkSimilar) landmarkSimilarityCount++
        }

        // Verificar si la cantidad de similitudes cumple con el 60% requerido
        Log.e("faceDetection", "Contador de TRUE similitud contorno: $contourSimilarityCount y requiero : $requiredSimilarityCount")
        Log.e("faceDetection", "Contador de TRUE similitud landmark: $landmarkSimilarityCount y requiero : $requiredSimilarityCount")

        return@runBlocking  landmarkSimilarityCount >= (requiredSimilarityCount+1) || contourSimilarityCount >= requiredSimilarityCount
    }

    private fun convertirAStringC(faceContour: FaceContour): String {
        return faceContour.toString()
    }

    private fun convertirAStringL(faceLandmark: FaceLandmark): String {
        return faceLandmark.toString()
    }

    fun convertStringFormat(input: String): String {
        val faceContourRegex = Regex("""FaceContour\{type=(\d+), points=\[(.*?)\]\}""")
        val matchResult = faceContourRegex.find(input)

        if (matchResult != null) {
            val type = matchResult.groupValues[1]
            val points = matchResult.groupValues[2].substringBefore("]").substringAfter("[").split("}, ").map { point ->
                val x = point.substringAfter("{x=").substringBefore(",").trim().toFloat()
                val y = point.substringAfter("y=").substringBefore("}").trim().toFloat()
                "PointF($x, $y)"
            }.joinToString(", ")
            return "FaceContour{type=$type, points=[$points]}"
        }
        return ""
    }
}

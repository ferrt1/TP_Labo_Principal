package com.example.cypher_vault.model.facermanager

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

/*
Interfaz de llamado externo

val faceDetectionActivity = FaceDetectionActivity()
faceDetectionActivity.detectFaces(yourBitmap)

*/


class FaceDetectionManager {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(highAccuracyOpts)

    fun detectFaces(image: Bitmap) {
        Log.d("faceDetection", "Comienza el proceso de deteccion de imagen")
        val inputImage = InputImage.fromBitmap(image, 0)
        detector.process(inputImage)
            .addOnSuccessListener(OnSuccessListener { faces ->
                for (face in faces) {
                    val faceContours = face.allContours
                    val faceLandMarks = face.allLandmarks
                    for (contours in faceContours)
                        Log.d("faceDetection", "contours: $contours")
                    for (landMarks in faceLandMarks)
                        Log.d("faceDetection", "landMarks: $landMarks")

                    /// Probabilidades de ojos abiertos y sonrisa
                    if (face.leftEyeOpenProbability != null) {
                        val leftEyeOpenProb = face.leftEyeOpenProbability
                        Log.d("faceDetection", "Left eye open probability: $leftEyeOpenProb")
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        Log.d("faceDetection", "Right eye open probability: $rightEyeOpenProb")
                    }
                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                        Log.d("faceDetection", "Smiling probability: $smileProb")
                    }

                    /// Rotacion de rostro
                    val rotY = face.headEulerAngleY
                    Log.d("faceDetection", "Head rotation Y: $rotY degrees")
                    val rotZ = face.headEulerAngleZ
                    Log.d("faceDetection", "Head tilt Z: $rotZ degrees")

                    // Axis-aligned bounding rectangle of the detected face
                    val bounds = face.boundingBox
                    Log.d("faceDetection", "Bounds: $bounds")

                    // Datos landMark otro metodo
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                    leftEar?.let {
                        val leftEarPos = leftEar.position
                        Log.d("faceDetection", "Left ear position: $leftEarPos")
                    }
                    val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                    leftEyeContour?.let {
                        Log.d("faceDetection", "Left eye contour points: $it")
                    }
                    val upperLipBottomContour =
                        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                    upperLipBottomContour?.let {
                        Log.d("faceDetection", "Upper lip bottom contour points: $it")
                    }

                    if (face.trackingId != null) {
                        val id = face.trackingId
                        Log.d("faceDetection", "Tracking ID: $id")
                    }
                }
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.e("errorFaceDetection", "Error detecting faces: $e")
            })
    }
}
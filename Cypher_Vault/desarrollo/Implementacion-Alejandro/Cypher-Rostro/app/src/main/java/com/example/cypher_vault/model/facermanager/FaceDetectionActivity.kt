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


class FaceDetectionActivity {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(highAccuracyOpts)

    fun detectFaces(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        detector.process(inputImage)
            .addOnSuccessListener(OnSuccessListener { faces ->
                for (face in faces) {
                    val bounds = face.boundingBox
                    Log.d("faceDetection", "Bounds: $bounds")
                    val rotY = face.headEulerAngleY
                    Log.d("faceDetection", "Head rotation Y: $rotY degrees")
                    val rotZ = face.headEulerAngleZ
                    Log.d("faceDetection", "Head tilt Z: $rotZ degrees")

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

                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                        Log.d("faceDetection", "Smiling probability: $smileProb")
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        Log.d("faceDetection", "Right eye open probability: $rightEyeOpenProb")
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
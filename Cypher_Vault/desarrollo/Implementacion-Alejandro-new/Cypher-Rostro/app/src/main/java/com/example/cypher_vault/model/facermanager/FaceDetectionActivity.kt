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
                    Log.e("faceDetection", "For faceContours")
                    for (contours in faceContours)
                        Log.d("faceDetection", "contours: $contours")
                    Log.e("faceDetection", "For faceLandMarks")
                    for (landMarks in faceLandMarks)
                        Log.d("faceDetection", "landMarks: $landMarks")

                    /// Probabilidades de ojos abiertos y sonrisa
                    Log.e("faceDetection", "Ojos y sonrisa")
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
                    Log.e("faceDetection", "Rotacion")
                    val rotY = face.headEulerAngleY
                    Log.d("faceDetection", "Head rotation Y: $rotY degrees")
                    val rotZ = face.headEulerAngleZ
                    Log.d("faceDetection", "Head tilt Z: $rotZ degrees")

                    // Axis-aligned bounding rectangle of the detected face
                    Log.e("faceDetection", "Rectangulo para el rostro")
                    val bounds = face.boundingBox
                    Log.d("faceDetection", "Bounds: $bounds")

                    // Obtener landmarks faciales
                    Log.e("faceDetection", "getLandmark")
                    val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
                    leftCheek?.let {
                        val leftCheekPos = leftCheek.position
                        Log.d("faceDetection", "Left cheek position: $leftCheekPos")
                    }

                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                    leftEar?.let {
                        val leftEarPos = leftEar.position
                        Log.d("faceDetection", "Left ear position: $leftEarPos")
                    }

                    val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
                    leftEye?.let {
                        val leftEyePos = leftEye.position
                        Log.d("faceDetection", "Left eye position: $leftEyePos")
                    }

                    val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)
                    mouthBottom?.let {
                        val mouthBottomPos = mouthBottom.position
                        Log.d("faceDetection", "Mouth bottom position: $mouthBottomPos")
                    }

                    val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)
                    mouthLeft?.let {
                        val mouthLeftPos = mouthLeft.position
                        Log.d("faceDetection", "Mouth left position: $mouthLeftPos")
                    }

                    val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)
                    mouthRight?.let {
                        val mouthRightPos = mouthRight.position
                        Log.d("faceDetection", "Mouth right position: $mouthRightPos")
                    }

                    val noseBase = face.getLandmark(FaceLandmark.NOSE_BASE)
                    noseBase?.let {
                        val noseBasePos = noseBase.position
                        Log.d("faceDetection", "Nose base position: $noseBasePos")
                    }

                    val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
                    rightCheek?.let {
                        val rightCheekPos = rightCheek.position
                        Log.d("faceDetection", "Right cheek position: $rightCheekPos")
                    }

                    val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)
                    rightEar?.let {
                        val rightEarPos = rightEar.position
                        Log.d("faceDetection", "Right ear position: $rightEarPos")
                    }

                    val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
                    rightEye?.let {
                        val rightEyePos = rightEye.position
                        Log.d("faceDetection", "Right eye position: $rightEyePos")
                    }

                    // Obtener contornos faciales
                    Log.e("faceDetection", "getContour")
                    val faceContour = face.getContour(FaceContour.FACE)?.points
                    faceContour?.let {
                        Log.d("faceDetection", "Face contour points: $faceContour")
                    }

                    val leftCheekContour = face.getContour(FaceContour.LEFT_CHEEK)?.points
                    leftCheekContour?.let {
                        Log.d("faceDetection", "Left cheek contour points: $leftCheekContour")
                    }

                    val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                    leftEyeContour?.let {
                        Log.d("faceDetection", "Left eye contour points: $leftEyeContour")
                    }

                    val leftEyebrowBottomContour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
                    leftEyebrowBottomContour?.let {
                        Log.d("faceDetection", "Left eyebrow bottom contour points: $leftEyebrowBottomContour")
                    }

                    val leftEyebrowTopContour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
                    leftEyebrowTopContour?.let {
                        Log.d("faceDetection", "Left eyebrow top contour points: $leftEyebrowTopContour")
                    }

                    val lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points
                    lowerLipBottomContour?.let {
                        Log.d("faceDetection", "Lower lip bottom contour points: $lowerLipBottomContour")
                    }

                    val lowerLipTopContour = face.getContour(FaceContour.LOWER_LIP_TOP)?.points
                    lowerLipTopContour?.let {
                        Log.d("faceDetection", "Lower lip top contour points: $lowerLipTopContour")
                    }

                    val noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM)?.points
                    noseBottomContour?.let {
                        Log.d("faceDetection", "Nose bottom contour points: $noseBottomContour")
                    }

                    val noseBridgeContour = face.getContour(FaceContour.NOSE_BRIDGE)?.points
                    noseBridgeContour?.let {
                        Log.d("faceDetection", "Nose bridge contour points: $noseBridgeContour")
                    }

                    val rightCheekContour = face.getContour(FaceContour.RIGHT_CHEEK)?.points
                    rightCheekContour?.let {
                        Log.d("faceDetection", "Right cheek contour points: $rightCheekContour")
                    }

                    val rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)?.points
                    rightEyeContour?.let {
                        Log.d("faceDetection", "Right eye contour points: $rightEyeContour")
                    }

                    val rightEyebrowBottomContour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points
                    rightEyebrowBottomContour?.let {
                        Log.d("faceDetection", "Right eyebrow bottom contour points: $rightEyebrowBottomContour")
                    }

                    val rightEyebrowTopContour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
                    rightEyebrowTopContour?.let {
                        Log.d("faceDetection", "Right eyebrow top contour points: $rightEyebrowTopContour")
                    }

                    val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                    upperLipBottomContour?.let {
                        Log.d("faceDetection", "Upper lip bottom contour points: $upperLipBottomContour")
                    }

                    val upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
                    upperLipTopContour?.let {
                        Log.d("faceDetection", "Upper lip top contour points: $upperLipTopContour")
                    }

                    /// TEST HARD CODEADO EN FACETOOLS
                    Log.e("faceDetection", "Test HardCodeado")
                    val faceTools = FaceDetectionTools()
                    faceTools.testDeContornosHardCodeados()
                }
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.e("errorFaceDetection", "Error detecting faces: $e")
            })
    }
}
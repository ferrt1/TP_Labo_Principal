package com.example.cypher_vault.model.mtcnn

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Point
import com.example.cypher_vault.model.utils.MyUtil
import org.tensorflow.lite.Interpreter
import java.util.Vector
import kotlin.math.ceil

class MTCNN(assetManager: AssetManager) {
    private val factor = 0.709f
    private val pNetThreshold = 0.6f
    private val rNetThreshold = 0.7f
    private val oNetThreshold = 0.7f

    private val pInterpreter: Interpreter
    private val rInterpreter: Interpreter
    private val oInterpreter: Interpreter

    init {
        val options = Interpreter.Options().apply { numThreads = 4 }
        pInterpreter = Interpreter(MyUtil.loadModelFile(assetManager, "pnet.tflite"), options)
        rInterpreter = Interpreter(MyUtil.loadModelFile(assetManager, "rnet.tflite"), options)
        oInterpreter = Interpreter(MyUtil.loadModelFile(assetManager, "onet.tflite"), options)
    }

    fun detectFaces(bitmap: Bitmap, minFaceSize: Int): Vector<Box> {
        var boxes: Vector<Box>
        try {
            boxes = pNet(bitmap, minFaceSize)
            squareLimit(boxes, bitmap.width, bitmap.height)
            boxes = rNet(bitmap, boxes)
            squareLimit(boxes, bitmap.width, bitmap.height)
            boxes = oNet(bitmap, boxes)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return Vector()
        }
        return boxes
    }

    private fun squareLimit(boxes: Vector<Box>, w: Int, h: Int) {
        for (box in boxes) {
            box.toSquareShape()
            box.limitSquare(w, h)
        }
    }

    private fun pNet(bitmap: Bitmap, minSize: Int): Vector<Box> {
        val whMin = minOf(bitmap.width, bitmap.height)
        var currentFaceSize = minSize.toFloat()
        val totalBoxes = Vector<Box>()
        while (currentFaceSize <= whMin) {
            val scale = 12.0f / currentFaceSize
            val resizedBitmap = MyUtil.bitmapResize(bitmap, scale)
            val w = resizedBitmap.width
            val h = resizedBitmap.height
            val outW = (ceil(w * 0.5 - 5) + 0.5).toInt()
            val outH = (ceil(h * 0.5 - 5) + 0.5).toInt()
            val prob1 = Array(1) { Array(outW) { Array(outH) { FloatArray(2) } } }
            val conv4_2_BiasAdd = Array(1) { Array(outW) { Array(outH) { FloatArray(4) } } }
            pNetForward(resizedBitmap, prob1, conv4_2_BiasAdd)
            val transposedProb1 = MyUtil.transposeBatch(prob1)
            val transposedConv4_2_BiasAdd = MyUtil.transposeBatch(conv4_2_BiasAdd)
            val curBoxes = Vector<Box>()
            generateBoxes(transposedProb1, transposedConv4_2_BiasAdd, scale, curBoxes)
            nms(curBoxes, 0.5f, "Union")
            curBoxes.filter { !it.deleted }.forEach { totalBoxes.addElement(it) }
            currentFaceSize /= factor
        }
        nms(totalBoxes, 0.7f, "Union")
        boundingBoxRegression(totalBoxes)
        return updateBoxes(totalBoxes)
    }

    private fun pNetForward(bitmap: Bitmap, prob1: Array<Array<Array<FloatArray>>>, conv4_2_BiasAdd: Array<Array<Array<FloatArray>>>) {
        val img = MyUtil.normalizeImage(bitmap)
        val pNetIn = arrayOf(img).apply { MyUtil.transposeBatch(this) }
        val outputs: MutableMap<Int, Any> = hashMapOf(
            pInterpreter.getOutputIndex("pnet/prob1") to prob1 as Any,
            pInterpreter.getOutputIndex("pnet/conv4-2/BiasAdd") to conv4_2_BiasAdd as Any
        )
        pInterpreter.runForMultipleInputsOutputs(arrayOf(pNetIn), outputs)
    }

    private fun generateBoxes(prob1: Array<Array<Array<FloatArray>>>, conv4_2_BiasAdd: Array<Array<Array<FloatArray>>>, scale: Float, boxes: Vector<Box>) {
        val h = prob1[0].size
        val w = prob1[0][0].size
        for (y in 0 until h) {
            for (x in 0 until w) {
                val score = prob1[0][y][x][1]
                if (score > pNetThreshold) {
                    val box = Box()
                    box.score = score
                    box.box[0] = Math.round(x * 2 / scale)
                    box.box[1] = Math.round(y * 2 / scale)
                    box.box[2] = Math.round((x * 2 + 11) / scale)
                    box.box[3] = Math.round((y * 2 + 11) / scale)
                    for (i in 0..3) {
                        box.bbr[i] = conv4_2_BiasAdd[0][y][x][i]
                    }
                    boxes.addElement(box)
                }
            }
        }
    }

    private fun nms(boxes: Vector<Box>, threshold: Float, method: String) {
        for (i in boxes.indices) {
            val box = boxes[i]
            if (!box.deleted) {
                for (j in i + 1 until boxes.size) {
                    val box2 = boxes[j]
                    if (!box2.deleted) {
                        val x1 = maxOf(box.box[0], box2.box[0])
                        val y1 = maxOf(box.box[1], box2.box[1])
                        val x2 = minOf(box.box[2], box2.box[2])
                        val y2 = minOf(box.box[3], box2.box[3])
                        if (x2 >= x1 && y2 >= y1) {
                            val areaIoU = (x2 - x1 + 1) * (y2 - y1 + 1)
                            val iou = if (method == "Union") {
                                1.0f * areaIoU / (box.area() + box2.area() - areaIoU)
                            } else {
                                1.0f * areaIoU / minOf(box.area(), box2.area())
                            }
                            if (iou >= threshold) {
                                if (box.score > box2.score) box2.deleted = true else box.deleted = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun boundingBoxRegression(boxes: Vector<Box>) {
        for (box in boxes) {
            box.calibrate()
        }
    }

    private fun rNet(bitmap: Bitmap, boxes: Vector<Box>): Vector<Box> {
        val num = boxes.size
        val rNetIn = Array(num) { Array(24) { Array(24) { FloatArray(3) } } }
        for (i in 0 until num) {
            var curCrop = MyUtil.cropAndResize(bitmap, boxes[i], 24)
            curCrop = MyUtil.transposeImage(curCrop)
            rNetIn[i] = curCrop
        }
        rNetForward(rNetIn, boxes)
        for (i in 0 until num) {
            if (boxes[i].score < rNetThreshold) {
                boxes[i].deleted = true
            }
        }
        nms(boxes, 0.7f, "Union")
        boundingBoxRegression(boxes)
        return updateBoxes(boxes)
    }

    private fun rNetForward(rNetIn: Array<Array<Array<FloatArray>>>, boxes: Vector<Box>) {
        val num = rNetIn.size
        val prob1 = Array(num) { FloatArray(2) }
        val conv5_2_conv5_2 = Array(num) { FloatArray(4) }
        val outputs: MutableMap<Int, Any> = hashMapOf(
            rInterpreter.getOutputIndex("rnet/prob1") to prob1 as Any,
            rInterpreter.getOutputIndex("rnet/conv5-2/conv5-2") to conv5_2_conv5_2 as Any
        )
        rInterpreter.runForMultipleInputsOutputs(arrayOf(rNetIn), outputs)
        for (i in 0 until num) {
            boxes[i].score = prob1[i][1]
            for (j in 0..3) {
                boxes[i].bbr[j] = conv5_2_conv5_2[i][j]
            }
        }
    }

    private fun oNet(bitmap: Bitmap, boxes: Vector<Box>): Vector<Box> {
        val num = boxes.size
        val oNetIn = Array(num) { Array(48) { Array(48) { FloatArray(3) } } }
        for (i in 0 until num) {
            var curCrop = MyUtil.cropAndResize(bitmap, boxes[i], 48)
            curCrop = MyUtil.transposeImage(curCrop)
            oNetIn[i] = curCrop
        }
        oNetForward(oNetIn, boxes)
        for (i in 0 until num) {
            if (boxes[i].score < oNetThreshold) {
                boxes[i].deleted = true
            }
        }
        boundingBoxRegression(boxes)
        nms(boxes, 0.7f, "Min")
        return updateBoxes(boxes)
    }

    private fun oNetForward(oNetIn: Array<Array<Array<FloatArray>>>, boxes: Vector<Box>) {
        val num = oNetIn.size
        val prob1 = Array(num) { FloatArray(2) }
        val conv6_2_conv6_2 = Array(num) { FloatArray(4) }
        val conv6_3_conv6_3 = Array(num) { FloatArray(10) }
        val outputs: MutableMap<Int, Any> = hashMapOf(
            oInterpreter.getOutputIndex("onet/prob1") to prob1 as Any,
            oInterpreter.getOutputIndex("onet/conv6-2/conv6-2") to conv6_2_conv6_2 as Any,
            oInterpreter.getOutputIndex("onet/conv6-3/conv6-3") to conv6_3_conv6_3 as Any
        )
        oInterpreter.runForMultipleInputsOutputs(arrayOf(oNetIn), outputs)
        for (i in 0 until num) {
            boxes[i].score = prob1[i][1]
            for (j in 0..3) {
                boxes[i].bbr[j] = conv6_2_conv6_2[i][j]
            }
            for (j in 0..4) {
                val x = Math.round(boxes[i].left() + (conv6_3_conv6_3[i][j] * boxes[i].width()))
                val y = Math.round(boxes[i].top() + (conv6_3_conv6_3[i][j + 5] * boxes[i].height()))
                boxes[i].landmark[j] = Point(x, y)
            }
        }
    }


    companion object {
        fun updateBoxes(boxes: Vector<Box>): Vector<Box> {
            val b = Vector<Box>()
            for (box in boxes) {
                if (!box.deleted) {
                    b.addElement(box)
                }
            }
            return b
        }
    }
}

package com.example.cypher_vault.model.mtcnn

import android.graphics.Point
import android.graphics.Rect
import kotlin.math.max

class Box {
    var box: IntArray = IntArray(4)
    var score: Float = 0.0f
    var bbr: FloatArray = FloatArray(4)
    var deleted: Boolean = false
    var landmark: Array<Point> = Array(5) { Point() }

    fun left(): Int {
        return box[0]
    }

    fun right(): Int {
        return box[2]
    }

    fun top(): Int {
        return box[1]
    }

    fun bottom(): Int {
        return box[3]
    }

    fun width(): Int {
        return box[2] - box[0] + 1
    }

    fun height(): Int {
        return box[3] - box[1] + 1
    }

    fun transform2Rect(): Rect {
        val rect = Rect()
        rect.left = box[0]
        rect.top = box[1]
        rect.right = box[2]
        rect.bottom = box[3]
        return rect
    }

    fun area(): Int {
        return width() * height()
    }

    fun calibrate() {
        val w = box[2] - box[0] + 1
        val h = box[3] - box[1] + 1
        box[0] = (box[0] + w * bbr[0]).toInt()
        box[1] = (box[1] + h * bbr[1]).toInt()
        box[2] = (box[2] + w * bbr[2]).toInt()
        box[3] = (box[3] + h * bbr[3]).toInt()
        for (i in bbr.indices) bbr[i] = 0.0f
    }

    fun toSquareShape() {
        val h = height()
        val w = width()
        if (w > h) {
            box[1] -= (w - h) / 2
            box[3] += (w - h) / 2
        } else {
            box[0] -= (h - w) / 2
            box[2] += (h - w) / 2
        }
    }

    fun limitSquare(maxW: Int, maxH: Int) {
        toSquareShape()
        if (box[0] < 0) {
            val len = -box[0]
            box[0] += len
            box[2] += len
        }
        if (box[1] < 0) {
            val len = -box[1]
            box[1] += len
            box[3] += len
        }
        if (box[2] > maxW) {
            val len = box[2] - maxW + 1
            box[0] -= len
            box[2] -= len
        }
        if (box[3] > maxH) {
            val len = box[3] - maxH + 1
            box[1] -= len
            box[3] -= len
        }
    }

    fun limitSquare2(maxW: Int, maxH: Int) {
        toSquareShape()
        val len = max(width(), height())
        box[0] = max(0, box[0])
        box[1] = max(0, box[1])
        box[2] = box[0] + len
        box[3] = box[1] + len
        if (box[2] > maxW) {
            val diff = box[2] - maxW
            box[0] -= diff
            box[2] -= diff
        }
        if (box[3] > maxH) {
            val diff = box[3] - maxH
            box[1] -= diff
            box[3] -= diff
        }
    }
}

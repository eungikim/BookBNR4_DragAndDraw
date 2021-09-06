package me.eungi.draganddraw

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Box(private val start: PointF, var end: PointF = start, var rotate: Float = 0f) : Parcelable {

    val left: Float
        get() = Math.min(start.x, end.x)

    val right: Float
        get() = Math.max(start.x, end.x)

    val top: Float
        get() = Math.min(start.y, end.y)

    val bottom: Float
        get() = Math.max(start.y, end.y)

    val center: PointF
        get() {
            val y = (bottom - top) / 2 + top
            val x = (right - left) / 2 + left
            return PointF(x, y)
        }
}

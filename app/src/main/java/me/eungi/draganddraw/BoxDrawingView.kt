package me.eungi.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.atan2

private const val TAG = "BoxDrawingView"
private const val KEY_VIEW = "KEY_VIEW"
private const val KEY_BOX_LIST = "KEY_BOX_LIST"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var currentBox: Box? = null
    private var currentRotate: Float? = null
    private val boxen = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return super.onDraw(canvas)
        // 배경을 채운다
        canvas.drawPaint(backgroundPaint)
        boxen.forEach { box ->
            canvas.save()
            canvas.rotate(box.rotate, box.center.x, box.center.y)
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
            canvas.restore()
        }
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
//        printEvent(event)
        val pointCount = event.pointerCount
        for (p in 0 until pointCount) {
            if (event.getPointerId(p) == 0) {
                val current = PointF(event.getX(p), event.getY(p))
                Log.d(TAG, "PointerId[0] action: ${event.action}")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 박스 생성
                        currentBox = Box(current).also {
                            boxen.add(it)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        updateCurrentBox(current)
                    }
                    MotionEvent.ACTION_UP -> {
                        updateCurrentBox(current)
                        currentBox = null
                        currentRotate = null
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        currentBox = null
                        currentRotate = null
                    }
                }
            } else if (event.getPointerId(p) == 1) {
                val current = PointF(event.getX(p), event.getY(p))
                Log.d(TAG, "PointerId[1] action: ${event.action}")
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        rotateCurrentBox(current, currentRotate)
                    }
                    MotionEvent.ACTION_POINTER_2_UP -> {
                        currentRotate = currentBox?.rotate
                    }
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable(KEY_VIEW, super.onSaveInstanceState())
        putParcelableArray(KEY_BOX_LIST, boxen.toTypedArray())
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle: Bundle = state as? Bundle ?: return super.onRestoreInstanceState(state)
        super.onRestoreInstanceState(bundle.getParcelable(KEY_VIEW))
        bundle.getParcelableArray(KEY_BOX_LIST)?.forEach {
            boxen.add(it as Box)
        }
    }


    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }

    private fun rotateCurrentBox(current: PointF, currentRotate: Float?) {
        currentBox?.let {
            val preRotate: Float = currentRotate ?: 0f
            it.rotate = (calculationAngle(it.end to current) + preRotate) % 360
            invalidate()
        }
    }

    private fun calculationAngle(coordinates: Pair<PointF, PointF>): Float {
        val x = coordinates.second.x - coordinates.first.x
        val y = coordinates.second.y - coordinates.first.y
        return (atan2(y, x) * 180 / PI).toFloat()
    }
}

fun printEvent(ev: MotionEvent) {
    val historySize = ev.historySize
    val pointerCount = ev.pointerCount

    val action = when(ev.action) {
        0 -> "DOWN"
        1 -> "UP"
        2 -> "MOVE"
        3 -> "CANCEL"
        else -> "OTHER"
    }

    Log.d(TAG, "ME action $action pointer ${ev.actionIndex}")

//    Log.d(TAG, "HistorySize:$historySize, pointerCount$pointerCount")
//    for (h in 0 until historySize) {
//        Log.d(TAG, "At time ${ev.getHistoricalEventTime(h)}:")
//        for (p in 0 until pointerCount) {
//            Log.d(TAG, "  pointer ${ev.getPointerId(p)}: (${ev.getHistoricalX(p, h)},${ev.getHistoricalY(p, h)})")
//        }
//    }
//    System.out.printf("At time ${ev.eventTime}:")
//    for (p in 0 until pointerCount) {
//        System.out.printf(
//            "  pointer ${ev.getPointerId(p)}: (${ev.getX(p)},${ev.getY(p)})")
//    }
}
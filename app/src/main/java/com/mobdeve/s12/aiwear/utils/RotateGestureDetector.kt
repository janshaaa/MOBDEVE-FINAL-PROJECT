package com.mobdeve.s12.aiwear.utils

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.atan2

class RotateGestureDetector(context: Context, private val listener: OnRotateGestureListener) {

    private val context: Context = context.applicationContext
    private val slopTouch = ViewConfiguration.get(context).scaledTouchSlop

    private var fX = 0f
    private var fY = 0f
    private var sX = 0f
    private var sY = 0f

    private var rotation = 0f // New variable to store accumulated rotation

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                sX = event.x
                sY = event.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                fX = event.getX(0)
                fY = event.getY(0)
                sX = event.getX(1)
                sY = event.getY(1)
            }
            MotionEvent.ACTION_MOVE -> {
                val nfX = event.getX(0)
                val nfY = event.getY(0)
                val nsX = event.getX(1)
                val nsY = event.getY(1)

                val angleStart = Math.toDegrees(atan2((fY - sY).toDouble(), (fX - sX).toDouble()))
                val angleEnd = Math.toDegrees(atan2((nfY - nsY).toDouble(), (nfX - nsX).toDouble()))

                val rotation = angleEnd - angleStart
                this.rotation = rotation.toFloat() // Update accumulated rotation
                listener.onRotate(this.rotation, (nfX + nsX) / 2, (nfY + nsY) / 2)

                fX = nfX
                fY = nfY
                sX = nsX
                sY = nsY
            }
        }
        return true
    }

    interface OnRotateGestureListener {
        fun onRotate(rotation: Float, focusX: Float, focusY: Float)
    }

    fun getRotation(): Float {
        return rotation
    }

}


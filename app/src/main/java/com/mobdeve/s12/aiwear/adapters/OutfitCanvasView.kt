package com.mobdeve.s12.aiwear.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.lang.Math.sqrt
import kotlin.math.sqrt

class OutfitCanvasView : View {

    private val bitmaps: MutableList<BitmapData> = mutableListOf()
    private val paint = Paint()

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var activeBitmapIndex: Int? = null
    private var isMoving = false
    private var isResizing = false

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        // Initialize any attributes or variables here
    }

    fun addBitmap(bitmap: Bitmap) {
        Toast.makeText(
            context,
            "${bitmap} is",
            Toast.LENGTH_SHORT
        ).show()
        val centerX = width / 2f
        val centerY = height / 2f

        // Calculate the initial position to center the bitmap on the canvas
        val initialLeft = centerX - bitmap.width / 2f
        val initialTop = centerY - bitmap.height / 2f

        bitmaps.add(BitmapData(bitmap, Matrix().apply { postTranslate(initialLeft, initialTop) }))
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((bitmap, matrix) in bitmaps) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, matrix, paint)
            }
            else {
                Toast.makeText(
                    context,
                    "null",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                activeBitmapIndex = findActiveBitmap(event.x, event.y)
                activeBitmapIndex?.let {
                    isMoving = true
                    isResizing = isResizingTouch(event.x, event.y, bitmaps[it].matrix)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                activeBitmapIndex?.let {
                    if (isMoving) {
                        moveBitmap(it, event.x - lastTouchX, event.y - lastTouchY)
                    } else if (isResizing) {
                        resizeBitmap(it, event)
                    }
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isMoving = false
                isResizing = false
                activeBitmapIndex = null
            }
        }
        return true
    }

    private fun findActiveBitmap(touchX: Float, touchY: Float): Int? {
        for (i in bitmaps.indices.reversed()) {
            val rect = getBitmapRect(bitmaps[i].bitmap, bitmaps[i].matrix)
            if (rect.contains(touchX, touchY)) {
                return i
            }
        }
        return null
    }

    private fun isResizingTouch(touchX: Float, touchY: Float, matrix: Matrix): Boolean {
        val rect = getBitmapRect(bitmaps[activeBitmapIndex!!].bitmap, matrix)
        val resizeRect = RectF(rect.right - RESIZE_THRESHOLD, rect.bottom - RESIZE_THRESHOLD, rect.right, rect.bottom)
        return resizeRect.contains(touchX, touchY)
    }

    private fun getBitmapRect(bitmap: Bitmap, matrix: Matrix): RectF {
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        matrix.mapRect(rect)
        return rect
    }

    private fun moveBitmap(index: Int, dx: Float, dy: Float) {
        bitmaps[index].matrix.postTranslate(dx, dy)
    }

    private fun resizeBitmap(index: Int, event: MotionEvent) {
        val bitmapData = bitmaps[index]
        val rect = getBitmapRect(bitmapData.bitmap, bitmapData.matrix)
        val newWidth = event.x - rect.left
        val newHeight = event.y - rect.top
        if (newWidth > MIN_SIZE && newHeight > MIN_SIZE) {
            bitmapData.matrix.postScale(newWidth / rect.width(), newHeight / rect.height(), rect.left, rect.top)
        }
    }

    data class BitmapData(val bitmap: Bitmap, val matrix: Matrix)

    companion object {
        private const val RESIZE_THRESHOLD = 50f
        private const val MIN_SIZE = 100f
    }
}

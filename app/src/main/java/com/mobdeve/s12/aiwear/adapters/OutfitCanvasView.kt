package com.mobdeve.s12.aiwear.adapters

import com.mobdeve.s12.aiwear.models.BitmapData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.utils.RotateGestureDetector
import java.io.ByteArrayOutputStream

class OutfitCanvasView : View {

    private val bitmaps: MutableList<BitmapData> = mutableListOf()
    private val paint = Paint()

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var activeBitmapIndex: Int? = null
    private var isMoving = false
    private var isResizing = false
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var rotateGestureDetector: RotateGestureDetector


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
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        rotateGestureDetector = RotateGestureDetector(context, RotateListener())
    }

    fun addItem(clothesItem: ClothesItem, bitmap: Bitmap) {
        val centerX = width / 2f
        val centerY = height / 2f

        // Calculate the initial position to center the bitmap on the canvas
        val initialLeft = centerX - bitmap.width / 2f
        val initialTop = centerY - bitmap.height / 2f

        bitmaps.add(BitmapData(clothesItem.clothes_id, bitmap, Matrix().apply { postTranslate(initialLeft, initialTop) }, initialLeft, initialTop))
        invalidate()
    }

    fun removeItem(clothesItem: ClothesItem) {
        val index = findBitmapIndex(clothesItem)
        if (index >= 0 && index < bitmaps.size) {
            bitmaps.removeAt(index)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((_, bitmap, matrix, _, _) in bitmaps) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, matrix, paint)
            } else {

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 2) {
            scaleGestureDetector.onTouchEvent(event)
            rotateGestureDetector.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                activeBitmapIndex = findActiveBitmap(event.x, event.y)
                activeBitmapIndex?.let {
                    isMoving = true
                    isResizing = isResizingTouch(event, bitmaps[it].matrix)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                activeBitmapIndex?.let {
                    if (isMoving) {
                        moveBitmap(it, event.x - lastTouchX, event.y - lastTouchY)
                    } else if (isResizing) {
                        resizeAndRotateBitmap(it, event)
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
            val rect = getBitmapRect(bitmaps[i].bitmap!!, bitmaps[i].matrix)
            if (rect.contains(touchX, touchY)) {
                return i
            }
        }
        return null
    }

    private fun isResizingTouch(event: MotionEvent, matrix: Matrix): Boolean {
        if (event.pointerCount == 2) {
            val rect = getBitmapRect(bitmaps[activeBitmapIndex!!].bitmap!!, matrix)
            val resizeRect = RectF(rect.right - RESIZE_THRESHOLD, rect.bottom - RESIZE_THRESHOLD, rect.right, rect.bottom)

            val touchX1 = event.getX(0)
            val touchY1 = event.getY(0)
            val touchX2 = event.getX(1)
            val touchY2 = event.getY(1)

            return resizeRect.contains(touchX1, touchY1) && resizeRect.contains(touchX2, touchY2)
        }
        return false
    }


    private fun getBitmapRect(bitmap: Bitmap, matrix: Matrix): RectF {
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        matrix.mapRect(rect)
        return rect
    }

    private fun moveBitmap(index: Int, dx: Float, dy: Float) {
        val bitmapData = bitmaps[index]
        bitmapData.matrix.postTranslate(dx, dy)
        bitmapData.left += dx
        bitmapData.top += dy
    }

    private fun resizeAndRotateBitmap(index: Int, event: MotionEvent) {
        val bitmapData = bitmaps[index]
        val rect = getBitmapRect(bitmapData.bitmap!!, bitmapData.matrix)
        val newWidth = event.x - rect.left
        val newHeight = event.y - rect.top
        if (newWidth > MIN_SIZE && newHeight > MIN_SIZE) {
            // Calculate rotation based on the change in focus points
            val rotation = rotateGestureDetector.getRotation()
            // Apply scaling and rotation to the matrix
            bitmapData.matrix.postScale(newWidth / rect.width(), newHeight / rect.height(), rect.left, rect.top)
            bitmapData.matrix.postRotate(rotation, event.x, event.y)
        }
    }

    private fun findBitmapIndex(clothesItem: ClothesItem): Int {
        for (i in this.bitmaps.indices) {
            val bitmapData = this.bitmaps[i]
            // Assuming clothesItem has a unique identifier, adjust the condition accordingly
            if (bitmapData.id == clothesItem.clothes_id) {
                return i
            }
        }
        return -1
    }

    fun getBitmaps(): MutableList<BitmapData>{
        return bitmaps
    }

    fun saveCanvasToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)

        return bitmap
    }

    companion object {
        private const val RESIZE_THRESHOLD = 50f
        private const val MIN_SIZE = 100f
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            activeBitmapIndex?.let {
                val scaleFactor = detector.scaleFactor

                // Apply scaling to the matrix
                bitmaps[it].matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)

                // Invalidate the view to trigger a redraw
                invalidate()
            }
            return true
        }
    }

    private inner class RotateListener : RotateGestureDetector.OnRotateGestureListener {
        override fun onRotate(rotation: Float, focusX: Float, focusY: Float) {
            activeBitmapIndex?.let {
                bitmaps[it].matrix.postRotate(rotation, focusX, focusY)
                invalidate()
            }
        }
    }

}

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kamikaze.rotateview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView

/**
 * A @{code ImageView} which can rotate it's content.
 */
class RotateImageView : AppCompatImageView {
    private var currentDegree = 0 // [0, 359]
    private var startDegree = 0
    private var degree = 0
    private var clockwise = false
    private var enableAnimation = true
    private var animationStartTime: Long = 0
    private var animationEndTime: Long = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun enableAnimation(enable: Boolean) {
        enableAnimation = enable
    }

    // Rotate the view counter-clockwise
    fun setOrientation(degree: Int) {
        // make sure in the range of [0, 359]
        val degree = degree
            .makeSureInRegularRange()
            .round()

        if (degree == this.degree) return
        this.degree = degree
        startDegree = currentDegree
        animationStartTime = AnimationUtils.currentAnimationTimeMillis()
        var diff = this.degree - currentDegree
        diff = if (diff >= 0) diff else 360 + diff // make it in range [0, 359]

        // Make it in range [-179, 180]. That's the shorted distance between the
        // two angles
        diff = if (diff > 180) diff - 360 else diff
        clockwise = diff >= 0
        animationEndTime = (animationStartTime
                + Math.abs(diff) * 1000 / ANIMATION_SPEED)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val drawable: Drawable = drawable ?: return
        val bounds = drawable.bounds
        val w = bounds.right - bounds.left
        val h = bounds.bottom - bounds.top
        if (w == 0 || h == 0) return  // nothing to draw
        if (currentDegree != degree) {
            val time = AnimationUtils.currentAnimationTimeMillis()
            if (time < animationEndTime) {
                val deltaTime = (time - animationStartTime).toInt()
                var degree = startDegree + ANIMATION_SPEED * (if (clockwise) deltaTime else -deltaTime) / 1000
                degree = degree.makeSureInRegularRange()
                currentDegree = degree
                invalidate()
            } else {
                currentDegree = degree
            }
        }
        val left: Int = paddingLeft
        val top: Int = paddingTop
        val right: Int = paddingRight
        val bottom: Int = paddingBottom
        val width: Int = width - left - right
        val height: Int = height - top - bottom
        val saveCount = canvas.saveCount

        // Scale down the image first if required.
        if (scaleType === android.widget.ImageView.ScaleType.FIT_CENTER &&
            (width < w || height < h)
        ) {
            val ratio = Math.min(width.toFloat() / w, height.toFloat() / h)
            canvas.scale(ratio, ratio, width / 2.0f, height / 2.0f)
        }
        canvas.translate((left + width / 2).toFloat(), (top + height / 2).toFloat())
        canvas.rotate(-currentDegree.toFloat())
        canvas.translate((-w / 2).toFloat(), (-h / 2).toFloat())
        drawable.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private var mThumb: Bitmap? = null
    private var mThumbs: Array<Drawable?>? = null
    private var mThumbTransition: TransitionDrawable? = null
    fun setBitmap(bitmap: Bitmap?) {
        // Make sure uri and original are consistently both null or both
        // non-null.
        if (bitmap == null) {
            mThumb = null
            mThumbs = null
            setImageDrawable(null)
            visibility = GONE
            return
        }
        val param: ViewGroup.LayoutParams = layoutParams
        val miniThumbWidth: Int = (param.width
                - paddingLeft - paddingRight)
        val miniThumbHeight: Int = (param.height
                - paddingTop - paddingBottom)
        mThumb = ThumbnailUtils.extractThumbnail(
            bitmap, miniThumbWidth, miniThumbHeight
        )
        if (mThumbs == null || !enableAnimation) {
            mThumbs = arrayOfNulls(2)
            mThumbs!![1] = BitmapDrawable(context.resources, mThumb)
            setImageDrawable(mThumbs!![1])
        } else {
            mThumbs!![0] = mThumbs!![1]
            mThumbs!![1] = BitmapDrawable(context.resources, mThumb)
            mThumbTransition = TransitionDrawable(mThumbs)
            setImageDrawable(mThumbTransition)
            mThumbTransition!!.startTransition(500)
        }
        visibility = VISIBLE
    }

    companion object {
        private const val ANIMATION_SPEED = 270 // 270 deg/sec
    }
}
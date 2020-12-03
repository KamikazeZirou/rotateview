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
package com.kamikaze.rotatelayout

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
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

/**
 * A @{code ImageView} which can rotate it's content.
 */
class RotateImageView : AppCompatImageView {
    private var mCurrentDegree = 0 // [0, 359]
    private var mStartDegree = 0
    private var degree = 0
        private set
    private var mClockwise = false
    private var mEnableAnimation = true
    private var mAnimationStartTime: Long = 0
    private var mAnimationEndTime: Long = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context) : super(context) {}

    fun enableAnimation(enable: Boolean) {
        mEnableAnimation = enable
    }

    // Rotate the view counter-clockwise
    fun setOrientation(degree: Int) {
        // make sure in the range of [0, 359]
        var degree = degree
        degree = if (degree >= 0) degree % 360 else degree % 360 + 360
        degree = when (degree) {
            in (0..45), in (315..360) -> 0
            in (45..135) -> 90
            in (135..225) -> 180
            in (225..315) -> 270
            else -> 0
        }

        if (degree == this.degree) return
        this.degree = degree
        mStartDegree = mCurrentDegree
        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis()
        var diff = this.degree - mCurrentDegree
        diff = if (diff >= 0) diff else 360 + diff // make it in range [0, 359]

        // Make it in range [-179, 180]. That's the shorted distance between the
        // two angles
        diff = if (diff > 180) diff - 360 else diff
        mClockwise = diff >= 0
        mAnimationEndTime = (mAnimationStartTime
                + Math.abs(diff) * 1000 / ANIMATION_SPEED)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val drawable: Drawable = getDrawable() ?: return
        val bounds = drawable.bounds
        val w = bounds.right - bounds.left
        val h = bounds.bottom - bounds.top
        if (w == 0 || h == 0) return  // nothing to draw
        if (mCurrentDegree != degree) {
            val time = AnimationUtils.currentAnimationTimeMillis()
            if (time < mAnimationEndTime) {
                val deltaTime = (time - mAnimationStartTime).toInt()
                var degree = mStartDegree + ANIMATION_SPEED * (if (mClockwise) deltaTime else -deltaTime) / 1000
                degree = if (degree >= 0) degree % 360 else degree % 360 + 360
                mCurrentDegree = degree
                invalidate()
            } else {
                mCurrentDegree = degree
            }
        }
        val left: Int = getPaddingLeft()
        val top: Int = getPaddingTop()
        val right: Int = getPaddingRight()
        val bottom: Int = getPaddingBottom()
        val width: Int = getWidth() - left - right
        val height: Int = getHeight() - top - bottom
        val saveCount = canvas.saveCount

        // Scale down the image first if required.
        if (getScaleType() === android.widget.ImageView.ScaleType.FIT_CENTER &&
            (width < w || height < h)
        ) {
            val ratio = Math.min(width.toFloat() / w, height.toFloat() / h)
            canvas.scale(ratio, ratio, width / 2.0f, height / 2.0f)
        }
        canvas.translate((left + width / 2).toFloat(), (top + height / 2).toFloat())
        canvas.rotate(-mCurrentDegree.toFloat())
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
            setVisibility(GONE)
            return
        }
        val param: ViewGroup.LayoutParams = getLayoutParams()
        val miniThumbWidth: Int = (param.width
                - getPaddingLeft() - getPaddingRight())
        val miniThumbHeight: Int = (param.height
                - getPaddingTop() - getPaddingBottom())
        mThumb = ThumbnailUtils.extractThumbnail(
            bitmap, miniThumbWidth, miniThumbHeight
        )
        var drawable: Drawable
        if (mThumbs == null || !mEnableAnimation) {
            mThumbs = arrayOfNulls(2)
            mThumbs!![1] = BitmapDrawable(getContext().getResources(), mThumb)
            setImageDrawable(mThumbs!![1])
        } else {
            mThumbs!![0] = mThumbs!![1]
            mThumbs!![1] = BitmapDrawable(getContext().getResources(), mThumb)
            mThumbTransition = TransitionDrawable(mThumbs)
            setImageDrawable(mThumbTransition)
            mThumbTransition!!.startTransition(500)
        }
        setVisibility(VISIBLE)
    }

    companion object {
        private const val TAG = "RotateImageView"
        private const val ANIMATION_SPEED = 270 // 270 deg/sec
    }
}
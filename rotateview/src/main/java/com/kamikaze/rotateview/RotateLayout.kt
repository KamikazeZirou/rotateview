/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

// A RotateLayout is designed to display a single item and provides the
// capabilities to rotate the item.
class RotateLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    var orientation = 0
        private set
    private var child: View? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        child = getChildAt(0)
        child!!.pivotX = 0f
        child!!.pivotY = 0f
    }

    override fun onLayout(
        change: Boolean, left: Int, top: Int, right: Int, bottom: Int
    ) {
        val width = right - left
        val height = bottom - top
        when (orientation) {
            0, 180 -> child!!.layout(0, 0, width, height)
            90, 270 -> child!!.layout(0, 0, height, width)
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var w = 0
        var h = 0
        when (orientation) {
            0, 180 -> {
                measureChild(child, widthSpec, heightSpec)
                w = child!!.measuredWidth
                h = child!!.measuredHeight
            }
            90, 270 -> {
                measureChild(child, heightSpec, widthSpec)
                w = child!!.measuredHeight
                h = child!!.measuredWidth
            }
        }
        setMeasuredDimension(w, h)
        when (orientation) {
            0 -> {
                child!!.translationX = 0f
                child!!.translationY = 0f
            }
            90 -> {
                child!!.translationX = 0f
                child!!.translationY = h.toFloat()
            }
            180 -> {
                child!!.translationX = w.toFloat()
                child!!.translationY = h.toFloat()
            }
            270 -> {
                child!!.translationX = w.toFloat()
                child!!.translationY = 0f
            }
        }
        child!!.rotation = -orientation.toFloat()
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    // Rotate the view counter-clockwise
    fun setOrientation(orientation: Int) {
        val orientation = orientation
            .makeSureInRegularRange()
            .round()

        if (this.orientation == orientation) return
        this.orientation = orientation
        requestLayout()
    }

    init {
        // The transparent background here is a workaround of the render issue
        // happened when the view is rotated as the device's orientation
        // changed. The view looks fine in landscape. After rotation, the view
        // is invisible.
        setBackgroundResource(R.color.transparent)
    }
}
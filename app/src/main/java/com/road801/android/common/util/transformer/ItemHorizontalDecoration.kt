package com.road801.android.common.util.transformer

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class ItemHorizontalDecoration(@DimenRes private val padding: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemCount = state.itemCount
        val position = parent.getChildAdapterPosition(view)

        val top = 0
        val bottom = 0
        when (position) {
            0 -> {
                outRect.left = padding*2
                outRect.right = padding/2
            }
            itemCount-1 -> {
                outRect.left = padding/2
                outRect.right = padding*2
            }
            else -> {
                outRect.left = padding/2
                outRect.right = padding/2
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

    }
}
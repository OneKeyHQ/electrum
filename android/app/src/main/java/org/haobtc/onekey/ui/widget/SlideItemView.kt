package org.haobtc.onekey.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import com.orhanobut.logger.Logger

class SlideItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HorizontalScrollView(context, attrs, defStyleAttr) {
  private var mFunctionViewWidth = 0
  private var mSlideLimit = 0
  private var mIsExpansion = false
  private var mOnSlideListener: OnSlideListener? = null
  private fun init() {
    this.isFillViewport = true
    this.isHorizontalScrollBarEnabled = false
    this.overScrollMode = View.OVER_SCROLL_NEVER
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    super.onLayout(changed, l, t, r, b)
    val childCount = childCount
    if (childCount == 0) {
      return
    }
    // The SlideItem can only have one child view of type ViewGroup.
    var rootView: ViewGroup? = null
    for (i in 0 until childCount) {
      val view: View = getChildAt(i)
      if (view is ViewGroup) {
        rootView = view
        break
      }
    }
    if (rootView == null) {
      return
    }
    val rootLayoutChildCount = rootView.childCount
    if (rootLayoutChildCount < 2) {
      return
    }
    val contentView: View = rootView.getChildAt(0)
    val functionView: View = rootView.getChildAt(1)
    val width = measuredWidth
    mFunctionViewWidth = functionView.measuredWidth
    if (mSlideLimit == 0) {
      mSlideLimit = mFunctionViewWidth / 2
    }

    // SlideItem（ViewGroup（ViewGroup ViewGroup））
    rootView.layout(rootView.left, rootView.top,
        rootView.left + width + mFunctionViewWidth, rootView.bottom)
    contentView.layout(contentView.left, contentView.top,
        contentView.left + width, contentView.bottom)
    functionView.layout(contentView.left + width, contentView.top,
        contentView.left + width + mFunctionViewWidth, contentView.bottom)

    // Set content area width
    val layoutParams: ViewGroup.LayoutParams = contentView.layoutParams
    layoutParams.width = width
    contentView.layoutParams = layoutParams
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent): Boolean {
    when (ev.action) {
      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        if (mIsExpansion) {
          // slide to right
          if (scrollX < mFunctionViewWidth - mSlideLimit) {
            mOnSlideListener?.onSlideClose()
            mIsExpansion = false
            smoothScrollTo(0, 0)
          } else {
            smoothScrollTo(mFunctionViewWidth, 0)
          }
        } else {
          // slide to left
          if (scrollX > mSlideLimit) {
            mIsExpansion = true
            mOnSlideListener?.onSlideOpen()
            smoothScrollTo(mFunctionViewWidth, 0)
          } else {
            smoothScrollTo(0, 0)
          }
        }
        return true
      }
    }
    return super.onTouchEvent(ev)
  }

  /**
   * Set slide range.
   *
   * @param slideLimit 0~mFunctionViewWidth
   */
  fun setSlideLimit(slideLimit: Int) {
    mSlideLimit = slideLimit
  }

  /**
   * Smooth scroll to (0, 0)
   */
  fun reset() {
    smoothScrollTo(0, 0)
    mIsExpansion = false
    mOnSlideListener?.onSlideClose()
  }

  fun setSlideWidth(width: Int) {
    try {
      (getChildAt(0) as ViewGroup).getChildAt(1).apply {
        val params = layoutParams
        params.width = width
        layoutParams = params
      }
    } catch (e: Exception) {
    }
  }

  fun open() {
    smoothScrollTo(mFunctionViewWidth, 0)
  }

  fun setOnSlideListener(listener: OnSlideListener) {
    mOnSlideListener = listener
  }

  fun isExpansion() = mIsExpansion

  init {
    init()
  }

  interface OnSlideListener {
    fun onSlideOpen()
    fun onSlideClose()
  }
}

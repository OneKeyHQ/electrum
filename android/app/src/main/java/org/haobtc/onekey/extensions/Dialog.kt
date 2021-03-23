package org.haobtc.onekey.extensions

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi


@RequiresApi(api = Build.VERSION_CODES.M)
@JvmOverloads
fun Dialog.setCustomNavigationBar(@ColorInt colorInt: Int = Color.WHITE) {
  val window: Window? = this.window
  if (window != null) {
    val metrics = DisplayMetrics()
    window.windowManager.defaultDisplay.getMetrics(metrics)
    val dimDrawable = GradientDrawable()
    val navigationBarDrawable = GradientDrawable()
    navigationBarDrawable.shape = GradientDrawable.RECTANGLE
    navigationBarDrawable.setColor(colorInt)
    val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
    val windowBackground = LayerDrawable(layers)
    // The separator
    // windowBackground.setLayerInsetTop(1, metrics.heightPixels)
    window.setBackgroundDrawable(windowBackground)
  }
}

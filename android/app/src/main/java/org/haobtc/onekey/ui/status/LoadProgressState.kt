package org.haobtc.onekey.ui.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer
import org.haobtc.onekey.R

class LoadProgressState : MultiState() {
  private lateinit var mAnimation: Animation

  override fun onCreateMultiStateView(context: Context, inflater: LayoutInflater, container: MultiStateContainer): View {
    mAnimation = AnimationUtils.loadAnimation(context, R.anim.dialog_progress_anim)
    return inflater.inflate(R.layout.state_load_progress, container, false)
  }

  override fun onMultiStateViewCreate(view: View) {
    view.findViewById<View>(R.id.ivProgress).startAnimation(mAnimation)
  }
}

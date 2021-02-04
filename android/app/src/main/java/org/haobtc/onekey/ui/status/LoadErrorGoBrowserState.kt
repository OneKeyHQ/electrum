package org.haobtc.onekey.ui.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer
import org.haobtc.onekey.R

class LoadErrorGoBrowserState : MultiState() {
  private var mOnCLickListener: View.OnClickListener? = null

  override fun onCreateMultiStateView(context: Context, inflater: LayoutInflater, container: MultiStateContainer): View {
    return inflater.inflate(R.layout.state_load_error_go_to_browser, container, false)
  }

  override fun onMultiStateViewCreate(view: View) {
    view.findViewById<View>(R.id.tv_go_to_browser).setOnClickListener {
      mOnCLickListener?.onClick(it)
    }
  }

  fun setOnGotoBrowserListener(listener: View.OnClickListener) {
    mOnCLickListener = listener
  }

}

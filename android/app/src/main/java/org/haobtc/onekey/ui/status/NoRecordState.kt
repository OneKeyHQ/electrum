package org.haobtc.onekey.ui.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer
import org.haobtc.onekey.R

class NoRecordState : MultiState() {
  private lateinit var tvRefresh: View;
  override fun onCreateMultiStateView(context: Context, inflater: LayoutInflater, container: MultiStateContainer): View {
    return inflater.inflate(R.layout.state_no_transfer_record, container, false)
  }

  override fun onMultiStateViewCreate(view: View) {
    tvRefresh = view.findViewById(R.id.tv_refresh)
  }

  override fun enableReload() = true

  override fun bindRetryView(): View {
    return tvRefresh
  }
}

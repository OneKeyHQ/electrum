package org.haobtc.onekey.business.wallet

import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.manager.PreferencesManager

class DappManager {
  private val context by lazy {
    MyApplication.getInstance()
  }

  fun userDapp(name: String) {
    PreferencesManager.put(
        context, "use_dapp", name, 1)
  }

  fun firstUse(name: String): Boolean {
    return (PreferencesManager.get(
        context, "use_dapp", name, -1) == -1)

  }
}

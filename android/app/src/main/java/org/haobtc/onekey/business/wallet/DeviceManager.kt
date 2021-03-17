package org.haobtc.onekey.business.wallet

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentActivity
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.google.common.base.Strings
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.HardwareFeatures
import org.haobtc.onekey.constant.Constant
import org.haobtc.onekey.manager.BleManager
import org.haobtc.onekey.manager.PreferencesManager
import org.haobtc.onekey.utils.Utils

/**
 * 硬件设备管理类
 *
 * @author Onekey@QuincySx
 * @create 2021-03-01 2:06 PM
 */
class DeviceManager private constructor(private val context: Context) {

  /**
   * 根据设备 ID 获取设备信息
   *
   * @param deviceId 设备 ID
   *
   * @return 设备信息
   */
  fun getDeviceInfo(deviceId: String): HardwareFeatures? {
    val deviceInfo = PreferencesManager.get(
        context, Constant.DEVICES, deviceId, "")
        .toString()
    return if (!Strings.isNullOrEmpty(deviceInfo)) {
      HardwareFeatures.objectFromData(deviceInfo)
    } else {
      null
    }
  }

  /**
   * 根据设备名称获取设备蓝牙 MacAddress
   *
   * @param bleName 设备名称
   *
   * @return 设备蓝牙的 MacAddress
   */
  fun getDeviceBleMacAddress(bleName: String): String? {
    val macAddress = PreferencesManager.get(
        context, Constant.BLE_INFO, bleName, "")
    return if (macAddress is String && macAddress.isNotEmpty()) {
      macAddress
    } else {
      null
    }
  }

  /**
   * 根据设备 ID 地址取消连接
   * @param deviceId 设备 ID
   */
  fun cancelDevice(deviceId: String) {
    val deviceInfo = getDeviceInfo(deviceId)
    if (deviceInfo == null) {
      return
    }
    val deviceBleMacAddress = getDeviceBleMacAddress(deviceInfo.bleName)
    if (deviceBleMacAddress == null) {
      return
    }
    cancelDeviceByMacAddress(deviceBleMacAddress)
  }

  /**
   * 根据设备 Mac 地址取消连接
   * @param deviceBleMacAddress  设备 Mac 地
   */
  fun cancelDeviceByMacAddress(deviceBleMacAddress: String) {
    val activity = Utils.getTopActivity()
    if (activity == null || activity !is FragmentActivity) {
      return
    }

    BleManager.getInstance(activity).cancelConnect(deviceBleMacAddress)
  }

  /**
   * 根据设备 Mac 地址连接设备
   * @param deviceBleMacAddress  设备 Mac 地
   * @param callback  连接回调
   */
  fun connectDeviceByMacAddress(deviceBleMacAddress: String, callback: OnConnectDeviceListener<BleDevice>) {
    val activity = Utils.getTopActivity()
    if (activity == null || activity !is FragmentActivity) {
      callback.onException(null, DeviceException.OnConnectError())
      return
    }
    val bleManager = BleManager.getInstance(activity)
    bleManager.connDevByMac(deviceBleMacAddress, object : OnekeyBleConnectCallback() {
      override fun onSuccess(device: BleDevice?) {
        if (device != null) {
          callback.onSuccess(device)
        } else {
          callback.onException(device, DeviceException.OnConnectError())
        }
      }

      override fun onConnectCancel(device: BleDevice?) {
        super.onConnectCancel(device)
        callback.onException(device, DeviceException.OnCancelError())
      }

      override fun onConnectTimeOut(device: BleDevice?) {
        super.onConnectTimeOut(device)
        callback.onException(device, DeviceException.OnTimeoutError())
      }

      override fun onConnectException(device: BleDevice?, errorCode: Int) {
        super.onConnectException(device, errorCode)
        callback.onException(device, DeviceException.OnConnectError(errorCode.toString()))
      }
    })
  }

  /**
   * 根据设备 ID 连接设备
   * @param deviceId 设备 ID
   * @param callback  连接回调
   */
  fun connectDeviceByDeviceId(deviceId: String, callback: OnConnectDeviceListener<BleDevice>) {
    val deviceInfo = getDeviceInfo(deviceId)
    if (deviceInfo == null) {
      callback.onException(null, DeviceException.OnConnectError())
      return
    }
    val deviceBleMacAddress = getDeviceBleMacAddress(deviceInfo.bleName)
    if (deviceBleMacAddress == null) {
      callback.onException(null, DeviceException.OnConnectError())
      return
    }
    connectDeviceByMacAddress(deviceBleMacAddress, callback)
  }

  interface OnConnectDeviceListener<T> {
    fun onSuccess(t: T)
    fun onException(t: T?, e: Exception)
  }

  companion object {
    @SuppressLint("StaticFieldLeak")
    private var instance: DeviceManager? = null

    @JvmStatic
    @JvmOverloads
    fun getInstance(context: Context = MyApplication.getInstance()): DeviceManager {
      return instance ?: synchronized(DeviceManager::class.java) {
        instance ?: DeviceManager(context.applicationContext).also {
          instance = it
        }
      }
    }

    @JvmStatic
    fun forceUpdate(features: HardwareFeatures): Boolean {
      if (Strings.isNullOrEmpty(features.getSerialNum())
          || features.isBootloaderMode()) {
        return true
      }
      return false
    }
  }
}

abstract class OnekeyBleConnectCallback : BleConnectCallback<BleDevice>() {
  abstract fun onSuccess(device: BleDevice?)
}

open class DeviceException(message: String? = null) : RuntimeException(message) {
  class OnCancelError : DeviceException()
  class OnTimeoutError : DeviceException()
  class OnConnectError(message: String? = null) : DeviceException(message)
}

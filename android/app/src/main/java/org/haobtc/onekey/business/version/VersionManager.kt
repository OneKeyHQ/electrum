package org.haobtc.onekey.business.version

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.haobtc.onekey.BuildConfig
import org.haobtc.onekey.bean.UpdateInfo
import org.haobtc.onekey.constant.Constant
import org.haobtc.onekey.manager.PreferencesManager
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch

class VersionManager {
  companion object {
    /**
     * 获取版本信息需要请求的 URL
     */
    @JvmStatic
    private fun getVersionDataUrl(): String {
      // version_testnet.json version_regtest.json
      val appId = BuildConfig.APPLICATION_ID
      return Constant.ONE_KEY_WEBSITE + when {
        appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_0) -> {
          "version.json"
        }
        appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_2) -> {
          "version_testnet.json"
        }
        appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_1) -> {
          "version_regtest.json"
        }
        else -> {
          "version.json"
        }
      }
    }
  }

  /**
   * 在服务器获取软件、硬件的版本信息
   * @param success 获取成功的回调方法
   * @param error 获取失败的回调方法，可不传
   */
  @JvmOverloads
  fun getVersionData(success: (UpdateInfo) -> Unit, error: ((Exception) -> Unit)? = null) {
    Single
        .create<UpdateInfo> {
          val url = getVersionDataUrl()
          val request: Request = Request.Builder().url(url).build()
          val response = OkHttpClient().newCall(request).execute()
          if (response.code() != HttpURLConnection.HTTP_OK) {
            it.onError(Exception(response.body()?.toString() ?: ""))
          } else {
            val info = response.body()?.string() ?: ""
            try {
              val updateInfo = UpdateInfo.objectFromData(info)
              it.onSuccess(updateInfo)
            } catch (e: Exception) {
              e.printStackTrace()
              if (e is JsonSyntaxException) {
                Log.e("Main", "获取到的更新信息格式错误")
              }
              it.onError(e)
            }
          }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : SingleObserver<UpdateInfo> {
          override fun onSubscribe(d: Disposable?) {
          }

          override fun onSuccess(t: UpdateInfo) {
            success.invoke(t)
          }

          override fun onError(e: Throwable?) {
            e?.printStackTrace()
            Logger.e("获取更新信息失败")
            error?.invoke(Exception(e))
          }
        })
  }

  /**
   * 在服务器获取软件、硬件的版本信息
   * @return 版本信息，获取不到返回空。
   */
  @WorkerThread
  fun getVersionData(): UpdateInfo? {
    val countDownLatch = CountDownLatch(1)
    var info: UpdateInfo? = null
    getVersionData(success = {
      info = it
      countDownLatch.countDown()
    }, error = {
      countDownLatch.countDown()
    })
    countDownLatch.await()
    return info
  }

  /**
   * 获取本地版本信息，如果本地不存在联网获取软件信息。
   * @return 版本信息，获取不到则请求网络，网络请求失败返回空。
   */
  fun getForceLocalVersionInfo(context: Context): UpdateInfo? {
    return getLocalVersionInfo(context) ?: getVersionData()
  }

  /**
   * 获取本地版本信息。
   * @return 版本信息，本地不存在返回空。
   */
  fun getLocalVersionInfo(context: Context): UpdateInfo? {
    val oldInfo = PreferencesManager
        .get(context, "Preferences", Constant.UPGRADE_INFO, "").toString()
    return try {
      UpdateInfo.objectFromData(oldInfo)
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * 将版本信息存储本地
   */
  fun saveVersionInfo(context: Context, info: UpdateInfo) {
    getLocalVersionInfo(context)?.apply {
      if (stm32.url != info.stm32?.url) {
        info.stm32?.isNeedUpload = true
      }
      if (nrf.url != info.nrf?.url) {
        info.nrf?.isNeedUpload = true
      }
    }
    PreferencesManager
        .put(context, "Preferences", Constant.UPGRADE_INFO, info.toString())
  }
}

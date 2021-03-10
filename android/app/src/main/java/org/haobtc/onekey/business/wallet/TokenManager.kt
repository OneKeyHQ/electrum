package org.haobtc.onekey.business.wallet

import android.util.Log
import androidx.annotation.WorkerThread
import com.alibaba.fastjson.JSON
import com.google.common.base.Strings
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.TokenList.ERCToken
import org.haobtc.onekey.manager.PyEnv
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.HashMap
import kotlin.concurrent.read
import kotlin.concurrent.write

/** @Description: java类作用描述 @Author: peter Qin
 */
class TokenManager {
  private val mReferenceQueue = ReferenceQueue<ERCToken>()

  private val mTokenSoftReferenceMap = HashMap<String, SoftReference<ERCToken>>(1100)


  private fun cleanCache() = mReadWriteLock.write {
    var se = mReferenceQueue.poll()
    while (se != null) {
      se.get()?.address?.toLowerCase(Locale.ROOT)?.let {
        mTokenSoftReferenceMap.remove(it)
      }
      se = mReferenceQueue.poll()
    }
  }

  /**
   * 如果没网，就用本地文件存储的 TokenList 展示 如果能从服务器拿到数据，判断是否需要更新本地文件
   *
   * @param json
   */
  @WorkerThread
  fun uploadLocalTokenList(json: String?): Boolean {
    var writer: FileWriter? = null
    var isSuccess: Boolean
    try {
      val file = File(FILE_PATH)
      if (file.exists()) {
        file.delete()
      }
      file.createNewFile()
      writer = FileWriter(file)
      if (!Strings.isNullOrEmpty(json)) {
        writer.write(json)
      }
      writer.flush()
      isSuccess = true
    } catch (e: Exception) {
      isSuccess = false
    } finally {
      if (null != writer) {
        try {
          writer.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
    return isSuccess
  }

  @WorkerThread
  fun initFile() {
    Log.e("TokenManager", "init file")
    mReadWriteLock.write {
      val response = PyEnv.getAllTokenInfo()
      uploadLocalTokenList(response.result)

      JSON.parseArray(response.result, ERCToken::class.java).forEach {

        mTokenSoftReferenceMap[it.address.toLowerCase(Locale.ROOT)] = SoftReference(it, mReferenceQueue)
      }
      Log.e("TokenManager", "init file done")
    }
  }

  @get:WorkerThread
  private val localTokenList: String?
    private get() {
      try {
        val bfr = BufferedReader(FileReader(FILE_PATH))
        var line = bfr.readLine()
        val builder = StringBuilder()
        while (line != null) {
          builder.append(line)
          builder.append("\n")
          line = bfr.readLine()
        }
        bfr.close()
        return builder.toString()
      } catch (e: IOException) {
        e.printStackTrace()
      }
      return null
    }

  val tokenList: List<ERCToken>
    get() = mReadWriteLock.read {
      JSON.parseArray(localTokenList, ERCToken::class.java)
    }// 拼接字符串

  /**
   * 通过 address 获取到 ERCToken 实体类
   *
   * @param address token 地址
   * @return
   */
  fun getTokenByAddress(address: String?): ERCToken? = mReadWriteLock.read {
    val softReference = mTokenSoftReferenceMap[address?.toLowerCase(Locale.ROOT)]
    var customERCToken: ERCToken? = null
    if (softReference?.get() == null) {
      customERCToken = getCustomTokenByAddress(address)
      if (customERCToken == null) {
        cleanCache()
        initFile()
      }
    }
    return mTokenSoftReferenceMap[address?.toLowerCase(Locale.ROOT)]?.get() ?: customERCToken
  }

  private fun getCustomTokenList(): MutableList<ERCToken> {
    return PyEnv.getTokenList().result ?: arrayListOf()
  }

  private fun getCustomTokenByAddress(address: String?): ERCToken? = mReadWriteLock.write {
    getCustomTokenList().forEach {
      if (it.address.equals(address, true)) {
        return it
      }
    }
    return null
  }

  companion object {
    //    private static final String FILE_PATH = "eth_token_list.json";
    private val FILE_PATH = (MyApplication.getInstance().filesDir.absolutePath
        + "/"
        + "eth_token_list.json")

    // 读写锁
    private val mReadWriteLock = ReentrantReadWriteLock()
  }
}

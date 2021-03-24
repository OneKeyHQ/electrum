package org.haobtc.onekey.utils

import android.util.Patterns
import android.webkit.URLUtil
import com.orhanobut.logger.Logger
import java.net.URI
import java.util.*


object URLUtils {
  private const val GOOGLE_SEARCH_PREFIX = "https://www.google.com/search?q="
  private const val HTTPS_PREFIX = "https://"
  private const val HTTP_PREFIX = "http://"

  fun formatUrl(url: String?): String {
    return if (url != null && (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url) || URLUtil.isAssetUrl(url))) {
      url
    } else {
      if (url != null && isValidUrl(url)) {
        HTTPS_PREFIX + url
      } else {
        url ?: "$GOOGLE_SEARCH_PREFIX$url"
      }
    }
  }

  fun removeHTTPProtocol(url: String?): String {
    return if (url != null && URLUtil.isHttpsUrl(url)) {
      url.replace(HTTPS_PREFIX, "")
    } else if (url != null && URLUtil.isHttpUrl(url)) {
      url.replace(HTTP_PREFIX, "")
    } else {
      url ?: ""
    }
  }

  fun isValidUrl(url: String): Boolean {
    val p = Patterns.WEB_URL
    val m = p.matcher(url.toLowerCase(Locale.ROOT))
    return m.matches()
  }

  fun getWebFavicon(url: String?): String {
    return "${getDomainName(url)}/favicon.ico"
  }

  fun getDomainName(url: String?): String {
    val formatUrl = formatUrl(url)
    try {
      val uri = URI(formatUrl)
      return "${uri.scheme}://${uri.host}"
    } catch (e: Exception) {
      return url ?: "$GOOGLE_SEARCH_PREFIX$url"
    }
  }
}

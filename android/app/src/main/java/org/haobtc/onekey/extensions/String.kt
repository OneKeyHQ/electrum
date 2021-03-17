package org.haobtc.onekey.extensions

const val SHOW_DECIMAL_PLACES = 8

/**
 * 裁切多余位数的小数
 * @param decimal 保留的小数位数
 */
@JvmOverloads
fun String.interceptDecimal(decimal: Int = SHOW_DECIMAL_PLACES): String {
  val amountNumber: Int = this.lastIndexOf('.')
  if (amountNumber == -1) {
    return this
  }
  val amountDecimal: Int = this.length - amountNumber - 1
  return if (amountDecimal > decimal) {
    this.substring(0, amountNumber + decimal + 1)
  } else {
    this
  }
}

/**
 * 截取字符串后 N 位
 */
@JvmOverloads
fun String.cutTheLast(digits: Int = 4): String {
  if (this.length > digits) {
    return this.substring(this.length - digits)
  } else {
    return this
  }
}

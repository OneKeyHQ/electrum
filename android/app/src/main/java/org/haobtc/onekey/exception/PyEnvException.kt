package org.haobtc.onekey.exception

import com.orhanobut.logger.Logger


open class PyEnvException : RuntimeException {
  constructor() : super()
  constructor(e: Exception) : super(e)
  constructor(e: Throwable) : super(e)
  constructor(message: String) : super(message)

  class BleResponseReadTimeoutException() : PyEnvException()
  class FileFormatErrorException() : PyEnvException()
  class PassphraseOperationTimeoutException() : PyEnvException()
  class PinOperationTimeoutException() : PyEnvException()
  class TransactionFormatException() : PyEnvException()
  class UnPairableException() : PyEnvException()
  class UserCancelException() : PyEnvException()
  class WalletAlreadyExistException() : PyEnvException()
  class UpdateFailedException() : PyEnvException()
  class OperationCancelException() : PyEnvException()
  class PairUserCancelException() : PyEnvException()
  class SoftwareException(message: String) : RuntimeException(message)

  companion object {
    @JvmStatic
    fun convert(e: Throwable): RuntimeException {
      val messagePair = getMessage(e.message)
      return when {
        "read ble response timeout".equals(messagePair.first, true) -> BleResponseReadTimeoutException()
        "File is not a zip file".equals(messagePair.first, true) -> FileFormatErrorException()
        "waiting passphrase timeout".equals(messagePair.first, true) -> PassphraseOperationTimeoutException()
        "waiting pin timeout".equals(messagePair.first, true) -> PinOperationTimeoutException()
        "failed to recognize transaction encoding for txt: craft fury pig target diagram ...".equals(messagePair.second, true) -> TransactionFormatException()
        "Can't Pair With You Device".equals(messagePair.first, true) -> UnPairableException()
        "user cancel".equals(messagePair.first, true) -> UserCancelException()
        "file already exists at path".equals(messagePair.first, true) -> WalletAlreadyExistException()
        "The same xpubs have create wallet".equals(messagePair.first, true) -> WalletAlreadyExistException()
        "Update failed: FirmwareError".equals(messagePair.first, true) -> UpdateFailedException()
        "Operation cancelled".equals(messagePair.first, true) -> OperationCancelException()
        messagePair.second.contains("UserCancel", true) -> PairUserCancelException()
        else -> {
          Logger.d("PyEnv 错误信息转换:" + e.message)
          SoftwareException(HardWareExceptions.getExceptionString(e))
        }
      }
    }

    private fun getMessage(message: String?): Pair<String, String> {
      if (message?.contains(":") == true) {
        val split = message.split(":")
        return Pair(
            split.getOrElse(1) { "" }.trim(),
            split.getOrElse(0) { "" }.trim())
      } else {
        return Pair(message?.trim() ?: "", "")
      }
    }
  }
}

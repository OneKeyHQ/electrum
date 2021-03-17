package org.haobtc.onekey.exception

import androidx.annotation.StringRes
import com.orhanobut.logger.Logger
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication

private fun getString(@StringRes id: Int) =
    MyApplication.getInstance().getString(id)

open class PyEnvException : RuntimeException {
  constructor() : super()
  constructor(e: Exception) : super(e)
  constructor(e: Throwable) : super(e)
  constructor(message: String) : super(message)
  constructor(@StringRes id: Int) : super(getString(id))


  class BleResponseReadTimeoutException() : PyEnvException(R.string.bluetooth_exception)
  class FileFormatErrorException() : PyEnvException()
  class PassphraseOperationTimeoutException() : PyEnvException(R.string.passphrase_timeout)
  class PinOperationTimeoutException() : PyEnvException(R.string.pin_timeout)
  class TransactionFormatException() : PyEnvException(R.string.transaction_parse_error)
  class UnPairableException() : PyEnvException(R.string.not_found_device_msg)
  class UserCancelException() : PyEnvException(R.string.hint_error_user_cancelled)
  class WalletAlreadyExistException() : PyEnvException()
  class UpdateFailedException() : PyEnvException(R.string.update_failed)
  class OperationCancelException() : PyEnvException(R.string.hint_error_operation_cancelled)
  class PairUserCancelException() : PyEnvException(R.string.hint_error_user_cancelled)
  class LackOfBalanceException() : PyEnvException(R.string.balance_zero)
  class ForcedHardwareUpgradeException() : PyEnvException(R.string.hint_forced_hardware_upgrade)
  class SoftwareException(message: String) : RuntimeException(message)

  companion object {
    @JvmStatic
    fun convert(e: Throwable): RuntimeException {
      if (e is PyEnvException) {
        return e
      }
      val messagePair = getMessage(e.message)
      return when {
        "read ble response timeout".equals(messagePair.first, true) -> BleResponseReadTimeoutException()
        "File is not a zip file".equals(messagePair.first, true) -> FileFormatErrorException()
        "waiting passphrase timeout".equals(messagePair.first, true) -> PassphraseOperationTimeoutException()
        "waiting pin timeout".equals(messagePair.first, true) -> PinOperationTimeoutException()
        "failed to recognize transaction encoding for txt: craft fury pig target diagram ...".equals(messagePair.second, true) -> TransactionFormatException()
        "Can't Pair With You Device".equals(messagePair.first, true) -> UnPairableException()
        "user cancel".equals(messagePair.first, true) -> UserCancelException()
        "UserCancelled:".equals(messagePair.first, true) -> UserCancelException()
        "file already exists at path".equals(messagePair.first, true) -> WalletAlreadyExistException()
        "The same xpubs have create wallet".equals(messagePair.first, true) -> WalletAlreadyExistException()
        "Update failed: FirmwareError".equals(messagePair.first, true) -> UpdateFailedException()
        "Operation cancelled".equals(messagePair.first, true) -> OperationCancelException()
        "InsufficientFundsException".equals(messagePair.second, true) -> LackOfBalanceException()
        messagePair.second.contains("UserCancel", true) -> PairUserCancelException()
        else -> {
          Logger.d("PyEnv 错误信息转换:" + e.message +"   first:${messagePair.first}   second:${messagePair.second}")
          SoftwareException(messagePair.first)
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

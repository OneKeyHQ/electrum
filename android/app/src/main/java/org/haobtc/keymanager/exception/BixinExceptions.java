package org.haobtc.keymanager.exception;

//
// Created by liyan on 2020/5/24.
//
public enum BixinExceptions {

   BLE_RESPONSE_READ_TIMEOUT("BaseException: read ble response timeout", 12),
   FILE_FORMAT_ERROR("BaseException: File is not a zip file", 13),
   PASSPHRASE_OPERATION_TIMEOUT("BaseException: waiting passphrase timeout", 11),
   PIN_INVALID("BaseException: (7, 'PIN invalid')", 7),
   PIN_OPERATION_TIMEOUT("BaseException: waiting pin timeout", 10),
   TRANSACTION_FORMAT_ERROR("BaseException: failed to recognize transaction encoding for txt: craft fury pig target diagram ...", 9),
   UN_PAIRABLE("BaseException: Can't Pair With You Device", 8),
   USER_CANCEL("BaseException: user cancel", 14);


   private final String message;
   private final int code;

   BixinExceptions(String message, int code) {
      this.message = message;
      this.code = code;
   }

   public int getCode() {
      return code;
   }

   public String getMessage() {
      return message;
   }
}

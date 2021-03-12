package org.haobtc.onekey.exception;

import java.util.Objects;

/** @author liyan */
public enum HardWareExceptions {
    BLE_RESPONSE_READ_TIMEOUT("BaseException: read ble response timeout", 12),
    FILE_FORMAT_ERROR("BaseException: File is not a zip file", 13),
    PASSPHRASE_OPERATION_TIMEOUT("BaseException: waiting passphrase timeout", 11),
    PIN_INVALID("BaseException: (7, 'PIN invalid')", 7),
    PIN_OPERATION_TIMEOUT("BaseException: waiting pin timeout", 10),
    TRANSACTION_FORMAT_ERROR(
            "BaseException: failed to recognize transaction encoding for txt: craft fury pig target diagram ...",
            9),
    UN_PAIRABLE("BaseException: Can't Pair With You Device", 8),
    USER_CANCEL("BaseException: user cancel", 14),
    WALLET_ALREADY_EXIST("BaseException: file already exists at path", 15),
    WALLET_ALREADY_EXIST_1("The same xpubs have create wallet", 16),
    UPDATE_FAILED("BaseException: Update failed: FirmwareError", 17),
    PAIR_USER_CANCEL("UserCancel: ", 18),
    OPERATION_CANCEL("Operation cancelled", 19);
    private final String message;
    private final int code;
    public static final String BASE_EXCEPTION_PREFIX = ":";

    HardWareExceptions(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 统一 replace "BaseException: "，后端后续返回可能不是这个格式
     *
     * @param e 异常
     * @return
     */
    public static Exception exceptionConvert(Exception e) {
        if (Objects.requireNonNull(e.getMessage()).contains(BASE_EXCEPTION_PREFIX)) {
            return new Exception(
                    e.getMessage().substring(e.getMessage().indexOf(BASE_EXCEPTION_PREFIX) + 1));
        }
        return e;
    }

    public static String getExceptionString(Throwable e) {
        if (Objects.requireNonNull(e.getMessage()).contains(BASE_EXCEPTION_PREFIX)) {
            return e.getMessage().substring(e.getMessage().indexOf(BASE_EXCEPTION_PREFIX) + 1);
        }
        return "";
    }

    public static String getThrowString(Throwable throwable) {
        if (throwable.getMessage().contains(BASE_EXCEPTION_PREFIX)) {
            return throwable
                    .getMessage()
                    .substring(throwable.getMessage().indexOf(BASE_EXCEPTION_PREFIX) + 1);
        }
        return "";
    }
}

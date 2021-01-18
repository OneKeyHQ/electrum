package org.haobtc.onekey.exception;

/**
 * 账户相关报错
 *
 * @author Onekey@QuincySx
 * @create 2021-01-18 11:44 AM
 */
public class AccountException extends Exception {
    public final String message;

    public AccountException(String message) {
        super(message);
        this.message = message;
    }

    public static class CreateException extends AccountException {
        public CreateException(String message) {
            super(message);
        }
    }

    public static class DeriveException extends AccountException {
        public DeriveException(String message) {
            super(message);
        }
    }
}

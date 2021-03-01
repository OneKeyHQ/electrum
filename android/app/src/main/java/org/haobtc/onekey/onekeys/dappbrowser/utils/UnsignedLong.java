package org.haobtc.onekey.onekeys.dappbrowser.utils;

import java.math.BigInteger;

/**
 * @author Onekey@QuincySx
 * @create 2021-03-05 11:03 PM
 */
public class UnsignedLong extends BigInteger {

    public UnsignedLong(byte[] byteValue) {
        super(1, byteValue);
    }

    public static UnsignedLong create(long value) {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] = (byte) getByteVal(value, 3 - i);
        }
        return new UnsignedLong(byteVal);
    }

    public static UnsignedLong create(BigInteger value) {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] =
                    value.divide(BigInteger.valueOf(1 << (3 - i) * 8))
                            .byteValue(); //  (value, 3 - i);
        }
        return new UnsignedLong(byteVal);
    }

    public static byte[] createBytes(long value) {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] = (byte) getByteVal(value, 3 - i);
        }

        return byteVal;
    }

    // select the value 0-255 from each byte of the long
    private static int getByteVal(long value, int p) {
        return ((int) ((byte) ((value >> (p * 8)) & 0xFF)));
    }
}

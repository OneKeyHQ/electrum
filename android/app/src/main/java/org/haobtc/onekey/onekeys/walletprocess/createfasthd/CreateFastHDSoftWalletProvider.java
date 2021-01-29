package org.haobtc.onekey.onekeys.walletprocess.createfasthd;

public interface CreateFastHDSoftWalletProvider {

    /**
     * 是否是导入
     *
     * @return true 导入
     */
    boolean isImport();

    /**
     * 临时限制
     *
     * @return true:支持 ETH
     */
    boolean supportETH();
}

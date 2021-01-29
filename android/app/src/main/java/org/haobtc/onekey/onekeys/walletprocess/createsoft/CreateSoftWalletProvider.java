package org.haobtc.onekey.onekeys.walletprocess.createsoft;

public interface CreateSoftWalletProvider {
    /**
     * 是否存在 HD 钱包
     *
     * @return true 存在
     */
    boolean existsHDWallet();

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

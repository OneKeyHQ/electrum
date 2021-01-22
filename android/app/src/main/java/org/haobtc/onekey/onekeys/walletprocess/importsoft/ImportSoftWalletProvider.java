package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import org.haobtc.onekey.constant.Vm;

/**
 * @author Onekey@QuincySx
 * @create 2021-01-16 9:13 PM
 */
public interface ImportSoftWalletProvider {
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
     * 获取当前选择钱包币种类型
     *
     * @return 币种类型
     */
    Vm.CoinType currentCoinType();

    /**
     * 导入的是否是 HD 钱包
     *
     * @return true 导入 HD 钱包
     */
    boolean isImportHDWallet();
}

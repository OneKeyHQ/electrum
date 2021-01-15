package org.haobtc.onekey.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 钱包信息的数据模型
 * @Author: peter Qin
 * @CreateDate: 2021/1/14 6:13 PM
 */
public class WalletAsset {
    public List<WalletInfo> wallets = new ArrayList<>();
    public int validShowNum;
}

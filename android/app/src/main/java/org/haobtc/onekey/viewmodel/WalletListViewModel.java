package org.haobtc.onekey.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.common.base.Strings;
import com.orhanobut.logger.Logger;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.adapter.WalletListTypeAdapter;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.WalletAsset;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 钱包列表页面的ViewModel
 * @Author: peter Qin
 */
public class WalletListViewModel extends AndroidViewModel {
    public MutableLiveData<WalletAsset> mAllWallets = new MutableLiveData<>();
    public MutableLiveData<List<WalletInfo>> mBtcWallets = new MutableLiveData<>();
    public MutableLiveData<List<WalletInfo>> mEthWallets = new MutableLiveData<>();

    public WalletListViewModel(@NonNull Application application) {
        super(application);
        getAllWallets("");
        getBtcWallets(Constant.BTC);
        getEthWallets(Constant.ETH);
    }

    private void getEthWallets(String eth) {
        getAllWallets(eth);
    }

    private void getBtcWallets(String btc) {
        getAllWallets(btc);
    }

    /**
     * 获取本地所有钱包
     */
    public void getAllWallets(String type) {
        PyResponse<List<WalletInfo>> response = PyEnv.loadWalletByType(type);
        if (Strings.isNullOrEmpty(response.getErrors())) {
            List<WalletInfo> list = response.getResult();
            if (Strings.isNullOrEmpty(type)) {
                if (list.size() == 0) {
                    WalletInfo walletInfo = new WalletInfo();
                    walletInfo.itemType = WalletListTypeAdapter.NoWallet;
                    list.add(walletInfo);
                    PreferencesManager.put(MyApplication.getInstance(), "Preferences", Constant.HAS_LOCAL_HD, false);
                } else {
                    setItemType(list, WalletListTypeAdapter.WalletNorMal);
                    WalletInfo walletInfo = new WalletInfo();
                    walletInfo.itemType = WalletListTypeAdapter.AddWallet;
                    list.add(walletInfo);
                }
                WalletAsset asset = new WalletAsset();
                asset.wallets.addAll(list);
                asset.validShowNum = getShowNum(list);
                mAllWallets.postValue(asset);
            } else {
                if (type.equals(Constant.BTC)) {
                    setItemType(list, WalletListTypeAdapter.WalletNorMal);
                    mBtcWallets.postValue(list);
                } else if (type.equals(Constant.ETH)) {
                    setItemType(list, WalletListTypeAdapter.WalletNorMal);
                    mEthWallets.postValue(list);
                }
            }
        } else {
            Logger.d("异常", "-->" + response.getErrors());
        }
    }

    // 组装数据
    private void setItemType(List<WalletInfo> list, int type) {
        for (WalletInfo walletInfo : list) {
            walletInfo.itemType = type;
        }
    }

    private int getShowNum(List<WalletInfo> mAllData) {
        List<WalletInfo> showNumList = new ArrayList<>();
        for (WalletInfo data : mAllData) {
            if (data.itemType == WalletListTypeAdapter.WalletNorMal) {
                showNumList.add(data);
            }
        }
        return showNumList.size();
    }

}

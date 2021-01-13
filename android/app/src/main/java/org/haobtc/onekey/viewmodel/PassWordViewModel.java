package org.haobtc.onekey.viewmodel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 身份校验页面的ViewModel
 * @Author: peter Qin
 */
public class PassWordViewModel extends ViewModel {
    public MutableLiveData<Integer> passSetType = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLongType = new MutableLiveData<>();
    public SystemConfigManager systemConfigManager = new SystemConfigManager(MyApplication.getInstance());

    public PassWordViewModel () {
        checkPssLengthType();
    }

    private void checkPssLengthType () {
        String type = systemConfigManager.getPassWordType();
        if (Constant.SOFT_HD_PASS_TYPE_LONG.equals(type)) {
            isLongType.postValue(true);
        } else {
            isLongType.postValue(false);
        }
    }

    public void checkIsSetType (int numType) {
        passSetType.postValue(numType);
        List<String> needPassWallets = new ArrayList<>();
        Map<String, ?> jsonToMap = PreferencesManager.getAll(MyApplication.getInstance(), Constant.WALLETS);
        jsonToMap.entrySet().forEach(stringEntry -> {
            LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
            String type = info.getType();
            String label = info.getLabel();
            if (!type.contains("hw") && !"btc-watch-standard".equals(type)) {
                needPassWallets.add(label);
            }
        });
        if (needPassWallets.size() == 0) {
            passSetType.postValue(0);
        }
    }

    public void setPassLengthType (boolean isLong) {
        systemConfigManager.setPassWordType(isLong ? SystemConfigManager.SoftHdPassType.LONG : SystemConfigManager.SoftHdPassType.SHORT);
        isLongType.postValue(isLong);
    }

}

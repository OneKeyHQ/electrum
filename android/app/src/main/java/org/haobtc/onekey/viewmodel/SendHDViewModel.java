package org.haobtc.onekey.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.base.Strings;
import com.orhanobut.logger.Logger;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PyEnv;

/**
 * @Description: 转账功能的ViewModel
 * @Author: peter Qin
 */
public class SendHDViewModel extends ViewModel {
    public MutableLiveData<CurrentFeeDetails> currentFeeDetail = new MutableLiveData<>();
    public MutableLiveData<Double> feeRate = new MutableLiveData<>();

    public void getDefaultFeeRate() {
        String type = new AccountManager(MyApplication.getInstance()).getCurrentWalletType();
        if (type.contains(Constant.BTC)) {

        } else if (type.equals(Constant.ETH)) {

        }
    }

    private void getBtcFeeRate() {
        PyResponse<String> response = PyEnv.getFeeInfo();
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            Logger.json(response.getResult());
            currentFeeDetail.postValue(CurrentFeeDetails.objectFromDate(response.getResult()));
        } else {
            currentFeeDetail.postValue(null);
        }
    }

}

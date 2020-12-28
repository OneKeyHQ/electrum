package org.haobtc.onekey.business.qrdecode;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONObject;

/**
 * 解析扫描二维码内容
 *
 * @author Onekey@QuincySx
 * @create 2020-12-28 10:31 AM
 */
public class QRDecode {

    /**
     * 在 Python 返回的金额字符串中截取 BTC 金额
     *
     * @param resultAmount Python 返回的金额字符串
     * @return 实际的 BTC 金额
     */
    @Nullable
    public String getAmountByPythonResultAmount(@Nullable String resultAmount) {
        if (TextUtils.isEmpty(resultAmount)) {
            return null;
        }
        String[] amountSplit = resultAmount.split(" ");
        return amountSplit.length >= 1 ? amountSplit[0] : null;
    }

    /**
     * 在二维码字符串中尝试解析 BTC 地址
     * support: bip72、bip21
     *
     * @param content 要解析的字符串
     * @return BTC 地址，如果解析的地址格式不正确，返回 null。
     */
    @WorkerThread
    @Nullable
    public MainSweepcodeBean.DataBean decodeAddress(String content) {
        MainSweepcodeBean.DataBean resultBean = new MainSweepcodeBean.DataBean();
        resultBean.setAddress(content);
        if (!TextUtils.isEmpty(content)) {
            try {
                PyObject parseQr = Daemon.commands.callAttr("parse_pr", content);
                if (parseQr.toString().length() > 2) {
                    String strParse = parseQr.toString();
                    String substring = strParse.substring(20);
                    String detailScan = substring.substring(0, substring.length() - 1);
                    JSONObject jsonObject = new JSONObject(strParse);
                    int type = jsonObject.getInt("type");
                    Gson gson = new Gson();
                    if (type == 1) {
                        MainSweepcodeBean mainSweepcodeBean = gson.fromJson(strParse, MainSweepcodeBean.class);
                        resultBean = mainSweepcodeBean.getData();
                        if (!TextUtils.isEmpty(resultBean.getAmount())) {
                            String amountByPythonResultAmount = getAmountByPythonResultAmount(resultBean.getAmount());
                            resultBean.setAmount(amountByPythonResultAmount);
                        }
                    } else {
                        resultBean.setAddress(detailScan);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultBean;
    }
}

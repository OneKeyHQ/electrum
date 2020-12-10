package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.GetAddressBean;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;
import org.haobtc.onekey.bean.GetsendFeenumBean;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.InputPassSendEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.utils.Daemon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

public class SendHdActivity extends BaseActivity implements TextWatcher, BusinessAsyncTask.Helper {

    @BindView(R.id.edit_input_address)
    EditText editInputAddress;
    @BindView(R.id.edit_amount)
    EditText tetamount;
    @BindView(R.id.text_fee_50)
    TextView textFee50;
    @BindView(R.id.text_dollar_50)
    TextView textDollar50;
    @BindView(R.id.text_fee_20)
    TextView textFee20;
    @BindView(R.id.text_dollar_20)
    TextView textDollar20;
    @BindView(R.id.text_fee_10)
    TextView textFee10;
    @BindView(R.id.text_dollar_10)
    TextView textDollar10;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.checkbox_slow)
    CheckBox checkboxSlow;
    @BindView(R.id.view_slow)
    View viewSlow;
    @BindView(R.id.checkbox_recommend)
    CheckBox checkboxRecommend;
    @BindView(R.id.view_recommend)
    View viewRecommend;
    @BindView(R.id.checkbox_fast)
    CheckBox checkboxFast;
    @BindView(R.id.view_fast)
    View viewFast;
    @BindView(R.id.text_num)
    TextView textNum;
    @BindView(R.id.text_slow_time)
    TextView textSlowTime;
    @BindView(R.id.text_recommend_time)
    TextView textRecommendTime;
    @BindView(R.id.text_fast_time)
    TextView textFastTime;
    @BindView(R.id.lin_choose_fee)
    LinearLayout linChooseFee;
    @BindView(R.id.lin_custom)
    LinearLayout linCustom;
    @BindView(R.id.text_fee_custom)
    TextView textFeeCustom;
    @BindView(R.id.text_dollar_custom)
    TextView textDollarCustom;
    @BindView(R.id.text_custom_time)
    TextView textCustomTime;
    private String sendNum;
    private int screenHeight;
    private boolean mIsSoftKeyboardShowing;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;
    private int recommend = 0;
    private BigInteger customFee;
    private int customTime;
    private SharedPreferences preferences;
    private String hdWalletName;
    private String strGive;
    private String fastTx;
    private String recommendTx;
    private String slowTx;
    private String customTx;
    private String useTx = "";
    float feeForChild;
    private String baseUnit;
    private String cnyUnit;
    private String errorMessage = "";
    private String amountUnit;
    private float amountUnitSat;
    private boolean flag = true;
    private boolean max;
    private BigDecimal bigRecommendFee;
    private float fastFeeForChild;
    private float slowFeeForChild;
    private float customFeeForChild;
    private String showWalletType;
    private String infoFromRaw;
    private String signedTx;


    @Override
    public int getLayoutId() {
        return R.layout.activity_send_hd;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        hdWalletName = getIntent().getStringExtra("hdWalletName");
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        sendNum = getIntent().getStringExtra("sendNum");
        showWalletType = preferences.getString(CURRENT_SELECTED_WALLET_TYPE, "");
        textSlowTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
        textRecommendTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
        textFastTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
        baseUnit = preferences.getString("base_unit", "");
        cnyUnit = preferences.getString("cny_strunit", "CNY");
        String addressScan = getIntent().getStringExtra("addressScan");
        if (!TextUtils.isEmpty(addressScan)) {
            editInputAddress.setText(addressScan);
        }
        textNum.setText(String.format("%s%s", sendNum, preferences.getString("base_unit", "")));

    }

    @Override
    public void initData() {
        registerKeyBoard();
        tetamount.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.tet_choose_type, R.id.text_max, R.id.tet_custom_fee, R.id.lin_slow, R.id.lin_recommend, R.id.lin_fast, R.id.btn_next, R.id.img_paste, R.id.text_recovery_default})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_choose_type:
//                Intent intent = new Intent(SendHdActivity.this, ChooseCurrencyActivity.class);
//                startActivity(intent);
                break;
            case R.id.text_max:
//                if (TextUtils.isEmpty(editInputAddress.getText().toString())) {
//                    mToast(getString(R.string.input_number));
//                    return;
//                }
                tetamount.setText(sendNum);
//                max = true;
                //getFeerate
                getFeeamont();
                //button to gray or blue
                changeButton();
                break;
            case R.id.tet_custom_fee:
                if (TextUtils.isEmpty(editInputAddress.getText().toString())) {
                    mToast(getString(R.string.input_number));
                    return;
                }
                if (TextUtils.isEmpty(tetamount.getText().toString())) {
                    mToast(getString(R.string.input_out_number));
                    return;
                }
                if (errorMessage.contains("Insufficient funds")) {
                    mToast(getString(R.string.wallet_insufficient));
                    return;
                }
                createWalletChooseDialog(SendHdActivity.this, R.layout.custom_fee);
                break;
            case R.id.lin_slow:
                if (TextUtils.isEmpty(editInputAddress.getText().toString())) {
                    mToast(getString(R.string.input_number));
                    return;
                }
                if (TextUtils.isEmpty(tetamount.getText().toString())) {
                    mToast(getString(R.string.input_out_number));
                    return;
                }
                viewSlow.setVisibility(View.VISIBLE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.VISIBLE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.GONE);
                useTx = slowTx;
//                if (max) {
//                    //慢速下 可发送的最大值
//                    textFeeForChild(slowFeeForChild);
//                }
                break;
            case R.id.lin_recommend:
                if (TextUtils.isEmpty(editInputAddress.getText().toString())) {
                    mToast(getString(R.string.input_number));
                    return;
                }
                if (TextUtils.isEmpty(tetamount.getText().toString())) {
                    mToast(getString(R.string.input_out_number));
                    return;
                }
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.VISIBLE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.VISIBLE);
                checkboxFast.setVisibility(View.GONE);
                useTx = recommendTx;
//                if (max) {
//                    //推速度下 可发送的最大值
//                    textFeeForChild(feeForChild);
//                }
                break;
            case R.id.lin_fast:
                if (TextUtils.isEmpty(editInputAddress.getText().toString())) {
                    mToast(getString(R.string.input_number));
                    return;
                }
                if (TextUtils.isEmpty(tetamount.getText().toString())) {
                    mToast(getString(R.string.input_out_number));
                    return;
                }
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.VISIBLE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.VISIBLE);
                useTx = fastTx;
//                if (max) {
//                    textFeeForChild(fastFeeForChild);
//                }
                break;
            case R.id.btn_next:
                if (!TextUtils.isEmpty(errorMessage)) {
                    if (errorMessage.contains("Insufficient funds")) {
                        mToast(getString(R.string.wallet_insufficient));
                        return;
                    } else {
                        mToast(errorMessage);
                        return;
                    }
                }
                try {
                    infoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", useTx).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
                    sendCurrency("");
                    return;
                }
                if (!TextUtils.isEmpty(infoFromRaw)) {
                    sendConfirmDialog(SendHdActivity.this, R.layout.send_confirm_dialog, infoFromRaw);
                }
                break;
            case R.id.img_paste:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData data = clipboard.getPrimaryClip();
                    if (data != null && data.getItemCount() > 0) {
                        editInputAddress.setText(data.getItemAt(0).getText());
                        //getFeerate
                        getFeeamont();
                        //button to gray or blue
                        changeButton();
                    }
                }
                break;
            case R.id.text_recovery_default:
                useTx = recommendTx;
                linChooseFee.setVisibility(View.VISIBLE);
                linCustom.setVisibility(View.GONE);
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.VISIBLE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.VISIBLE);
                checkboxFast.setVisibility(View.GONE);
//                if (max) {
//                    //自定义下 可发送的最大值
//                    textFeeForChild(feeForChild);
//                }
                break;
        }
    }

    private void textFeeForChild(float fastFeeForChild) {
        //快下 可发送的最大值
        BigDecimal bigDecimalFee2 = new BigDecimal(fastFeeForChild);
        BigDecimal bigDecimalSum2 = new BigDecimal(sendNum);
        bigRecommendFee = bigDecimalSum2.subtract(bigDecimalFee2);//推荐的最大费
        tetamount.setText(String.valueOf(bigRecommendFee));
    }

    private String rawTx;

    private void sendCurrency(String pass) {
        PyObject mktx;
        try {
            mktx = Daemon.commands.callAttr("mktx", useTx);

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Insufficient funds")) {
                mToast(getString(R.string.wallet_insufficient));
            } else {
                mToast(getString(R.string.changeaddress));
            }
            return;
        }
        if (mktx != null) {
            String jsonObj = mktx.toString();
            Gson gson = new Gson();
            GetAddressBean getAddressBean = gson.fromJson(jsonObj, GetAddressBean.class);
            rawTx = getAddressBean.getTx();
            //sign
            if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
                hardwareSign(rawTx);
            } else {
                signTx(rawTx, pass);
            }

        }

    }

    //soft sign
    private void signTx(String rowtx, String password) {
        try {
            String signContent = Daemon.commands.callAttr("sign_tx", rowtx, "", new Kwarg("password", password)).toString();
            if (!Strings.isNullOrEmpty(signContent)) {
                broacastTx(rowtx, signContent);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
        }

    }

    private void broacastTx(String rowtx, String signContent) {
        Gson gson = new Gson();
        GetnewcreatTrsactionListBean trsactionListBean = gson.fromJson(signContent.toString(), GetnewcreatTrsactionListBean.class);
        Daemon.commands.callAttr("broadcast_tx", trsactionListBean.getTx());
        Intent intent = new Intent(SendHdActivity.this, DetailTransactionActivity.class);
        intent.putExtra("txDetail", rowtx);
        startActivity(intent);
        EventBus.getDefault().post(new SecondEvent("finish"));
        finish();
    }

    private Button confirmBtn;

    private void sendConfirmDialog(Context context, @LayoutRes int resource, String detail) {
        Gson gson = new Gson();
        GetnewcreatTrsactionListBean fromJson = gson.fromJson(detail, GetnewcreatTrsactionListBean.class);
        Log.i("Tjindetail", "sendConfirmDialog:--=====  " + detail);
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        confirmBtn = view.findViewById(R.id.btn_confirm_pay);
        if (showWalletType.contains("watch")) {
            confirmBtn.setText(getString(R.string.confirm));
        } else if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
            confirmBtn.setText("在设备进行确认");
            confirmBtn.setEnabled(false);
        }
        TextView txAmount = view.findViewById(R.id.text_tx_amount);
        txAmount.setText(String.format("%s%s", tetamount.getText().toString(), preferences.getString("base_unit", "")));
        TextView sendAddress = view.findViewById(R.id.text_send_address);
        TextView receiveAddr = view.findViewById(R.id.text_receive_address);
        if (fromJson.getInputAddr() != null && fromJson.getInputAddr().size() != 0) {
            String inputAddr = fromJson.getInputAddr().get(0).getAddr();
            sendAddress.setText(inputAddr);
        }
        if (fromJson.getOutputAddr() != null && fromJson.getOutputAddr().size() != 0) {
            String outputAddr = fromJson.getOutputAddr().get(0).getAddr();
            receiveAddr.setText(outputAddr);
        }
        TextView sendName = view.findViewById(R.id.text_send_name);
        sendName.setText(hdWalletName);
        TextView txFee = view.findViewById(R.id.text_tx_fee);
        txFee.setText(fromJson.getFee());

        confirmBtn.setOnClickListener(v -> {
            if (showWalletType.contains("watch")) {
                dialogBtoms.dismiss();
            } else if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
                broacastTx(rawTx, signedTx);
            } else {
                //sign trsaction
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    Intent intent = new Intent(this, SetHDWalletPassActivity.class);
                    intent.putExtra("importHdword", "send");
                    intent.putExtra("useTx", useTx);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, SetLongPassActivity.class);
                    intent.putExtra("importHdword", "send");
                    intent.putExtra("useTx", useTx);
                    startActivity(intent);
                }
            }
        });
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();


    }

    private void getFeeamont() {
//        if (max) {
//            getDefaultFeeStatus();
//        } else {
        if (!TextUtils.isEmpty(editInputAddress.getText().toString()) && !TextUtils.isEmpty(tetamount.getText().toString())) {
            getDefaultFeeStatus();
        }
//        }
    }

    private void getDefaultFeeStatus() {
        PyObject getDefaultFeeStatus = null;
        try {
            getDefaultFeeStatus = Daemon.commands.callAttr("get_default_fee_status");
        } catch (Exception e) {
            Log.e("TAGgetFastFeerate", "getFastFeerate: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        String strFee = getDefaultFeeStatus.toString();
        if (!TextUtils.isEmpty(strFee)) {
            if (strFee.contains("sat/byte")) {
                String strFeeamont = strFee.substring(0, strFee.indexOf("sat/byte"));
                String strMaxTemp = strFeeamont.replaceAll(" ", "");
                strGive = strMaxTemp.split("\\.", 2)[0];
                int feeMax = (Integer.parseInt(strGive)) * 2;
                getFeerate(strGive);
                getSlowFeerate("1");
                getFastFeerate(feeMax + "");
            }
        }
    }

    //get fast fee
    private void getFastFeerate(String fastFee) {
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
//        if (max) {
//            pramas.put(editInputAddress.getText().toString(), "!");
//        } else {
        pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
//        }
        arrayList.add(pramas);
        String strPramas = new Gson().toJson(arrayList);
        float strRecommend = Float.parseFloat(fastFee);
        Log.i("strPramasjxm", "getFastFeerate: " + strPramas);
        PyObject getFeeByFeeRate = null;
        try {
            getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, "", strRecommend);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (getFeeByFeeRate != null) {
            String strnewFee = getFeeByFeeRate.toString();
            Log.i("strnewFeestrnewFee", "getFeerate:-- " + strnewFee);
            Gson gson = new Gson();
            GetsendFeenumBean getsendFeenumBean = gson.fromJson(strnewFee, GetsendFeenumBean.class);
            BigInteger fee = getsendFeenumBean.getFee();
            int time = getsendFeenumBean.getTime();
            String feeNum = String.valueOf(fee);
            fastTx = getsendFeenumBean.getTx();
            textFastTime.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
            if ("BTC".equals(baseUnit)) {
                fastFeeForChild = Float.parseFloat(feeNum) / 100000000;
            } else if ("mBTC".equals(baseUnit)) {
                fastFeeForChild = Float.parseFloat(feeNum) / 100000;
            } else if ("bits".equals(baseUnit)) {
                fastFeeForChild = Float.parseFloat(feeNum) / 100;
            }
            textFee10.setText(String.format("%s %s", fastFeeForChild, baseUnit));
            try {
                PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", String.valueOf(fastFeeForChild));
                if ("CNY".equals(cnyUnit)) {
                    textDollar10.setText(String.format("￥ %s", money.toString()));
                } else if ("USD".equals(cnyUnit)) {
                    textDollar10.setText(String.format("$ %s", money.toString()));
                } else {
                    textDollar10.setText(money.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    //get Recommend fee
    private void getFeerate(String strGive) {
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
//        if (max) {
//            pramas.put(editInputAddress.getText().toString(), "!");
//        } else {
        pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
//        }
        arrayList.add(pramas);
        String strPramas = new Gson().toJson(arrayList);
        float strRecommend = Float.parseFloat(strGive);
        PyObject getFeeByFeeRate = null;
        try {
            getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, "", strRecommend);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
            if (errorMessage.contains("Insufficient funds")) {
                mToast(getString(R.string.wallet_insufficient));
            }
            return;
        }
        if (getFeeByFeeRate != null) {
            String strnewFee = getFeeByFeeRate.toString();
            Log.i("strnewFeestrnewFee", "getFeerate:-- " + strnewFee);
            Gson gson = new Gson();
            GetsendFeenumBean getsendFeenumBean = gson.fromJson(strnewFee, GetsendFeenumBean.class);
            BigInteger fee = getsendFeenumBean.getFee();
            int time = getsendFeenumBean.getTime();
            String feeNum = String.valueOf(fee);
            recommendTx = getsendFeenumBean.getTx();
            useTx = recommendTx;
            textRecommendTime.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
            if ("BTC".equals(baseUnit)) {
                feeForChild = Float.parseFloat(feeNum) / 100000000;
            } else if ("mBTC".equals(baseUnit)) {
                feeForChild = Float.parseFloat(feeNum) / 100000;
            } else if ("bits".equals(baseUnit)) {
                feeForChild = Float.parseFloat(feeNum) / 100;
            }
            textFee20.setText(String.format("%s %s", feeForChild, baseUnit));
//            if (max) {
//                //推荐矿工费下 可发送的最大值
//                textFeeForChild(feeForChild);
//            }

            try {
                PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", String.valueOf(feeForChild));
                if ("CNY".equals(cnyUnit)) {
                    textDollar20.setText(String.format("￥ %s", money.toString()));
                } else if ("USD".equals(cnyUnit)) {
                    textDollar20.setText(String.format("$ %s", money.toString()));
                } else {
                    textDollar20.setText(money.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    //get slow fee
    private void getSlowFeerate(String slowFee) {
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
//        if (max) {
//            pramas.put(editInputAddress.getText().toString(), "!");
//        } else {
        pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
//        }
        arrayList.add(pramas);
        String strPramas = new Gson().toJson(arrayList);
        float strRecommend = Float.parseFloat(slowFee);
        PyObject getFeeByFeeRate = null;
        try {
            getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, "", strRecommend);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (getFeeByFeeRate != null) {
            String strnewFee = getFeeByFeeRate.toString();
            Log.i("strnewFeestrnewFee", "getFeerate:-- " + strnewFee);
            Gson gson = new Gson();
            GetsendFeenumBean getsendFeenumBean = gson.fromJson(strnewFee, GetsendFeenumBean.class);
            BigInteger fee = getsendFeenumBean.getFee();
            int time = getsendFeenumBean.getTime();
            String feeNum = String.valueOf(fee);
            slowTx = getsendFeenumBean.getTx();
            textSlowTime.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
            if ("BTC".equals(baseUnit)) {
                slowFeeForChild = Float.parseFloat(feeNum) / 100000000;
            } else if ("mBTC".equals(baseUnit)) {
                slowFeeForChild = Float.parseFloat(feeNum) / 100000;
            } else if ("bits".equals(baseUnit)) {
                slowFeeForChild = Float.parseFloat(feeNum) / 100;
            }
            textFee50.setText(String.format("%s %s", slowFeeForChild, baseUnit));
            try {
                PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", String.valueOf(slowFeeForChild));
                if ("CNY".equals(cnyUnit)) {
                    textDollar50.setText(String.format("￥ %s", money.toString()));
                } else if ("USD".equals(cnyUnit)) {
                    textDollar50.setText(String.format("$ %s", money.toString()));
                } else {
                    textDollar50.setText(money.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private void changeButton() {
        if (!TextUtils.isEmpty(editInputAddress.getText().toString()) && !TextUtils.isEmpty(tetamount.getText().toString())) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        EditText editFeeByte = view.findViewById(R.id.edit_fee_byte);
        TextView textSize = view.findViewById(R.id.text_size);
        TextView textTime = view.findViewById(R.id.text_time);
        TextView textBtc = view.findViewById(R.id.text_btc);
        Button btnConfirm = view.findViewById(R.id.btn_next);
        TextView textDollar = view.findViewById(R.id.text_dollar);
        editFeeByte.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    Long aLong = Long.valueOf(s.toString());
                    if (aLong > 100000000) {
                        mToast(getString(R.string.dont_greater_than));
                        textSize.setText("");
                        textTime.setText(String.format("%s", getString(R.string.second_0)));
                        textBtc.setText("");
                        editFeeByte.setText("");
                        return;
                    }
                    ArrayList<Map<String, String>> arrayList = new ArrayList<>();
                    Map<String, String> pramas = new HashMap<>();
//                    if (max) {
//                        pramas.put(editInputAddress.getText().toString(), "!");
//                    } else {
                    pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
//                    }
                    arrayList.add(pramas);
                    String strPramas = new Gson().toJson(arrayList);
                    float strRecommend = Float.parseFloat(s.toString());
                    PyObject getFeeByFeeRate = null;
                    try {
                        getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, "", strRecommend);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (getFeeByFeeRate != null) {
                        String strnewFee = getFeeByFeeRate.toString();
                        Log.i("strnewFeestrnewFee", "getFeerate:-- " + strnewFee);
                        Gson gson = new Gson();
                        GetsendFeenumBean getsendFeenumBean = gson.fromJson(strnewFee, GetsendFeenumBean.class);
                        customFee = getsendFeenumBean.getFee();
                        customTime = getsendFeenumBean.getTime();
                        int size = getsendFeenumBean.getSize();
                        textSize.setText(String.valueOf(size));
                        customTx = getsendFeenumBean.getTx();
                        textTime.setText(String.format("%s%s%s", getString(R.string.second_0), customTime + "", getString(R.string.minute)));
                        textBtc.setText(String.format("%ssat", customFee));

                        if ("BTC".equals(baseUnit)) {
                            customFeeForChild = Float.parseFloat(customFee.toString()) / 100000000;
                        } else if ("mBTC".equals(baseUnit)) {
                            customFeeForChild = Float.parseFloat(customFee.toString()) / 100000;
                        } else if ("bits".equals(baseUnit)) {
                            customFeeForChild = Float.parseFloat(customFee.toString()) / 100;
                        }
                        try {
                            PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", String.valueOf(customFeeForChild));
                            if ("CNY".equals(cnyUnit)) {
                                textDollar.setText(String.format("≈ ￥ %s", money.toString()));
                            } else if ("USD".equals(cnyUnit)) {
                                textDollar.setText(String.format("≈ $ %s", money.toString()));
                            } else {
                                textDollar.setText(money.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                } else {
                    textSize.setText("");
                    textTime.setText(String.format("%s", getString(R.string.second_0)));
                    textBtc.setText("");
                    textDollar.setText("");
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editFeeByte.getText().toString())) {
                    mToast(getString(R.string.please_input_fee));
                    return;
                }
                useTx = customTx;
                linChooseFee.setVisibility(View.GONE);
                linCustom.setVisibility(View.VISIBLE);
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.GONE);
                textFeeCustom.setText(String.format("%s", customFee + ""));
                textCustomTime.setText(String.format("%s%s%s", getString(R.string.about_), customTime + "", getString(R.string.minute)));
//                if (max) {
//                    //自定义下 可发送的最大值
//                    textFeeForChild(customFeeForChild);
//                }
                if (customFee == null) {
                    return;
                }
                if ("BTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(String.valueOf(customFee)) / 100000000;
                } else if ("mBTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(String.valueOf(customFee)) / 100000;
                } else if ("bits".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(String.valueOf(customFee)) / 100;
                }
                try {
                    PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", String.valueOf(feeForChild));
                    if ("CNY".equals(cnyUnit)) {
                        textDollarCustom.setText(String.format("￥ %s", money.toString()));
                    } else if ("USD".equals(cnyUnit)) {
                        textDollarCustom.setText(String.format("$ %s", money.toString()));
                    } else {
                        textDollarCustom.setText(money.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                dialogBtoms.dismiss();
            }
        });
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

    private void registerKeyBoard() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;
        mIsSoftKeyboardShowing = false;
        mLayoutChangeListener = () -> {
            //Determine the size of window visible area
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //If the difference between screen height and window visible area height is greater than 1 / 3 of the whole screen height, it means that the soft keyboard is in display, otherwise, the soft keyboard is hidden.
            int heightDifference = screenHeight - (r.bottom - r.top);
            boolean isKeyboardShowing = heightDifference > screenHeight / 3;

            //If the status of the soft keyboard was previously displayed, it is now closed, or it was previously closed, it is now displayed, it means that the status of the soft keyboard has changed
            if ((mIsSoftKeyboardShowing && !isKeyboardShowing) || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
                mIsSoftKeyboardShowing = isKeyboardShowing;
                if (!mIsSoftKeyboardShowing) {
                    //getFeerate
                    getFeeamont();
                    //button to gray or blue
                    changeButton();
                }
            }
        };
        //Register layout change monitoring
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 7) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 8);
                tetamount.setText(s);
                tetamount.setSelection(s.length());
            }
        }
        if (".".equals(s.toString().trim())) {
            s = "0" + s;
            tetamount.setText(s);
            tetamount.setSelection(2);
        }
        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!".".equals(s.toString().substring(1, 2))) {
                tetamount.setText(s.subSequence(0, 1));
                tetamount.setSelection(1);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String strAmount = editable.toString();
        if (!TextUtils.isEmpty(strAmount)) {
            if (flag) {
                flag = false;
//                max = false;
            } else {
                flag = true;
            }
        } else {
            if (flag) {
                flag = false;
            } else {
                flag = true;
            }
        }
    }

    @Subscribe
    public void event(InputPassSendEvent event) {
        //create and sign、broadcast
        sendCurrency(event.getPass());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 硬件签名方法
     */
    private void hardwareSign(String rawTx) {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_TX,
                rawTx,
                MyApplication.getInstance().getDeviceWay());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        // 回写PIN码
        PyEnv.setPin(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        switch (event.getType()) {
            case PyConstant.PIN_CURRENT:
                Intent intent = new Intent(this, VerifyPinActivity.class);
                startActivity(intent);
                break;
            case PyConstant.BUTTON_REQUEST_7:
                break;
            case PyConstant.BUTTON_REQUEST_8:
                EventBus.getDefault().post(new ExitEvent());
                sendConfirmDialog(SendHdActivity.this, R.layout.send_confirm_dialog, infoFromRaw);
                break;
            default:

        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {
        mlToast(e.getMessage());
    }

    @Override
    public void onResult(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            signedTx = s;
            confirmBtn.setEnabled(true);
        } else {
            finish();
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("fuck you in send page!!!!!!!!!");
    }
}
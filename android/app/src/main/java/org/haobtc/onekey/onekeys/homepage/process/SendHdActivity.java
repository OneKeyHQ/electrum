package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.GetAddressBean;
import org.haobtc.onekey.bean.GetsendFeenumBean;
import org.haobtc.onekey.utils.Daemon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendHdActivity extends BaseActivity implements TextWatcher {

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_hd;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        sendNum = getIntent().getStringExtra("sendNum");
        textNum.setText(sendNum);
        textSlowTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
        textRecommendTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
        textFastTime.setText(String.format("%s0%s", getString(R.string.about_), getString(R.string.minute)));
    }

    @Override
    public void initData() {
        registerKeyBoard();
        tetamount.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.tet_choose_type, R.id.text_max, R.id.tet_custom_fee, R.id.lin_slow, R.id.lin_recommend, R.id.lin_fast, R.id.btn_next, R.id.img_paste})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_choose_type:
                Intent intent = new Intent(SendHdActivity.this, ChooseCurrencyActivity.class);
                startActivity(intent);
                break;
            case R.id.text_max:
                tetamount.setText(sendNum);
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
                createWalletChooseDialog(SendHdActivity.this, R.layout.custom_fee);
                break;
            case R.id.lin_slow:
                viewSlow.setVisibility(View.VISIBLE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.VISIBLE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.GONE);
                break;
            case R.id.lin_recommend:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.VISIBLE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.VISIBLE);
                checkboxFast.setVisibility(View.GONE);
                break;
            case R.id.lin_fast:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.VISIBLE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_next:
                sendCurrency();

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
        }
    }

    private void sendCurrency() {
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
        pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
        arrayList.add(pramas);
        String strPramas = new Gson().toJson(arrayList);
        Log.i("strPramasstrPramasstrPramas", "sendCurrency: ==" + strPramas);
        PyObject mktx;
        try {
            mktx = Daemon.commands.callAttr("mktx", strPramas, "");

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
            String rowtx = getAddressBean.getTx();

            mToast("chenhgong");
        }

    }

    private void getFeeamont() {
        if (!TextUtils.isEmpty(editInputAddress.getText().toString()) && !TextUtils.isEmpty(tetamount.getText().toString())) {
            PyObject getDefaultFeeStatus = null;
            try {
                getDefaultFeeStatus = Daemon.commands.callAttr("get_default_fee_status");
            } catch (Exception e) {
                e.printStackTrace();
                mToast(e.getMessage());
                return;
            }

            if (getDefaultFeeStatus != null) {
                String strFee = getDefaultFeeStatus.toString();
                Log.i("get_default_fee", "strFee:   " + strFee);
                if (strFee.contains("sat/byte")) {
                    String strFeeamont = strFee.substring(0, strFee.indexOf("sat/byte"));
                    String strMaxTemp = strFeeamont.replaceAll(" ", "");
                    String strGive = strMaxTemp.split("\\.", 2)[0];
                    Log.i("strMaxsss", "getFeeamont:-- " + strGive);
                    getFeerate(strGive);

                }
            }
        }
    }

    private void getFeerate(String strGive) {
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
        pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
        arrayList.add(pramas);
        String strPramas = new Gson().toJson(arrayList);
        float strRecommend = Float.parseFloat(strGive);
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
            int feeMax = (Integer.parseInt(feeNum)) * 2;
            String maxNum = String.valueOf(feeMax);
            int slowTime = (int) Math.floor(time / 2);
            textSlowTime.setText(String.format("%s%s%s", getString(R.string.about_), slowTime + "", getString(R.string.minute)));
            textRecommendTime.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
            textFastTime.setText(String.format("%s%s%s", getString(R.string.about_), (time * 2) + "", getString(R.string.minute)));
            textFee20.setText(String.format("%s sat", feeNum));
            textFee10.setText(String.format("%s sat", maxNum));
//            recommend = feeNum;
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
                    ArrayList<Map<String, String>> arrayList = new ArrayList<>();
                    Map<String, String> pramas = new HashMap<>();
                    pramas.put(editInputAddress.getText().toString(), tetamount.getText().toString());
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
                        textTime.setText(String.format("%s%s%s", getString(R.string.second_0), customTime + "", getString(R.string.minute)));
                        textBtc.setText(String.format("%ssat", customFee));

                    }
                } else {
                    textSize.setText("");
                    textTime.setText(String.format("%s", getString(R.string.second_0)));
                    textBtc.setText("");
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linChooseFee.setVisibility(View.GONE);
                linCustom.setVisibility(View.VISIBLE);
                textFeeCustom.setText(String.format("%s", customFee + ""));
                textCustomTime.setText(String.format("%s%s%s", getString(R.string.about_), customTime + "", getString(R.string.minute)));
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
    public void afterTextChanged(Editable s) {

    }

}
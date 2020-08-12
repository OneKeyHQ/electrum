package org.haobtc.wallet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;
import org.haobtc.wallet.adapter.ChoosePayAddressAdapter;
import org.haobtc.wallet.bean.AddressEvent;
import org.haobtc.wallet.bean.GetAddressBean;
import org.haobtc.wallet.bean.GetCodeAddressBean;
import org.haobtc.wallet.bean.GetnewcreatTrsactionListBean;
import org.haobtc.wallet.bean.GetsendFeenumBean;
import org.haobtc.wallet.bean.HardwareFeatures;
import org.haobtc.wallet.bean.MainNewWalletBean;
import org.haobtc.wallet.bean.MainSweepcodeBean;
import org.haobtc.wallet.event.FirstEvent;
import org.haobtc.wallet.event.HandlerEvent;
import org.haobtc.wallet.event.MainpageWalletEvent;
import org.haobtc.wallet.event.SecondEvent;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.IndicatorSeekBar;
import org.json.JSONException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.heaton.blelibrary.ble.Ble;

public class SendOne2OneMainPageActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    public static final String TAG = SendOne2OneMainPageActivity.class.getSimpleName();
    @BindView(R.id.edit_changeMoney)
    EditText editChangeMoney;
    @BindView(R.id.seek_bar)
    IndicatorSeekBar seekBar;
    @BindView(R.id.tv_indicator)
    TextView tvIndicator;
    @BindView(R.id.lin_btcAddress)
    LinearLayout mRootView;
    @BindView(R.id.tet_Table)
    TextView tetTable;
    @BindView(R.id.tet_WalletTable)
    TextView tetWalletTable;
    @BindView(R.id.tet_strunit)
    TextView tetStrunit;
    @BindView(R.id.btnRecommendFee)
    TextView btnRecommendFee;
    @BindView(R.id.testNowCanUse)
    TextView testNowCanUse;
    @BindView(R.id.text_blocks)
    EditText textBlocks;
    @BindView(R.id.linear_show)
    LinearLayout linearShow;
    @BindView(R.id.choose_text)
    TextView chooseText;
    @BindView(R.id.text_choose_num)
    TextView textChooseNum;
    private LinearLayout selectSend, selectSigNumLin;
    private ImageView buttonSweep, selectSigNum;
    private EditText editTextComments, editAddress;
    private TextView bytesCount, buttonPaste;
    private Button buttonCreate;
    private Dialog dialogBtom;
    private TextView tetMoneye;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private EditText tetamount;
    private List<AddressEvent> dataListName;
    private ChoosePayAddressAdapter choosePayAddressAdapetr;
    private String straddress;
    private String strAmount;
    private TextView tetWalletname;
    private String wallet_name;
    private TextView textView;
    private PyObject get_wallets_list;
    private PyObject pyObject;
    private float intmaxFee;
    private String strComment = "";
    private String waletType;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;
    private boolean mIsSoftKeyboardShowing;
    private int screenHeight;
    private String base_unit;
    private String strUnit;
    private String errorMessage = "";
    private String hideRefresh = "";
    private String wallet_type_to_sign;
    private String payAddress;
    private ArrayList<GetnewcreatTrsactionListBean.OutputAddrBean> outputAddr;
    private boolean showSeek = true;
    private String onlickName;
    private boolean flag = true;
    private int wallet_name_pos;
    private String fee;
    private int recommendFee;
    private String feeNum;
    private float tjFee;
    private SharedPreferences preferences;
    private String utxoListDates = "";
    private ArrayList<String> utxoPosData;
    private String sumUtxo;

    @Override
    public int getLayoutId() {
        return R.layout.send_one2one_main_page;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        hideRefresh = getIntent().getStringExtra("hideRefresh");
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        base_unit = preferences.getString("base_unit", "mBTC");
        strUnit = preferences.getString("cny_strunit", "CNY");
        wallet_type_to_sign = preferences.getString("wallet_type_to_sign", "");//1-n wallet  --> Direct signature and broadcast
        selectSend = findViewById(R.id.llt_select_wallet);
        LinearLayout linChooseUtxo = findViewById(R.id.lin_choose_utxo);
        tetMoneye = findViewById(R.id.tet_Money);
        tetamount = findViewById(R.id.amount);
        selectSigNumLin = findViewById(R.id.linear_fee_select);
        selectSigNum = findViewById(R.id.fee_select);
        tetWalletname = findViewById(R.id.tet_WalletName);
        textView = findViewById(R.id.tv_send2many);
        editTextComments = findViewById(R.id.comment_edit);
        editAddress = findViewById(R.id.edit_address_one2one);
        bytesCount = findViewById(R.id.byte_count);
        buttonCreate = findViewById(R.id.create_trans_one2one);
        buttonSweep = findViewById(R.id.bn_sweep_one2noe);
        buttonPaste = findViewById(R.id.bn_paste_one2one);
        ImageView imgBack = findViewById(R.id.img_back);
        TextView btnRecommend = findViewById(R.id.btnRecommendFee);
        btnRecommend.setOnClickListener(this);
        linChooseUtxo.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        init();

    }

    //edittext focus change
    private void focusChange() {
        registerKeyBoard();
        editAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                //getFeerate
                getFeerate();
                //button to gray or blue
                changeButton();
            }
        });
        tetamount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                //getFeerate
                getFeerate();
                //button to gray or blue
                changeButton();
            }
        });
        //btc to cny
        TextWatcher1 textWatcher1 = new TextWatcher1();
        tetamount.addTextChangedListener(textWatcher1);
        //cny to btc
        TextWatcher2 textWatcher2 = new TextWatcher2();
        editChangeMoney.addTextChangedListener(textWatcher2);

        TextWatcher3 textWatcher3 = new TextWatcher3();
        textBlocks.addTextChangedListener(textWatcher3);

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
                    getFeerate();
                    //button to gray or blue
                    changeButton();
                }
            }
        };
        //Register layout change monitoring
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    //button to gray or blue
    private void changeButton() {
        if (!TextUtils.isEmpty(editAddress.getText().toString()) && !TextUtils.isEmpty(tetamount.getText().toString())) {
            buttonCreate.setEnabled(true);
            buttonCreate.setBackground(getDrawable(R.drawable.button_bk));
        } else {
            buttonCreate.setEnabled(false);
            buttonCreate.setBackground(getDrawable(R.drawable.button_bk_grey));
        }
    }

    @SuppressLint("DefaultLocale")
    private void init() {
        rxPermissions = new RxPermissions(this);
        selectSend.setOnClickListener(this);
        selectSigNumLin.setOnClickListener(this);
        textView.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonSweep.setOnClickListener(this);
        buttonPaste.setOnClickListener(this);
        dataListName = new ArrayList<>();
        tetTable.setText(base_unit);
        tetWalletTable.setText(base_unit);
        tetStrunit.setText(strUnit);
        Intent intent = getIntent();
        wallet_name = intent.getStringExtra("wallet_name");
        waletType = intent.getStringExtra("wallet_type");
        String sendAdress = intent.getStringExtra("sendAdress");
        String sendmessage = intent.getStringExtra("sendmessage");
        String strNowBtc = intent.getStringExtra("strNowBtc");
        String strNowCny = intent.getStringExtra("strNowCny");
        if (!TextUtils.isEmpty(strNowCny)) {
            if (strNowCny.contains("≈")) {
                testNowCanUse.setText(String.format("%s%s%s", getString(R.string.usable), strNowBtc, strNowCny));
            } else {
                testNowCanUse.setText(String.format("%s%s≈ %s", getString(R.string.usable), strNowBtc, strNowCny));
            }
        } else {
            testNowCanUse.setText(String.format("%s%s", getString(R.string.usable), strNowBtc));
        }
        onlickName = wallet_name;//if onlickName != wallet_name -->home page don't update transaction list
        tetWalletname.setText(wallet_name);
        String sendamount = intent.getStringExtra("sendamount");
        editAddress.setText(sendAdress);
        if (!TextUtils.isEmpty(sendamount)) {
            String amount = sendamount.substring(0, sendamount.indexOf(" "));
            tetamount.setText(String.format("%s", amount));
            if (sendamount.contains("(")) {
                String allCNY = sendamount.substring(sendamount.indexOf("(") + 1);
                String strCNY = allCNY.substring(0, allCNY.indexOf(" "));
                editChangeMoney.setText(strCNY);
            } else {
                try {
                    PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", strAmount);
                    editChangeMoney.setText(money.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        if (!TextUtils.isEmpty(sendmessage)) {
            editTextComments.setText(String.format("%s", sendmessage));
        } else {
            editTextComments.setText(String.format("%s", ""));
        }

        //fee
        getFeeamont();
        //InputMaxTextNum
        setEditTextComments();
        payAddressMore();
        //edittext focus change
        focusChange();

    }

    private void getFeeamont() {
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
                String strMax = strMaxTemp.split("\\.", 2)[0];
                tjFee = Float.parseFloat(strMax);
                intmaxFee = Float.parseFloat(strMax);//fee
                recommendFee = (int) (intmaxFee * 10000);//Current progress
                seekBar.setMax((int) (intmaxFee * 20000));
                seekBar.setProgress(recommendFee);
                textBlocks.setText(String.valueOf(intmaxFee));
            }
            seekbarLatoutup();
        }
    }

    private void seekbarLatoutup() {
        seekBar.setOnSeekBarChangeListener(new IndicatorSeekBar.OnIndicatorSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 1) {
                    intmaxFee = 1;
                    textBlocks.setText(String.format("%s", intmaxFee));
                } else {
                    intmaxFee = Float.parseFloat(String.valueOf(seekBar.getProgress())) / 10000;
                    textBlocks.setText(String.format("%s", intmaxFee));
                }
                //getFeerate
                getFeerate();
            }
        });
    }

    @Override
    public void initData() {
        //get pay address
        mGeneratecode();
    }

    private void setEditTextComments() {
        editTextComments.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bytesCount.setText(String.format(Locale.CHINA, "%d/20", input.length()));
                if (input.length() > 19) {
                    Toast.makeText(SendOne2OneMainPageActivity.this, R.string.moreinput_text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showPopupSelectWallet() {
        //check address
        showDialogs(SendOne2OneMainPageActivity.this, R.layout.select_send_wallet_popwindow);

    }

    private void showDialogs(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        dialogBtom = new Dialog(context, R.style.dialog);
        //cancel dialog
        view.findViewById(R.id.cancel_select_wallet).setOnClickListener(v -> {
            dialogBtom.cancel();
        });
        view.findViewById(R.id.bn_select_wallet).setOnClickListener(v -> {
            try {
                Daemon.commands.callAttr("load_wallet", wallet_name);
                PyObject selectWallet = Daemon.commands.callAttr("select_wallet", wallet_name);
                if (selectWallet != null) {
                    Log.i("select_wallet", "select_wallet+++: " + selectWallet.toString());
                    Gson gson = new Gson();
                    MainNewWalletBean mainWheelBean = gson.fromJson(selectWallet.toString(), MainNewWalletBean.class);
                    String balanceC = mainWheelBean.getBalance();
                    if (!TextUtils.isEmpty(balanceC)) {
                        if (balanceC.contains("(")) {
                            String strNowBtc = balanceC.substring(0, balanceC.indexOf("("));
                            String strNowCny = balanceC.substring(balanceC.indexOf("(") + 1, balanceC.indexOf(")"));
                            if (strNowCny.contains("≈")) {
                                testNowCanUse.setText(String.format("%s%s%s", getString(R.string.usable), strNowBtc, strNowCny));
                            } else {
                                testNowCanUse.setText(String.format("%s%s≈ %s", getString(R.string.usable), strNowBtc, strNowCny));
                            }
                        } else {
                            testNowCanUse.setText(String.format("%s%s", getString(R.string.usable), balanceC));
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            getFeerate();
            tetWalletname.setText(wallet_name);
            dialogBtom.cancel();
        });
        RecyclerView recyPayaddress = view.findViewById(R.id.recy_payAdress);
        choosePayAddressAdapetr = new ChoosePayAddressAdapter(SendOne2OneMainPageActivity.this, dataListName);
        recyPayaddress.setAdapter(choosePayAddressAdapetr);
        recyclerviewOnclick();
        dialogBtom.setContentView(view);
        Window window = dialogBtom.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtom.show();
    }

    //getMorepayAddress
    private void payAddressMore() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    get_wallets_list = Daemon.commands.callAttr("list_wallets");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (get_wallets_list != null) {
                    String toString = get_wallets_list.toString();
                    JSONArray jsons = JSONObject.parseArray(toString);
                    for (int i = 0; i < jsons.size(); i++) {
                        Map jsonToMap = (Map) jsons.get(i);
                        Set<String> keySets = jsonToMap.keySet();
                        Iterator<String> ki = keySets.iterator();
                        AddressEvent addressEvent = new AddressEvent();
                        while (ki.hasNext()) {
                            //get key
                            String key = ki.next();
                            String value = jsonToMap.get(key).toString();
                            addressEvent.setName(key);
                            addressEvent.setType(value);
                            dataListName.add(addressEvent);
                        }
                    }
                }

            }
        }, 200);

    }

    private void recyclerviewOnclick() {
        choosePayAddressAdapetr.setmOnItemClickListener(new ChoosePayAddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                wallet_name_pos = position;
                wallet_name = dataListName.get(position).getName();
                waletType = dataListName.get(position).getType();
                wallet_type_to_sign = waletType;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llt_select_wallet:
                //check address
                showPopupSelectWallet();
                break;
            case R.id.linear_fee_select:
                if (showSeek) {
                    selectSigNum.setImageDrawable(getDrawable(R.drawable.jiantou_up));
                    linearShow.setVisibility(View.VISIBLE);
                    showSeek = false;
                } else {
                    selectSigNum.setImageDrawable(getDrawable(R.drawable.jiantou));
                    linearShow.setVisibility(View.GONE);
                    showSeek = true;
                }
                break;
            case R.id.tv_send2many:
                Intent intent = new Intent(this, Send2ManyActivity.class);
                intent.putExtra("wallet_name", wallet_name);
                intent.putExtra("wallet_type", waletType);
                intent.putExtra("hideRefresh", hideRefresh);
                startActivity(intent);
                break;
            case R.id.create_trans_one2one:
                straddress = editAddress.getText().toString();
                strAmount = tetamount.getText().toString();

                if (TextUtils.isEmpty(straddress)) {
                    Toast.makeText(this, R.string.input_address, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strAmount)) {
                    Toast.makeText(this, R.string.inoutnum, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(errorMessage)) {
                    //creatTrnsaction
                    mCreatTransaction();
                } else {
                    if (errorMessage.contains("invalid bitcoin address")) {
                        Toast.makeText(this, getString(R.string.changeaddress), Toast.LENGTH_LONG).show();
                    } else if (errorMessage.contains("Insufficient funds")) {
                        mToast(getString(R.string.wallet_insufficient));
                    } else if (errorMessage.contains("Please use unconfirmed coins")) {
                        Toast.makeText(this, getString(R.string.please_open_unconfirmed), Toast.LENGTH_LONG).show();
                    } else if (errorMessage.contains("Please broadcast the parent tx")) {
                        Toast.makeText(this, getString(R.string.broad_parent), Toast.LENGTH_LONG).show();
                    } else {
                        mToast(errorMessage);
                    }
                }

                break;
            case R.id.bn_sweep_one2noe:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(this, CaptureActivity.class);
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);
                                config.setShake(true);
                                config.setDecodeBarCode(false);
                                config.setFullScreenScan(true);
                                config.setShowAlbum(false);
                                config.setShowbottomLayout(false);
                                intent2.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent2, REQUEST_CODE);
                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.bn_paste_one2one:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData data = clipboard.getPrimaryClip();
                    if (data != null && data.getItemCount() > 0) {
                        editAddress.setText(data.getItemAt(0).getText());
                        //getFeerate
                        getFeerate();
                        //button to gray or blue
                        changeButton();
                    }
                }
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.btnRecommendFee:
                intmaxFee = tjFee;
                if (!TextUtils.isEmpty(tetMoneye.getText().toString())) {
                    //getFeerate
                    getFeerate();
                }
                seekBar.setProgress(recommendFee);
                textBlocks.setText(String.valueOf(intmaxFee));
                break;
            case R.id.lin_choose_utxo:
                if (!TextUtils.isEmpty(tetamount.getText().toString())) {
                    Intent intent1 = new Intent(SendOne2OneMainPageActivity.this, ChooseUtxoActivity.class);
                    intent1.putExtra("sendNum", tetamount.getText().toString());
                    intent1.putStringArrayListExtra("utxoPositionData",utxoPosData);
                    intent1.putExtra("sumUtxoTotal",sumUtxo);
                    startActivityForResult(intent1, 1);
                } else {
                    mToast(getString(R.string.please_input_send_num));
                }
                break;
            default:
        }
    }

    //creat transaction
    private void mCreatTransaction() {
        straddress = editAddress.getText().toString();
        strAmount = tetamount.getText().toString();
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> pramas = new HashMap<>();
        pramas.put(straddress, strAmount);
        arrayList.add(pramas);
        strComment = editTextComments.getText().toString();
        String strPramas = new Gson().toJson(arrayList);
        PyObject mktx;
        try {
            mktx = Daemon.commands.callAttr("mktx", strPramas, strComment);

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Insufficient funds")) {
                mToast(getString(R.string.wallet_insufficient));
            } else {
                mToast(getString(R.string.changeaddress));
            }
            return;
        }
        Log.i("CreatTransaction", "mCreatTransaction: " + mktx);
        if (mktx != null) {
            String jsonObj = mktx.toString();
            Gson gson = new Gson();
            GetAddressBean getAddressBean = gson.fromJson(jsonObj, GetAddressBean.class);
            String rowtx = getAddressBean.getTx();
            if (!TextUtils.isEmpty(rowtx)) {
                if (wallet_type_to_sign.contains("1-") && TextUtils.isEmpty(hideRefresh)) {
                    try {
                        PyObject txInfoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", rowtx);
                        gson = new Gson();
                        GetnewcreatTrsactionListBean getnewcreatTrsactionListBean = gson.fromJson(txInfoFromRaw.toString(), GetnewcreatTrsactionListBean.class);
                        outputAddr = getnewcreatTrsactionListBean.getOutputAddr();
                        fee = getnewcreatTrsactionListBean.getFee();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (onlickName.equals(wallet_name)) {
                        EventBus.getDefault().post(new FirstEvent("22"));
                    } else {
                        EventBus.getDefault().post(new MainpageWalletEvent("22", wallet_name_pos));
                    }
                    //1-n wallet  --> Direct signature and broadcast
                    if ("1-1".equals(wallet_type_to_sign) && Ble.getInstance().getConnetedDevices().size() != 0) {
                        String deviceId = Daemon.commands.callAttr("get_device_info").toString().replaceAll("\"", "");
                        SharedPreferences devices = getSharedPreferences("devices", MODE_PRIVATE);
                        String feature = devices.getString(deviceId, "");
                        if (!Strings.isNullOrEmpty(feature)) {
                            HardwareFeatures features = HardwareFeatures.objectFromData(feature);
                            if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(features.getBleName())) {
                                EventBus.getDefault().postSticky(new HandlerEvent());
                            }
                        }

                    }
                    CommunicationModeSelector.runnables.clear();
                    CommunicationModeSelector.runnables.add(runnable);
                    Intent intent1 = new Intent(this, CommunicationModeSelector.class);
                    intent1.putExtra("tag", TAG);
                    intent1.putExtra("extras", rowtx);
                    startActivity(intent1);

                } else {
                    if (onlickName.equals(wallet_name)) {
                        EventBus.getDefault().post(new FirstEvent("22"));
                    } else {
                        EventBus.getDefault().post(new MainpageWalletEvent("22", wallet_name_pos));
                    }
                    Intent intent = new Intent(SendOne2OneMainPageActivity.this, TransactionDetailsActivity.class);
                    intent.putExtra("tx_hash", rowtx);
                    intent.putExtra("keyValue", "A");
                    intent.putExtra("is_mine", true);
                    intent.putExtra("strwalletType", waletType);
                    if (!TextUtils.isEmpty(hideRefresh)) {
                        intent.putExtra("hideWallet", "hideWallet");
                    }
                    intent.putExtra("txCreatTrsaction", rowtx);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    //get pay address
    private void mGeneratecode() {
        PyObject walletAddressShowUi = null;
        try {
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (walletAddressShowUi != null) {
            String strCode = walletAddressShowUi.toString();
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            payAddress = getCodeAddressBean.getAddr();
        }
    }


    private Runnable runnable = this::gotoConfirmOnHardware;

    private void gotoConfirmOnHardware() {
        Intent intentCon = new Intent(SendOne2OneMainPageActivity.this, ConfirmOnHardware.class);
        intentCon.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable("output", outputAddr);
        bundle.putString("pay_address", payAddress);
        bundle.putString("fee", fee);
        intentCon.putExtra("outputs", bundle);
        startActivity(intentCon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            // Scan QR code / barcode return
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                Log.i("sendScanData", "on------: " + content);
                if (!TextUtils.isEmpty(content)) {
                    PyObject parsePr = Daemon.commands.callAttr("parse_pr", content);
//                    Log.i("sendScanData", "on------: " + parsePr);
                    if (!TextUtils.isEmpty(parsePr.toString())) {
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(parsePr.toString());
                            int type = jsonObject.getInt("type");
                            Gson gson = new Gson();
                            if (type == 1) {
                                MainSweepcodeBean mainSweepcodeBean = gson.fromJson(parsePr.toString(), MainSweepcodeBean.class);
                                MainSweepcodeBean.DataBean listData = mainSweepcodeBean.getData();
                                String address = listData.getAddress();
                                String sendAmount = listData.getAmount();
                                String message = listData.getMessage();
                                editAddress.setText(address);
                                if (!TextUtils.isEmpty(sendAmount)) {
                                    String amount = sendAmount.substring(0, sendAmount.indexOf(" "));
                                    tetamount.setText(amount);
                                }
                                editTextComments.setText(message);

                            } else {
                                mToast(getString(R.string.address_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //getFeerate
                        getFeerate();
                        //button to gray or blue
                        changeButton();
                    }
                }
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            String language = preferences.getString("language", "Chinese");
            assert data != null;
            String chooseNum = data.getStringExtra("chooseNum");
            utxoListDates = data.getStringExtra("listDates");//selected utxo data
            utxoPosData = data.getStringArrayListExtra("UtxoPosData");
            sumUtxo = data.getStringExtra("sumUtxo");//sum utxo
            //getFeerate
            getFeerate();
            if ("English".equals(language)) {
                textChooseNum.setText(String.format("%s%s", getString(R.string.selected), chooseNum));
            } else {
                textChooseNum.setText(String.format("%s%s%s", getString(R.string.selected), chooseNum, getString(R.string.ge)));
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutChangeListener);
        } else {
            getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(mLayoutChangeListener);
        }
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
        strAmount = tetamount.getText().toString();
        if (!TextUtils.isEmpty(strAmount)) {
            BigDecimal amount = new BigDecimal(strAmount);
            try {
                pyObject = Daemon.commands.callAttr("get_exchange_currency", "base", amount);

                Log.i("pyObjectcommands", "---------: " + pyObject);
            } catch (Exception e) {
                e.printStackTrace();
                mToast(e.getMessage());
            }
            if (pyObject != null) {
                editChangeMoney.setText(pyObject.toString());
            }
        } else {
            editChangeMoney.setText("");
        }
    }

    //get fee num
    private void getFeerate() {
        straddress = editAddress.getText().toString();
        strAmount = tetamount.getText().toString();
        strComment = editTextComments.getText().toString();
        if (!TextUtils.isEmpty(straddress) && !TextUtils.isEmpty(strAmount)) {
            ArrayList<Map<String, String>> arrayList = new ArrayList<>();
            Map<String, String> pramas = new HashMap<>();
            pramas.put(straddress, strAmount);
            arrayList.add(pramas);
            String strPramas = new Gson().toJson(arrayList);
            PyObject getFeeByFeeRate = null;
            try {
                if (!TextUtils.isEmpty(utxoListDates)) {
                    Log.i("utxoListDates", "getFeerate:--- " + utxoListDates);
                    getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, strComment, intmaxFee, new Kwarg("customer", utxoListDates));
                } else {
                    getFeeByFeeRate = Daemon.commands.callAttr("get_fee_by_feerate", strPramas, strComment, intmaxFee);
                }
                errorMessage = "";
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().contains("invalid bitcoin address")) {
                    mToast(getString(R.string.changeaddress));
                } else if (e.getMessage().contains("Insufficient funds")) {
                    mToast(getString(R.string.wallet_insufficient));
                } else if (e.getMessage().contains("Please use unconfirmed coins")) {
                    if (!errorMessage.contains("Please use unconfirmed coins")) {
                        mToast(getString(R.string.please_open_unconfirmed));
                    }
                } else if (errorMessage.contains("Please broadcast the parent tx")) {
                    mToast(getString(R.string.broad_parent));
                } else {
                    mToast(errorMessage);
                }
                errorMessage = e.getMessage();
                return;
            }
            if (getFeeByFeeRate != null) {
                String strnewFee = getFeeByFeeRate.toString();
                Gson gson = new Gson();
                GetsendFeenumBean getsendFeenumBean = gson.fromJson(strnewFee, GetsendFeenumBean.class);
                BigInteger fee = getsendFeenumBean.getFee();
                feeNum = String.valueOf(fee);
                tetMoneye.setText(String.format("%s sat", feeNum));

            }
        }
    }

    //Turn off soft keyboard, lose focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mRootView.setClickable(true);
                    mRootView.setFocusable(true);
                    mRootView.setFocusableInTouchMode(true);
                    mRootView.requestFocusFromTouch();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // Necessary, otherwise all components will not have TouchEvent
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    //Turn off soft keyboard, lose focus
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }

    class TextWatcher1 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            inputType(tetamount, charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            strAmount = editable.toString();
            if (!TextUtils.isEmpty(strAmount)) {
                if (flag) {
                    flag = false;
                    try {
                        pyObject = Daemon.commands.callAttr("get_exchange_currency", "base", strAmount);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (pyObject != null) {
                        editChangeMoney.setText(pyObject.toString());
                    }
                } else {
                    flag = true;
                }
            } else {
                if (flag) {
                    flag = false;
                    editChangeMoney.setText("");
                } else {
                    flag = true;
                }
            }
        }
    }

    class TextWatcher2 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            inputType(editChangeMoney, charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(editable.toString())) {
                if (flag) {
                    flag = false;
                    try {
                        pyObject = Daemon.commands.callAttr("get_exchange_currency", "fiat", editable.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (pyObject != null) {
                        tetamount.setText(pyObject.toString());
                    }
                } else {
                    flag = true;
                }
            } else {
                if (flag) {
                    flag = false;
                    tetamount.setText("");
                } else {
                    flag = true;
                }
            }
        }
    }

    class TextWatcher3 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            inputType(textBlocks, charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(editable.toString())) {
                intmaxFee = Float.parseFloat(editable.toString());
            } else {
                intmaxFee = 0;
            }
            seekBar.setProgress((int) (intmaxFee * 10000));
            //getFeerate
            getFeerate();
        }
    }

    private void inputType(EditText edittext, CharSequence s) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 7) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 8);
                edittext.setText(s);
                edittext.setSelection(s.length());
            }
        }
        if (".".equals(s.toString().trim().substring(0))) {
            s = "0" + s;
            edittext.setText(s);
            edittext.setSelection(2);
        }
        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!".".equals(s.toString().substring(1, 2))) {
                edittext.setText(s.subSequence(0, 1));
                edittext.setSelection(1);
            }
        }
    }
}




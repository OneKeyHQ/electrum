package org.haobtc.onekey.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.activities.transaction.DeatilMoreAddressActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.AddspeedNewtrsactionBean;
import org.haobtc.onekey.bean.TransactionInfoBean;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.ScanCheckDetailBean;
import org.haobtc.onekey.event.CheckReceiveAddress;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.HandlerEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.IndicatorSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class TransactionDetailsActivity extends BaseActivity {

    public static final String TAG_HIDE_WALLET = "TAG_HIDE_WALLET_TRANSACTION";
    @BindView(R.id.img_progressone)
    ImageView imgProgressone;
    @BindView(R.id.img_progressthree)
    ImageView imgProgressthree;
    @BindView(R.id.img_progressfour)
    ImageView imgProgressfour;
    @BindView(R.id.tet_Trone)
    TextView tetTrone;
    @BindView(R.id.tet_Trthree)
    TextView tetTrthree;
    @BindView(R.id.tet_Trfore)
    TextView tetTrfore;
    @BindView(R.id.tv_in_tb2)
    TextView tvInTb2;
    @BindView(R.id.tet_getMoneyaddress)
    TextView tetGetMoneyaddress;
    @BindView(R.id.tet_payAddress)
    TextView tetPayAddress;
    @BindView(R.id.textView14)
    TextView textView14;
    @BindView(R.id.textView15)
    TextView textView15;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.textView20)
    TextView textView20;
    @BindView(R.id.sig_trans)
    Button sigTrans;
    public static final String TAG = "com.bixin.wallet.activities.TransactionDetailsActivity";
    @BindView(R.id.tet_content)
    TextView tetContent;
    @BindView(R.id.tet_state)
    TextView tetState;
    @BindView(R.id.tet_trsactionHash)
    TextView tetTrsactionHash;
    @BindView(R.id.tet_trsactionTime)
    TextView tetTrsactionTime;
    @BindView(R.id.lin_getMoreaddress)
    LinearLayout linGetMoreaddress;
    @BindView(R.id.tet_addressNum)
    TextView tetAddressNum;
    @BindView(R.id.tet_confirm)
    TextView tetConfirm;
    @BindView(R.id.lin_tractionHash)
    LinearLayout linTractionHash;
    @BindView(R.id.lin_tractionTime)
    LinearLayout linTractionTime;
    @BindView(R.id.tet_grive)
    TextView tetGrive;
    @BindView(R.id.tet_addSpeed)
    TextView tetAddSpeed;
    @BindView(R.id.lin_fee)
    LinearLayout linFee;
    @BindView(R.id.lin_payAddress)
    LinearLayout linPayAddress;
    @BindView(R.id.linearSignStatus)
    LinearLayout linearSignStatus;
    @BindView(R.id.tet_payAddressNum)
    TextView tetPayAddressNum;
    @BindView(R.id.btn_share)
    Button btnShare;
    private String keyValue;
    private String txHash;
    private String listType;
    private String publicTrsation;
    private String jsondefGet;
    private String rawtx;
    private String strParse;
    private String language;
    private boolean isMine;
    private PyObject getRbfStatus;
    private String strwalletType;
    private SharedPreferences preferences;
    private String txid;
    private boolean canBroadcast;
    private SharedPreferences.Editor edit;
    private String newFeerate;
    private AlertDialog alertDialog;
    private String fee;
    ArrayList<TransactionInfoBean.OutputAddrBean> outputAddr;
    public static String signedRawTx;
    private String signTransction;
    private boolean setRbf;
    private List<TransactionInfoBean.InputAddrBean> inputAddr;
    private List<ScanCheckDetailBean.DataBean.OutputAddrBean> outputAddrScan;
    private List<ScanCheckDetailBean.DataBean.InputAddrBean> inputAddrScan;
    private String unConfirmStatus;
    private String hideWallet = "";
    private String walletTypeToSign;
    private boolean braodStatus = false;
    private String minIntFees;
    private int minPro;
    private boolean buttonStatus = true;
    private int minProRate;
    private int recommendProRate;
    private String feeForChildReceive;
    private String txStatus;
    private String listTxStatus = "";
    private float feeForChild;


    @Override
    public int getLayoutId() {
        return R.layout.trans_details;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        language = preferences.getString("language", "");
        setRbf = preferences.getBoolean("set_rbf", false);//rbf transaction
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("signed_raw_tx"))) {
            signedRawTx = intent.getStringExtra("signed_raw_tx");
            EventBus.getDefault().post(new CheckReceiveAddress("finish_mode_select"));//close sign page
        } else {
            hideWallet = intent.getStringExtra("hideWallet");//hide wallet transaction
            publicTrsation = intent.getStringExtra("txCreatTrsaction");
            keyValue = intent.getStringExtra("keyValue");//Judge which interface to jump in from
            txHash = intent.getStringExtra("tx_hash");
            listType = intent.getStringExtra("listType");
            unConfirmStatus = intent.getStringExtra("unConfirmStatus");//Manually change to the status to be confirmed
            signTransction = intent.getStringExtra("signTransction");//from SignActivity
            strParse = intent.getStringExtra("strParse");
            String dataTime = intent.getStringExtra("dataTime");
            strwalletType = intent.getStringExtra("strwalletType");
            tetTrsactionTime.setText(dataTime);
            listTxStatus = getIntent().getStringExtra("listTxStatus");
            setSpeedBtn();//add speed button show or hide
        }
        isMine = intent.getBooleanExtra("is_mine", false);//is_mine -->recevid or send
    }

    @Override
    public void initData() {
        if (!TextUtils.isEmpty(signedRawTx)) {
            jsonDetailData(signedRawTx);
            signedRawTx = "";
            return;
        }
        //is_mine -->recevid or send
        if (isMine) {
            tvInTb2.setText(R.string.sendetail);
            linFee.setVisibility(View.VISIBLE);
        } else {
            linearSignStatus.setVisibility(View.GONE);
            tvInTb2.setText(R.string.recevid);
        }
        if (!TextUtils.isEmpty(keyValue)) {
            switch (keyValue) {
                case "A":
                    //create success
                    mCreataSuccsesCheck();
                    break;
                case "B":
                    if ("history".equals(listType)) {
                        //is_mine -->recevid or send
                        if (isMine) {
                            tvInTb2.setText(R.string.sendetail);
                        } else {
                            linearSignStatus.setVisibility(View.GONE);
                            tvInTb2.setText(R.string.recevid);
                        }
                        //history transaction detail
                        trsactionDetail();
                    } else if ("scan".equals(listType)) {
                        linearSignStatus.setVisibility(View.GONE);
                        tvInTb2.setText(R.string.recevid);
                        scanDataDetailMessage();
                    } else {
                        tvInTb2.setText(R.string.sendetail);
                        //create success
                        mCreataSuccsesCheck();
                    }
                    break;
                case "Sign":
                    jsonDetailData(signTransction);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + keyValue);
            }
        }
    }

    //Show RBF button or not
    private void setSpeedBtn() {
        try {
            getRbfStatus = Daemon.commands.callAttr("get_rbf_or_cpfp_status", txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getRbfStatus != null) {
            Log.i("getRbfStatus", "setSpeedBtn--: " + getRbfStatus);
            if (setRbf) {
                if (getRbfStatus.toString().contains("{}")) {
                    tetAddSpeed.setVisibility(View.GONE);//rbf speed Whether to display
                } else {
                    if (!"scan".equals(listType)) {
                        Log.i("getRbfStatus", "listTxStatus--: " + listTxStatus);
                        if ("Unconfirmed".equals(listTxStatus)) {
                            tetAddSpeed.setVisibility(View.VISIBLE);
                        } else {
                            tetAddSpeed.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    //trsaction detail
    private void trsactionDetail() {
        if (!TextUtils.isEmpty(txHash)) {
            PyObject getTxInfo;
            try {
                getTxInfo = Daemon.commands.callAttr("get_tx_info", txHash);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (getTxInfo != null) {
                jsondefGet = getTxInfo.toString();
                jsonDetailData(jsondefGet);
            }

        }
    }

    //creat succses check
    private void mCreataSuccsesCheck() {
        //get trsaction list content
        if (!TextUtils.isEmpty(publicTrsation)) {
            PyObject txInfoFromRaw = null;
            try {
                txInfoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", publicTrsation);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (txInfoFromRaw != null) {
                jsondefGet = txInfoFromRaw.toString();
                jsonDetailData(jsondefGet);
            }
        }
    }

    //intent ->histry or create
    @SuppressLint("DefaultLocale")
    private void jsonDetailData(String detail) {
        TransactionInfoBean transactionInfoBean;
        try {
            Gson gson = new Gson();
            transactionInfoBean = gson.fromJson(detail, TransactionInfoBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return;
        }
        String amount = transactionInfoBean.getAmount();
        fee = transactionInfoBean.getFee();
        String description = transactionInfoBean.getDescription();
        txStatus = transactionInfoBean.getTxStatus();
        outputAddr = transactionInfoBean.getOutputAddr();
        List<Integer> signStatus = transactionInfoBean.getSignStatus();
        inputAddr = transactionInfoBean.getInputAddr();
        txid = transactionInfoBean.getTxid();
        txHash = txid;
        rawtx = transactionInfoBean.getTx();
        canBroadcast = transactionInfoBean.isCanBroadcast();
        edit.putString("signedRowTransaction", rawtx);
        edit.apply();
        if (inputAddr.size() != 0) {
            String addrInput = inputAddr.get(0).getAddr();
            tetPayAddress.setText(addrInput);
        }
        //trsaction hash
        tetTrsactionHash.setText(txid);
        //input address num
        int size = outputAddr.size();
        if (size != 1) {
            if ("English".equals(language)) {
                tetAddressNum.setText(String.format("%s%d", getString(R.string.wait), size));
            } else {
                tetAddressNum.setText(String.format("%s%d%s", getString(R.string.wait), size, getString(R.string.ge)));
            }
        } else {
            tetAddressNum.setVisibility(View.GONE);
        }
        if (inputAddr.size() != 1) {
            if ("English".equals(language)) {
                tetPayAddressNum.setText(String.format("%s%d", getString(R.string.wait), inputAddr.size()));
            } else {
                tetPayAddressNum.setText(String.format("%s%d%s", getString(R.string.wait), inputAddr.size(), getString(R.string.ge)));
            }
        } else {
            tetPayAddressNum.setVisibility(View.GONE);
        }

        if (size != 0) {
            //output_address
            String addr = outputAddr.get(0).getAddr();
            tetGetMoneyaddress.setText(addr);
        }

        if (signStatus != null) {
            Integer integer = signStatus.get(0);
            Integer integer1 = signStatus.get(1);
            String strNum = integer + "/" + integer1;
            textView20.setText(strNum);
        }
        //Transfer accounts num
        if (!TextUtils.isEmpty(amount)) {
            if (amount.contains("-")) {
                String replaceAmont = amount.replace("-", "");
                textView14.setText(String.format("-%s", replaceAmont));
            } else {
                if (isMine) {
                    textView14.setText(String.format("-%s", amount));
                } else {
                    Log.i("buttonStatus", "jsonDetailData: " + buttonStatus);
                    if (!buttonStatus) { //speed up change "-"
                        textView14.setText(String.format("-%s", amount));
                    } else {
                        textView14.setText(String.format("+%s", amount));
                    }
                }
            }
        }
        //Miner's fee
        if (!TextUtils.isEmpty(fee)) {
            textView15.setText(fee);
        }
        //Remarks
        tetContent.setText(description);
        if (!TextUtils.isEmpty(txStatus)) {
            //judge state
            judgeState(txStatus);
        }

    }

    //scan get
    @SuppressLint("DefaultLocale")
    private void scanDataDetailMessage() {
        Gson gson = new Gson();
        ScanCheckDetailBean scanCheckDetailBean = gson.fromJson(strParse, ScanCheckDetailBean.class);
        ScanCheckDetailBean.DataBean scanListdata = scanCheckDetailBean.getData();
        String amount = scanListdata.getAmount();
        String fee = scanListdata.getFee();
        String description = scanListdata.getDescription();
        txStatus = scanListdata.getTxStatus();
        inputAddrScan = scanListdata.getInputAddr();
        outputAddrScan = scanListdata.getOutputAddr();
        ScanCheckDetailBean.DataBean data = scanCheckDetailBean.getData();

        if (inputAddrScan.size() != 0) {
            String addrInput = inputAddrScan.get(0).getAddr();
            tetPayAddress.setText(addrInput);
        }
        List<Integer> signStatusMes = scanListdata.getSignStatus();
        String txid = scanListdata.getTxid();
        rawtx = scanListdata.getTx();
        //trsaction hash
        tetTrsactionHash.setText(txid);
        //input address num
        int size = outputAddrScan.size();
        if (size != 1) {
            if ("English".equals(language)) {
                tetAddressNum.setText(String.format("%s%d", getString(R.string.wait), size));
            } else {
                tetAddressNum.setText(String.format("%s%d%s", getString(R.string.wait), size, getString(R.string.ge)));
            }
        } else {
            tetAddressNum.setVisibility(View.GONE);
        }
        if (inputAddr != null && inputAddr.size() != 0) {
            if (inputAddr.size() != 1) {
                if ("English".equals(language)) {
                    tetPayAddressNum.setText(String.format("%s%d", getString(R.string.wait), inputAddr.size()));
                } else {
                    tetPayAddressNum.setText(String.format("%s%d%s", getString(R.string.wait), inputAddr.size(), getString(R.string.ge)));
                }
            } else {
                tetPayAddressNum.setVisibility(View.GONE);
            }
        }

        //output_address
        if (outputAddrScan.size() != 0) {
            String addr = outputAddrScan.get(0).getAddr();
            tetGetMoneyaddress.setText(addr);
        }
        if ("Unconfirmed".equals(txStatus)) {
            tetAddSpeed.setVisibility(View.VISIBLE);
        } else {
            tetAddSpeed.setVisibility(View.GONE);
        }
        if (signStatusMes != null) {
            Integer integer = signStatusMes.get(0);
            Integer integer1 = signStatusMes.get(1);
            if (integer1 == 1) {
                walletTypeToSign = "1-1";
            }
            String strNum = integer + "/" + integer1;
            textView20.setText(strNum);
        }
        //Transfer accounts num
        if (!TextUtils.isEmpty(amount)) {
            if (!"Transaction unrelated to your wallet".equals(amount)) {
                if (amount.contains("-")) {
                    String replaceAmont = amount.replace("-", "");
                    textView14.setText(String.format("-%s", replaceAmont));
                    tvInTb2.setText(R.string.sendetail);
                } else {
                    textView14.setText(String.format("+%s", amount));
                    tvInTb2.setText(R.string.recevid);
                }
            } else {
                textView14.setText(amount);
                tvInTb2.setText(R.string.trans_details);
                sigTrans.setVisibility(View.GONE);
                tetGrive.setVisibility(View.GONE);
            }

        }
        //Miner's fee
        if (!TextUtils.isEmpty(fee)) {
            textView15.setText(fee);
        }
        //Remarks
        tetContent.setText(description);
        if (!TextUtils.isEmpty(txStatus)) {
            //judge state
            judgeState(txStatus);
        }

    }

    //judge state
    private void judgeState(String txStatus) {
        if ("broadcast_complete".equals(unConfirmStatus)) {
            if (!braodStatus) {
                broadcastStatus();//sendOne2OnePageActivity  1-n wallet -->sign and broadcast ，Modify status manually
                braodStatus = false;
            }

        } else {
            //transaction state
            if ("Unconfirmed".equals(txStatus)) {//Unconfirmed
                tetState.setText(R.string.waitchoose);
                sigTrans.setText(R.string.check_trsaction);
                imgProgressone.setVisibility(View.GONE);
                imgProgressthree.setVisibility(View.GONE);
                imgProgressfour.setVisibility(View.VISIBLE);
                //text color
                tetTrthree.setTextColor(getColor(R.color.button_bk_disableok));
                tetTrfore.setTextColor(getColor(R.color.button_bk_disableok));
                //transaction hash and time
                linTractionHash.setVisibility(View.VISIBLE);
                linTractionTime.setVisibility(View.GONE);
                linFee.setVisibility(View.VISIBLE);

            } else if (txStatus.contains("confirmations")) {//Confirmed
                tetState.setText(R.string.completed);
                sigTrans.setText(R.string.check_trsaction);
                imgProgressone.setVisibility(View.GONE);
                imgProgressthree.setVisibility(View.GONE);
                imgProgressfour.setVisibility(View.VISIBLE);
                //Number of judgment confirmation
                String strConfirl = txStatus.replaceAll(" confirmations", "");
                BigDecimal bignum1 = new BigDecimal(strConfirl);
                BigDecimal bigDecimal = new BigDecimal(100);
                int mathMax = bignum1.compareTo(bigDecimal);
                if (mathMax > 0) {
                    tetConfirm.setText(String.format("%s%s", getString(R.string.confirmnum), ">100"));
                } else {
                    tetConfirm.setText(String.format("%s%s", getString(R.string.confirmnum), strConfirl));
                }

                tetTrthree.setTextColor(getColor(R.color.button_bk_disableok));
                tetTrfore.setTextColor(getColor(R.color.button_bk_disableok));
                //transaction hash and time
                linTractionHash.setVisibility(View.VISIBLE);
                linTractionTime.setVisibility(View.VISIBLE);
                if (isMine) {
                    linFee.setVisibility(View.VISIBLE);
                } else {
                    linFee.setVisibility(View.GONE);
                }
            } else if (txStatus.contains("Unsigned")) {//unsigned
                linPayAddress.setVisibility(View.VISIBLE);
                tetState.setText(R.string.unsigned);
                sigTrans.setText(R.string.signature_trans);
            } else if (txStatus.contains("Signed") || txStatus.contains("Local") || canBroadcast) {//signed
                tetState.setText(R.string.wait_broadcast);
                sigTrans.setText(R.string.broadcast);
                tetGrive.setVisibility(View.VISIBLE);
                linPayAddress.setVisibility(View.GONE);
                //progress
                imgProgressone.setVisibility(View.GONE);
                imgProgressthree.setVisibility(View.VISIBLE);
                imgProgressfour.setVisibility(View.GONE);
                tetTrthree.setTextColor(getColor(R.color.button_bk_disableok));

            } else if (txStatus.contains("Partially signed")) {//
                tetState.setText(R.string.transaction_waitting);
                sigTrans.setText(R.string.signature_trans);
                //progress
                imgProgressone.setVisibility(View.VISIBLE);
                imgProgressthree.setVisibility(View.GONE);
                imgProgressfour.setVisibility(View.GONE);
            }
        }
    }

    private Runnable runnable = this::gotoConfirmOnHardware;

    private void gotoConfirmOnHardware() {
        String strPayAddress = tetPayAddress.getText().toString();
        Intent intentCon = new Intent(TransactionDetailsActivity.this, ConfirmOnHardware.class);
        intentCon.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable("output", outputAddr);
        bundle.putString("pay_address", strPayAddress);
        bundle.putString("fee", fee);
        intentCon.putExtra("outputs", bundle);
        startActivity(intentCon);
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_share, R.id.lin_getMoreaddress, R.id.tet_addSpeed, R.id.sig_trans, R.id.lin_payAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_share:
                Intent intent = new Intent(TransactionDetailsActivity.this, ShareOtherActivity.class);
                intent.putExtra("rowTrsaction", txHash);
                intent.putExtra("rowTx", rawtx);
                startActivity(intent);
                break;
            case R.id.sig_trans:
                //sign technological process
                signProcess();
                break;
            case R.id.lin_getMoreaddress:
                //transaction detail or create success
                Intent intent1 = new Intent(TransactionDetailsActivity.this, DeatilMoreAddressActivity.class);
                intent1.putExtra("plusNum",textView14.getText().toString());
                if (outputAddr != null) {
                    intent1.putExtra("jsondef_get", (Serializable) outputAddr);
                } else {
                    intent1.putExtra("jsondef_getScan", (Serializable) outputAddrScan);
                }
                startActivity(intent1);
                break;
            case R.id.tet_addSpeed:
                if (!TextUtils.isEmpty(getRbfStatus.toString())) {
                    if (getRbfStatus.toString().contains("rbf")) {
                        ifHaveRbf();
                    } else if (getRbfStatus.toString().contains("cpfp")) {
                        receiveAddSpeed();
                    }
                }
                break;
            case R.id.lin_payAddress:
                Intent intent2Pay = new Intent(TransactionDetailsActivity.this, DeatilMoreAddressActivity.class);
                intent2Pay.putExtra("plusNum",textView14.getText().toString());
                if (inputAddr != null) {
                    intent2Pay.putExtra("payAddress", (Serializable) inputAddr);
                } else {
                    intent2Pay.putExtra("payAddressScan", (Serializable) inputAddrScan);
                }
                intent2Pay.putExtra("addressType", "pay");
                intent2Pay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2Pay);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void ifHaveRbf() {
        PyObject getRbfFeeInfo = null;
        try {
            getRbfFeeInfo = Daemon.commands.callAttr("get_rbf_fee_info", txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getRbfFeeInfo != null) {
            String strNewfee = getRbfFeeInfo.toString();
            View viewSpeed = LayoutInflater.from(this).inflate(R.layout.add_speed, null, false);
            alertDialog = new AlertDialog.Builder(this).setView(viewSpeed).create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageView imgCancel = viewSpeed.findViewById(R.id.cancel_select_wallet);
            TextView tetNewfee = viewSpeed.findViewById(R.id.tet_Newfee);
            TextView testChangeFee = viewSpeed.findViewById(R.id.test_change_fee);
            IndicatorSeekBar seekBar = viewSpeed.findViewById(R.id.fee_seek_bar);
            try {
                JSONObject jsonObject = new JSONObject(strNewfee);
                String currentFeerate = jsonObject.getString("current_feerate");
                newFeerate = jsonObject.getString("new_feerate");
                String minFees = currentFeerate.substring(0, currentFeerate.indexOf("sat/byte"));
                String singleFee = newFeerate.substring(0, newFeerate.indexOf("."));
                int ingSingle = Integer.parseInt(singleFee);
                if (minFees.contains(".")) {
                    minIntFees = minFees.substring(0, minFees.indexOf("."));
                    minPro = Integer.parseInt(minIntFees);
                } else {
                    minIntFees = minFees.replaceAll(" ", "");
                    minPro = Integer.parseInt(minIntFees);
                }
                int allNum = (ingSingle * 2) - minPro;
                int noePro = ingSingle - minPro;
                seekBar.setMax(allNum * 10000);
                seekBar.setProgress(noePro * 10000);
                testChangeFee.setText(String.format("%s sat/byte", newFeerate));
                BigDecimal bigSingSingle = new BigDecimal(ingSingle);
                createBumpFee(tetNewfee, bigSingSingle);//get tx and fee
                seekbarLatoutup(seekBar, testChangeFee, tetNewfee);//seek bar Listener
                imgCancel.setOnClickListener(v -> {
                    alertDialog.dismiss();
                });
                viewSpeed.findViewById(R.id.btn_add_Speed).setOnClickListener(v -> {
                    confirmedSpeed();
                });
                alertDialog.show();
                //show center
                Window dialogWindow = alertDialog.getWindow();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                p.width = (int) (d.getWidth() * 0.95);
                p.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(p);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveAddSpeed() {
        PyObject getRbfFeeInfo = null;
        try {
            getRbfFeeInfo = Daemon.commands.callAttr("get_cpfp_info", txHash);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("max fee exceeded")) {
                mToast(getString(R.string.fee_exceeded));
            }
            return;
        }
        if (getRbfFeeInfo != null) {
            Log.i("getRbfFeeInfosss", "receiveAddSpeed: " + getRbfFeeInfo.toString());
            String strNewfeeReceive = getRbfFeeInfo.toString();
            View viewSpeed = LayoutInflater.from(this).inflate(R.layout.receive_add_speed, null, false);
            alertDialog = new AlertDialog.Builder(this).setView(viewSpeed).create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageView imgCancel = viewSpeed.findViewById(R.id.cancel_select_wallet);
            TextView tetNewfee = viewSpeed.findViewById(R.id.tet_Newfee);
            TextView testTitle = viewSpeed.findViewById(R.id.test_title);
            testTitle.setText(getString(R.string.receive_speed));
            TextView testTip = viewSpeed.findViewById(R.id.test_tips);
            TextView testChangeFee = viewSpeed.findViewById(R.id.test_change_fee);
            IndicatorSeekBar seekBar = viewSpeed.findViewById(R.id.fee_seek_bar_receive);
            testTip.setText(getString(R.string.receive_speed_tips));
            try {
                JSONObject jsonObject = new JSONObject(strNewfeeReceive);
                feeForChildReceive = jsonObject.getString("fee_for_child");//推荐费
                String parentFeeRate = jsonObject.getString("parent_feerate");//最小费率
                String feeRateForChild = jsonObject.getString("fee_rate_for_child");//推荐费率
                String minFeeRate = parentFeeRate.substring(0, parentFeeRate.indexOf(" "));
                String recommendFeeRate = feeRateForChild.substring(0, feeRateForChild.indexOf(" "));
                if (minFeeRate.contains(".")) {
                    String minIntFee = minFeeRate.substring(0, minFeeRate.indexOf("."));
                    minProRate = Integer.parseInt(minIntFee);
                } else {
                    minProRate = Integer.parseInt(minFeeRate);
                }
                if (recommendFeeRate.contains(".")) {
                    String recommendIntFee = recommendFeeRate.substring(0, recommendFeeRate.indexOf("."));
                    recommendProRate = Integer.parseInt(recommendIntFee);
                } else {
                    recommendProRate = Integer.parseInt(recommendFeeRate);
                }
                //The recommended rate is greater than the minimum rate
                if (minProRate > recommendProRate) {
                    mlToast(getString(R.string.not_recommend));
                    recommendProRate = minProRate + 1;
                }

                int maxRecommendNum = (recommendProRate * 2) - minProRate;
                int noeRecommendPro = recommendProRate - minProRate;

                seekBar.setMax(maxRecommendNum * 10000);
                seekBar.setProgress(noeRecommendPro * 10000);
                testChangeFee.setText(feeRateForChild);

                String baseUnit = preferences.getString("base_unit", "mBTC");
                if ("BTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100000000;
                } else if ("mBTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100000;
                } else if ("bits".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100;
                } else if ("sat".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive);
                }

                tetNewfee.setText(String.format("%s  %s", getString(R.string.speed_fee_is), feeForChild + " sat"));
                seekBarReceiveFee(seekBar, testChangeFee, tetNewfee, recommendProRate);
                imgCancel.setOnClickListener(v -> {
                    alertDialog.dismiss();
                });
                viewSpeed.findViewById(R.id.btn_add_Speed).setOnClickListener(v -> {
                    confirmedReceiveSpeed(feeForChildReceive);
                });
                alertDialog.show();
                //show center
                Window dialogWindow = alertDialog.getWindow();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                p.width = (int) (d.getWidth() * 0.95);
                p.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(p);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void seekBarReceiveFee(IndicatorSeekBar seekBar, TextView testChangeFee, TextView tetNewfee, int recommendProRate) {
        seekBar.setOnSeekBarChangeListener(new IndicatorSeekBar.OnIndicatorSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BigDecimal big10 = new BigDecimal(10000);
                BigDecimal bigChange = new BigDecimal(seekBar.getProgress() + (minProRate * 10000));
                BigDecimal bigResult = bigChange.divide(big10);
                String indicatorText = String.valueOf(bigResult);
                testChangeFee.setText(String.format("%s sat/byte", indicatorText));
                getCpfpInfo(tetNewfee, bigResult);//get tx and fee
            }
        });
    }

    private void getCpfpInfo(TextView tetNewfee, BigDecimal newFeerate) {
        PyObject getRbfFeeInfo = null;
        try {
            getRbfFeeInfo = Daemon.commands.callAttr("get_cpfp_info", txHash, new Kwarg("suggested_feerate", Float.parseFloat(String.valueOf(newFeerate))));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("max fee exceeded")) {
                mToast(getString(R.string.fee_exceeded));
            }
            return;
        }
        if (getRbfFeeInfo != null) {
            String strContent = getRbfFeeInfo.toString();
            try {
                JSONObject jsonObject = new JSONObject(strContent);
                feeForChildReceive = jsonObject.getString("fee_for_child");
                String baseUnit = preferences.getString("base_unit", "mBTC");
                if ("BTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100000000;
                } else if ("mBTC".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100000;
                } else if ("bits".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive) * 100;
                } else if ("sat".equals(baseUnit)) {
                    feeForChild = Float.parseFloat(feeForChildReceive);
                }
                tetNewfee.setText(String.format("%s  %s", getString(R.string.speed_fee_is), feeForChild + " sat"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void seekbarLatoutup(IndicatorSeekBar seekBar, TextView testChangeFee, TextView tetNewfee) {
        seekBar.setOnSeekBarChangeListener(new IndicatorSeekBar.OnIndicatorSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BigDecimal big10rbf = new BigDecimal(10000);
                BigDecimal bigChangeRbf = new BigDecimal(seekBar.getProgress() + (minPro * 10000));
                BigDecimal bigResultRbf = bigChangeRbf.divide(big10rbf);

                testChangeFee.setText(String.format("%s sat/byte", bigResultRbf));
                createBumpFee(tetNewfee, bigResultRbf);//get tx and fee

            }
        });
    }

    private void createBumpFee(TextView tetNewfee, BigDecimal newFee) {
        PyObject createBumpFee = null;
        try {
            createBumpFee = Daemon.commands.callAttr("create_bump_fee", txHash, Float.parseFloat(String.valueOf(newFee)));
            Log.i("getRbfFeeInfosss", "createBumpFee----: " + createBumpFee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (createBumpFee != null) {
            String strContent = createBumpFee.toString();
            Gson gson = new Gson();
            AddspeedNewtrsactionBean addspeedNewtrsactionBean = gson.fromJson(strContent, AddspeedNewtrsactionBean.class);
            int fee = addspeedNewtrsactionBean.getFee();
            tetNewfee.setText(String.format("%s  %s sat", getString(R.string.speed_fee_is), fee));
        }

    }

    private void confirmedReceiveSpeed(String feeForChild) {
        PyObject createCpfpTx = null;
        try {
            createCpfpTx = Daemon.commands.callAttr("create_cpfp_tx", txHash, feeForChild);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (createCpfpTx != null) {
            String strNewTx = createCpfpTx.toString();
            Gson gson = new Gson();
            AddspeedNewtrsactionBean addspeedNewtrsactionBean = gson.fromJson(strNewTx, AddspeedNewtrsactionBean.class);
            publicTrsation = addspeedNewtrsactionBean.getNewTx();
            EventBus.getDefault().post(new FirstEvent("22"));
            tetAddSpeed.setVisibility(View.GONE);
            buttonStatus = false;//buttonStatus = true-->don't speed up   ：  false-->speed up
            braodStatus = true;//braod_status = true   -->   speed don't broadcast
            edit.putString("signedRowTransaction", publicTrsation);
            edit.apply();
            mCreataSuccsesCheck();
            alertDialog.dismiss();

        }
    }

    //btn onclick
    private void signProcess() {
        String strBtncontent = sigTrans.getText().toString();
        if (strBtncontent.equals(getString(R.string.broadcast))) {
            //broadcast transaction
            braodcastTrsaction();

        } else if (strBtncontent.equals(getString(R.string.check_trsaction))) {
            Intent intent1 = new Intent(TransactionDetailsActivity.this, CheckChainDetailWebActivity.class);
            intent1.putExtra("checkTxid", txid);
            startActivity(intent1);
        } else if (strBtncontent.equals(getString(R.string.signature_trans))) {
            if ("standard".equals(strwalletType)) {
                //sign input pass
                signInputpassDialog();
            } else {
                if ("1-1".equals(walletTypeToSign) && Ble.getInstance().getConnetedDevices().size() != 0) {
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
                if (!TextUtils.isEmpty(hideWallet)) {
                    intent1.putExtra("tag", TAG_HIDE_WALLET);
                } else {
                    intent1.putExtra("tag", TAG);
                }
                intent1.putExtra("extras", rawtx);
                startActivity(intent1);
            }
        } else {
            mToast(getString(R.string.unrelated_transaction));
        }
    }


    //Radio broadcast
    private void braodcastTrsaction() {
        String signedRowTrsation = preferences.getString("signedRowTransaction", "");
        try {
            Daemon.commands.callAttr("broadcast_tx", signedRowTrsation);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            assert message != null;
            if (message.contains("bad-txns-inputs-missingorspent")) {
                mToast(getString(R.string.dont_braundcast));
            } else if (message.contains(".")) {
                if (message.endsWith(".")) {
                    message = message.substring(0, message.length() - 1);
                    mToast(message);
                }
                mToast(message.substring(message.lastIndexOf(".") + 1));
            }
            return;
        }
        EventBus.getDefault().post(new FirstEvent("22"));
        broadcastStatus();
    }

    private void broadcastStatus() {
        tetState.setText(R.string.waitchoose);
        sigTrans.setText(R.string.check_trsaction);
        imgProgressone.setVisibility(View.GONE);
        imgProgressthree.setVisibility(View.GONE);
        imgProgressfour.setVisibility(View.VISIBLE);
        //text color
        tetTrthree.setTextColor(getColor(R.color.button_bk_disableok));
        tetTrfore.setTextColor(getColor(R.color.button_bk_disableok));
        //trsaction hash and time
        linTractionHash.setVisibility(View.VISIBLE);
        mToast(getString(R.string.broadcast_success));

    }

    private void signInputpassDialog() {
        View view1 = LayoutInflater.from(TransactionDetailsActivity.this).inflate(R.layout.input_wallet_pass, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(TransactionDetailsActivity.this).setView(view1).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText strPass = view1.findViewById(R.id.edit_password);
        view1.findViewById(R.id.btn_enter_wallet).setOnClickListener(v -> {
            String strPassword = strPass.getText().toString();
            if (TextUtils.isEmpty(strPassword)) {
                mToast(getString(R.string.please_input_pass));
                return;
            }
            try {
                PyObject signTx = Daemon.commands.callAttr("sign_tx", rawtx, new Kwarg("password", strPassword));
                if (signTx != null) {
                    jsonDetailData(signTx.toString());
                    alertDialog.dismiss();
                    EventBus.getDefault().post(new FirstEvent("22"));

                }
            } catch (Exception e) {
                if (e.getMessage().contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass));
                }
                e.printStackTrace();
            }

        });
        view1.findViewById(R.id.cancel_select_wallet).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);

    }

    //confirm speed
    private void confirmedSpeed() {
        PyObject createBumpFee = null;
        try {
            createBumpFee = Daemon.commands.callAttr("confirm_rbf_tx", txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (createBumpFee != null) {
            publicTrsation = createBumpFee.toString();
            //todo
            EventBus.getDefault().post(new FirstEvent("22"));
            //braod_status = true   -->   speed don't broadcast
            braodStatus = true;
            tetAddSpeed.setVisibility(View.GONE);
            buttonStatus = false;//buttonStatus = true-->Click the button   ：  false-->Modify status only
            edit.putString("signedRowTransaction", publicTrsation);
            edit.apply();
            mCreataSuccsesCheck();
            alertDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


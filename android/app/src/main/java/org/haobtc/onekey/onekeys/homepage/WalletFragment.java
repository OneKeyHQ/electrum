package org.haobtc.onekey.onekeys.homepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.event.BackupEvent;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.FixWalletNameEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.DetailTransactionActivity;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;
import org.haobtc.onekey.onekeys.homepage.process.ReceiveHDActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;
import org.haobtc.onekey.onekeys.homepage.process.TransactionDetailWalletActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;

import static android.app.Activity.RESULT_OK;

/**
 * @author jinxiaomin
 */
public class WalletFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    TextView textWalletName;
    private RecyclerView recyclerview;
    private SharedPreferences preferences;
    private LinearLayout linearNoWallet;
    private ImageView imgBottom;
    private LinearLayout linearHaveWallet;
    private LinearLayout linearWalletList;
    private TextView tetAmount;
    private String num;
    private TextView textDollar;
    private TextView textBtcAmount;
    private String changeBalance;
    private TextView textStar;
    private String name;
    private String loadWalletName = "";
    private SharedPreferences.Editor edit;
    private RelativeLayout relNowBackUp;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private TextView textHard;
    private LinearLayout linearSign;
    private ImageView imgType;
    private TextView amountStars;
    private TextView dollarStars;
    private String nowType;
    private boolean isBackup;
    private ImageView imgScan;
    private String deviceId;
    private String bleMac;
    private static int currentAction;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        EventBus.getDefault().register(this);
        rxPermissions = new RxPermissions(this);
        preferences = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        textWalletName = view.findViewById(R.id.text_wallet_name);
        tetAmount = view.findViewById(R.id.text_amount);
        recyclerview = view.findViewById(R.id.recl_hd_list);
        RelativeLayout relRecovery = view.findViewById(R.id.rel_recovery_hd);
        RelativeLayout relCreateHd = view.findViewById(R.id.rel_create_hd);
        RelativeLayout relCheckWallet = view.findViewById(R.id.rel_check_wallet);
        imgScan = view.findViewById(R.id.img_scan);
        RelativeLayout relPairHard = view.findViewById(R.id.rel_pair_hard);
        LinearLayout linearSend = view.findViewById(R.id.linear_send);
        LinearLayout linearReceive = view.findViewById(R.id.linear_receive);
        RelativeLayout relWalletDetail = view.findViewById(R.id.rel_wallet_detail);
        RelativeLayout relBiDetail = view.findViewById(R.id.rel_bi_detail);
        ImageView imgAdd = view.findViewById(R.id.img_add);
        CheckBox imgCheckMoney = view.findViewById(R.id.img_check_money);
        imgType = view.findViewById(R.id.img_type);
        textStar = view.findViewById(R.id.text_amount_star);
        textBtcAmount = view.findViewById(R.id.text_btc_amount);
        textDollar = view.findViewById(R.id.text_dollar);
        linearNoWallet = view.findViewById(R.id.lin_no_wallet);
        linearHaveWallet = view.findViewById(R.id.lin_have_wallet);
        linearWalletList = view.findViewById(R.id.lin_wallet_list);
        relNowBackUp = view.findViewById(R.id.rel_now_back_up);
        textHard = view.findViewById(R.id.text_hard);
        linearSign = view.findViewById(R.id.linear_sign);
        amountStars = view.findViewById(R.id.text_btc_amount_stars);
        dollarStars = view.findViewById(R.id.text_dollar_stars);

        imgBottom = view.findViewById(R.id.img_bottom);
        imgAdd.setOnClickListener(this);
        relCreateHd.setOnClickListener(this);
        relRecovery.setOnClickListener(this);
        relCheckWallet.setOnClickListener(this);
        imgScan.setOnClickListener(this);
        relPairHard.setOnClickListener(this);
        linearSend.setOnClickListener(this);
        linearReceive.setOnClickListener(this);
        relWalletDetail.setOnClickListener(this);
        relBiDetail.setOnClickListener(this);
        imgCheckMoney.setOnCheckedChangeListener(this);
        relNowBackUp.setOnClickListener(this);
        linearSign.setOnClickListener(this);
        initdata();
        return view;
    }

    private void initdata() {
        if (PreferencesManager.hasWallet(getContext())) {
            //have wallet
            linearNoWallet.setVisibility(View.GONE);
            imgBottom.setVisibility(View.GONE);
            linearHaveWallet.setVisibility(View.VISIBLE);
            linearWalletList.setVisibility(View.VISIBLE);
            //get wallet balance
            getWalletBalance();
            // get wallet list save wallet type

        } else {
            //no wallet
            try {
                Daemon.commands.callAttr("set_currency", "CNY");
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgType.setImageDrawable(getActivity().getDrawable(R.drawable.loco_round));
            linearNoWallet.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.VISIBLE);
            linearHaveWallet.setVisibility(View.GONE);
            linearWalletList.setVisibility(View.GONE);
            imgScan.setVisibility(View.GONE);
            relNowBackUp.setVisibility(View.GONE);
        }
    }

    private void isBackup(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next_backup).setOnClickListener(v -> {
            edit.putBoolean("isBack_up", true);
            edit.apply();
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_now_backup).setOnClickListener(v -> {
            edit.putBoolean("isBack_up", true);
            edit.apply();
            Intent intent = new Intent(getActivity(), BackupGuideActivity.class);
            intent.putExtra("walletType", nowType);
            startActivity(intent);
            //Next time
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.img_close).setOnClickListener(v -> {
            edit.putBoolean("isBack_up", true);
            edit.apply();
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

    private void getWalletBalance() {
        //Get current wallet name
        loadWalletName = preferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
        try {
            Optional.ofNullable(PyEnv.selectWallet(loadWalletName)).ifPresent((balanceInfo -> {
                String balance = balanceInfo.getBalance();
                name = balanceInfo.getName();
                LocalWalletInfo localWalletInfo;
                String str = PreferencesManager.get(getContext(), org.haobtc.onekey.constant.Constant.WALLETS, name, "").toString();
                if (!Strings.isNullOrEmpty(str)) {
                    localWalletInfo = LocalWalletInfo.objectFromData(str);
                    textWalletName.setText(localWalletInfo.getLabel());
                    showTypeInfo(localWalletInfo);
                }
                num = balance.substring(0, balance.indexOf(" "));
                String strCny = balance.substring(balance.indexOf("(") + 1, balance.indexOf(")"));
                int cnyUnit = preferences.getInt("cny_unit", 0);
                switch (cnyUnit) {
                    case 0:
                        tetAmount.setText(String.format("￥%s", (Strings.isNullOrEmpty(strCny)) ? R.string.zero: strCny));
                        break;
                    case 1:
                        tetAmount.setText(String.format("$%s", (Strings.isNullOrEmpty(strCny)) ? R.string.zero: strCny));
                }
                textBtcAmount.setText(String.format("%s%s", num, preferences.getString("base_unit", "")));
                if (!"0".equals(num)) {
                    getCny(num, cnyUnit);
                }
                whetherBackup();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showTypeInfo(LocalWalletInfo localWalletInfo) {
        nowType = localWalletInfo.getType();
        edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE, nowType);
        edit.apply();
        if (nowType.contains("btc")) {
            imgType.setImageDrawable(getActivity().getDrawable(R.drawable.token_btc));
        } else if (nowType.contains("eth")) {
            imgType.setImageDrawable(getActivity().getDrawable(R.drawable.token_eth));
        } else {
            imgType.setImageDrawable(getActivity().getDrawable(R.drawable.loco_round));
        }
        if (nowType.contains("hw")) {
            textHard.setVisibility(View.VISIBLE);
            linearSign.setVisibility(View.VISIBLE);
            String thisType = nowType.substring(nowType.indexOf("hw-") + 3);
            deviceId = localWalletInfo.getDeviceId();
            // 去除deviceId上的双引号
            deviceId = deviceId.substring(1, deviceId.length()-1);
            String deviceInfo = PreferencesManager.get(getContext(), org.haobtc.onekey.constant.Constant.DEVICES, deviceId, "").toString();
            if (!Strings.isNullOrEmpty(deviceInfo)) {
                HardwareFeatures info = HardwareFeatures.objectFromData(deviceInfo);
                String bleName = info.getBleName();
                String label = info.getLabel();
                bleMac = PreferencesManager.get(getContext(), org.haobtc.onekey.constant.Constant.BLE_INFO, bleName, "").toString();
                textHard.setText(Strings.isNullOrEmpty(label)? bleName: label);
            } else {
                textHard.setText(deviceId);
            }
        } else {
            linearSign.setVisibility(View.GONE);
            textHard.setVisibility(View.GONE);
        }
    }

    private void whetherBackup() {
        try {
            boolean isBackup = PyEnv.hasBackup(loadWalletName);
            if (!isBackup) {
                //no back up
                relNowBackUp.setVisibility(View.VISIBLE);
                //whether to backup
                if (!preferences.getBoolean("isBack_up", false)) {
                    isBackup(getActivity(), R.layout.backup_wallet);
                }
            } else {
                relNowBackUp.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            relNowBackUp.setVisibility(View.GONE);
        }

    }

    private void getCny(String changeBalance, int cnyUnits) {
        try {
            PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", changeBalance);
            if (!TextUtils.isEmpty(money.toString())) {
                if (cnyUnits == 0) {
                    tetAmount.setText(String.format("￥%s", money.toString()));
                } else if (cnyUnits == 1) {
                    tetAmount.setText(String.format("$%s", money.toString()));
                }
            } else {
                if (cnyUnits == 0) {
                    tetAmount.setText(String.format("￥%s", getString(R.string.zero)));
                } else if (cnyUnits == 1) {
                    tetAmount.setText(String.format("$%s", getString(R.string.zero)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_check_wallet:
                Intent intent1 = new Intent(getActivity(), WalletListActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_scan:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(getActivity(), CaptureActivity.class);
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
                                Toast.makeText(getActivity(), R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.rel_create_hd:
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    Intent intent0 = new Intent(getActivity(), SetHDWalletPassActivity.class);
                    startActivity(intent0);
                } else {
                    Intent intent0 = new Intent(getActivity(), SetLongPassActivity.class);
                    startActivity(intent0);
                }
                break;
            case R.id.rel_recovery_hd:
                Intent intent = new Intent(getActivity(), RecoverHdWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_pair_hard:
                Intent pair = new Intent(getActivity(), SearchDevicesActivity.class);
                startActivity(pair);
                break;
            case R.id.linear_send:
            case R.id.linear_receive:
               deal(v.getId());
               break;
            case R.id.rel_wallet_detail:
                Intent intent4 = new Intent(getActivity(), HdWalletDetailActivity.class);
                intent4.putExtra("hdWalletName", textWalletName.getText().toString());
                intent4.putExtra("isBackup", isBackup);
                startActivity(intent4);
                break;
            case R.id.rel_bi_detail:
                Intent intent5 = new Intent(getActivity(), TransactionDetailWalletActivity.class);
                intent5.putExtra("walletBalance", changeBalance);
                intent5.putExtra("walletDollar", textDollar.getText().toString());
                intent5.putExtra("hdWalletName", textWalletName.getText().toString());
                startActivity(intent5);
                break;
            case R.id.img_add:

                break;
            case R.id.rel_now_back_up:
                Intent intent6 = new Intent(getActivity(), BackupGuideActivity.class);
                intent6.putExtra("walletType", nowType);
                startActivity(intent6);
                break;
            case R.id.linear_sign:
                Intent intent8 = new Intent(getActivity(), SignActivity.class);
                startActivity(intent8);
                break;

        }
    }
    private void deal(@IdRes int id) {
        if (org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE.equals(nowType)) {
            currentAction = id;
            if (Strings.isNullOrEmpty(bleMac)) {
                Toast.makeText(getContext(), "未发现设备信息", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent2 = new Intent(getActivity(), SearchDevicesActivity.class);
                intent2.putExtra(org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE, org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
                startActivity(intent2);
                BleManager.getInstance(getActivity()).connDevByMac(bleMac);
            }
            return;
        }
        toNext(id);

    }
    private void toNext(int id) {
        switch (id) {
            case R.id.linear_send:
                Intent intent2 = new Intent(getActivity(), SendHdActivity.class);
                intent2.putExtra("sendNum", changeBalance);
                intent2.putExtra("hdWalletName", name);
                startActivity(intent2);
                break;
            case R.id.linear_receive:
                Intent intent3 = new Intent(getActivity(), ReceiveHDActivity.class);
                if (org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE.equals(nowType)) {
                    intent3.putExtra(org.haobtc.onekey.constant.Constant.WALLET_TYPE, org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_);
                }
                startActivity(intent3);
                break;
            default:
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if (!TextUtils.isEmpty(msgVote) && msgVote.length() != 2 && msgVote.contains("{")) {
            setValue(msgVote);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(BackupEvent updataHint) {
        //whether back up
        whetherBackup();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(BleConnectedEvent event) {
        toNext(currentAction);
    }
    @Subscribe
    public void onConnectionTimeout(BleConnectionEx connectionEx) {
        if (connectionEx == BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT) {
            Toast.makeText(getContext(), "连接蓝牙设备超时，请确认你的设备是否已开启蓝牙，并在你的旁边", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除钱包后选择其他钱包
     * */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(LoadOtherWalletEvent event) {
        //load other wallet
        if (PreferencesManager.hasWallet(getContext())) {
            Optional<? extends Map.Entry<String, ?>> entry = PreferencesManager.getAll(getContext(), org.haobtc.onekey.constant.Constant.WALLETS).entrySet().stream().findFirst();
            LocalWalletInfo info = LocalWalletInfo.objectFromData(entry.get().getValue().toString());
            edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, info.getName());
            edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE, info.getType());
            edit.apply();
            //get wallet balance
            getWalletBalance();
        } else {
            edit.putString("shortOrLongPass", "short");
            edit.putBoolean("isBack_up", false);
            edit.apply();
            textWalletName.setText(getString(R.string.no_use_wallet));
            linearNoWallet.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.VISIBLE);
            linearHaveWallet.setVisibility(View.GONE);
            linearWalletList.setVisibility(View.GONE);
            imgType.setImageDrawable(getActivity().getDrawable(R.drawable.loco_round));
            isBackup = true;
            relNowBackUp.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fixName(FixWalletNameEvent event) {
        textWalletName.setText(event.getNewName());
    }
    public void setValue(String msgVote) {
        try {
            JSONObject jsonObject = new JSONObject(msgVote);
            if (msgVote.contains("fiat")) {
                String fiat = jsonObject.getString("fiat");
                changeBalance = jsonObject.getString("balance");
                textBtcAmount.setText(String.format("%s%s", changeBalance, preferences.getString("base_unit", "")));
                if (!TextUtils.isEmpty(fiat)) {
                    if (fiat.contains("USD")) {
                        String usd = fiat.substring(0, fiat.indexOf(" "));
                        tetAmount.setText(String.format("$%s", usd));
                        textDollar.setText(String.format("≈ $ %s", usd));
                    } else if (fiat.contains("CNY")) {
                        String cny = fiat.substring(0, fiat.indexOf(" "));
                        tetAmount.setText(String.format("￥%s", cny));
                        textDollar.setText(String.format("≈ ￥ %s", cny));
                    } else {
                        tetAmount.setText(String.format("%s", fiat));
                        textDollar.setText(String.format("≈ %s", fiat));
                    }
                } else {
                    textDollar.setText(String.format("≈ $ %s", getString(R.string.zero)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Scan QR code / barcode return
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (!TextUtils.isEmpty(content)) {
                    PyObject parseQr;
                    try {
                        parseQr = Daemon.commands.callAttr("parse_pr", content);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.address_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (parseQr.toString().length() > 2) {
                        String strParse = parseQr.toString();
                        Log.i("PyObjectjxm", "parse_qr:  " + strParse);
                        String substring = strParse.substring(20);
                        String detailScan = substring.substring(0, substring.length() - 1);
                        Log.i("PyObjectjxm", "parse_qr:---------  " + detailScan);
                        try {
                            JSONObject jsonObject = new JSONObject(strParse);
                            int type = jsonObject.getInt("type");
                            Gson gson = new Gson();
                            if (type == 1) {
                                MainSweepcodeBean mainSweepcodeBean = gson.fromJson(strParse, MainSweepcodeBean.class);
                                MainSweepcodeBean.DataBean listData = mainSweepcodeBean.getData();
                                String address = listData.getAddress();
                                Intent intent2 = new Intent(getActivity(), SendHdActivity.class);
                                intent2.putExtra("sendNum", changeBalance);
                                intent2.putExtra("hdWalletName", name);
                                intent2.putExtra("addressScan", address);
                                startActivity(intent2);

                            } else if (type == 2) {
                                Intent intent = new Intent(getActivity(), DetailTransactionActivity.class);
                                intent.putExtra("scanDetail", detailScan);
                                intent.putExtra("detailType", "homeScanDetail");
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.address_wrong), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.address_wrong), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tetAmount.setVisibility(View.VISIBLE);
            textStar.setVisibility(View.GONE);
            textBtcAmount.setVisibility(View.VISIBLE);
            textDollar.setVisibility(View.VISIBLE);
            amountStars.setVisibility(View.GONE);
            dollarStars.setVisibility(View.GONE);
        } else {
            tetAmount.setVisibility(View.GONE);
            textStar.setVisibility(View.VISIBLE);
            textBtcAmount.setVisibility(View.GONE);
            textDollar.setVisibility(View.GONE);
            amountStars.setVisibility(View.VISIBLE);
            dollarStars.setVisibility(View.VISIBLE);
        }
    }
}
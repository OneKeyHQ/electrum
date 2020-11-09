package org.haobtc.onekey.onekeys.homepage;

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

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.HomeHdAdapter;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.bean.AddressEvent;
import org.haobtc.onekey.bean.HomeWalletBean;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;
import org.haobtc.onekey.onekeys.homepage.process.ReceiveHDActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;
import org.haobtc.onekey.onekeys.homepage.process.TransactionDetailWalletActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.executorService;

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
    private String loadWalletMsg = "";
    private SharedPreferences.Editor edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        EventBus.getDefault().register(this);
        preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        loadWalletMsg = preferences.getString("loadWalletName", "BTC-1");//Get current wallet name
        textWalletName = view.findViewById(R.id.text_wallet_name);
        tetAmount = view.findViewById(R.id.text_amount);
        recyclerview = view.findViewById(R.id.recl_hd_list);
        RelativeLayout relRecovery = view.findViewById(R.id.rel_recovery_hd);
        RelativeLayout relCreateHd = view.findViewById(R.id.rel_create_hd);
        RelativeLayout relCheckWallet = view.findViewById(R.id.rel_check_wallet);
        ImageView imgScan = view.findViewById(R.id.img_scan);
        RelativeLayout relPairHard = view.findViewById(R.id.rel_pair_hard);
        LinearLayout linearSend = view.findViewById(R.id.linear_send);
        LinearLayout linearReceive = view.findViewById(R.id.linear_receive);
        RelativeLayout relWalletDetail = view.findViewById(R.id.rel_wallet_detail);
        RelativeLayout relBiDetail = view.findViewById(R.id.rel_bi_detail);
        ImageView imgAdd = view.findViewById(R.id.img_add);
        CheckBox imgCheckMoney = view.findViewById(R.id.img_check_money);
        textStar = view.findViewById(R.id.text_amount_star);
        textBtcAmount = view.findViewById(R.id.text_btc_amount);
        textDollar = view.findViewById(R.id.text_dollar);
        linearNoWallet = view.findViewById(R.id.lin_no_wallet);
        linearHaveWallet = view.findViewById(R.id.lin_have_wallet);
        linearWalletList = view.findViewById(R.id.lin_wallet_list);

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
        initdata();
        return view;
    }

    private void initdata() {
        //判断有没有备份
        if (preferences.getBoolean("isBack_up",false)){
//            isBackup(getActivity(), R.layout.backup_wallet);
        }
        boolean isHaveWallet = preferences.getBoolean("isHaveWallet", false);
        if (isHaveWallet) {
            linearNoWallet.setVisibility(View.GONE);
            imgBottom.setVisibility(View.GONE);
            linearHaveWallet.setVisibility(View.VISIBLE);
            linearWalletList.setVisibility(View.VISIBLE);
            //get wallet balance
            getWalletBalance();

        } else {
            linearNoWallet.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.VISIBLE);
            linearHaveWallet.setVisibility(View.GONE);
            linearWalletList.setVisibility(View.GONE);
        }
        //get wallet list save wallet type
        getHomeWalletList();

    }

    private void isBackup(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next_backup).setOnClickListener(v -> {
            edit.putBoolean("isBack_up",true);
            edit.apply();
            //back up now

            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_now_backup).setOnClickListener(v -> {
            edit.putBoolean("isBack_up",true);
            edit.apply();
            //Next time

            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.img_close).setOnClickListener(v -> {
            edit.putBoolean("isBack_up",true);
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

    private void getHomeWalletList() {
        executorService.execute(new Runnable() {
            private PyObject getWalletsListInfo;

            @Override
            public void run() {
                //wallet list
                try {
                    getWalletsListInfo = Daemon.commands.callAttr("list_wallets");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (getWalletsListInfo.toString().length() > 2) {
                    String toStrings = getWalletsListInfo.toString();
                    if (toStrings.length() != 2) {
                        JSONArray jsonDatas = com.alibaba.fastjson.JSONObject.parseArray(toStrings);
                        for (int i = 0; i < jsonDatas.size(); i++) {
                            Map jsonToMap = (Map) jsonDatas.get(i);
                            Set keySets = jsonToMap.keySet();
                            Iterator ki = keySets.iterator();
                            while (ki.hasNext()) {
                                try {
                                    //get key
                                    String key = (String) ki.next();
                                    String value = jsonToMap.get(key).toString();
                                    JSONObject jsonObject = new JSONObject(value);
                                    String type = jsonObject.getString("type");
                                    if (loadWalletMsg.equals(key)) {
                                        edit.putString("showWalletType", type);
                                        edit.apply();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void getWalletBalance() {
        try {
            PyObject selectWallet = Daemon.commands.callAttr("select_wallet", loadWalletMsg);
            Log.i("iiiigetWalletBalance", "getWalletBalance:--- " + selectWallet);
            if (!TextUtils.isEmpty(selectWallet.toString())) {
                HomeWalletBean homeWalletBean = new Gson().fromJson(selectWallet.toString(), HomeWalletBean.class);
                String balance = homeWalletBean.getBalance();
                name = homeWalletBean.getName();
                textWalletName.setText(name);
                num = balance.substring(0, balance.indexOf(" "));
                String strCny = balance.substring(balance.indexOf("(") + 1, balance.indexOf(")"));
                tetAmount.setText(strCny);
                textBtcAmount.setText(String.format("%s%s", num, preferences.getString("base_unit", "")));
                getCny(num);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    private void getCny(String changeBalance) {
        try {
            PyObject money = Daemon.commands.callAttr("get_exchange_currency", "base", changeBalance);
            if (!TextUtils.isEmpty(money.toString())) {
                tetAmount.setText(money.toString());
            } else {
                tetAmount.setText(getString(R.string.zero));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_check_wallet:
                Intent intent1 = new Intent(getActivity(), WalletListActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_scan:
                break;
            case R.id.rel_create_hd:
                Intent intent0 = new Intent(getActivity(), SetHDWalletPassActivity.class);
                startActivity(intent0);
                break;
            case R.id.rel_recovery_hd:
                Intent intent = new Intent(getActivity(), RecoverHdWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_pair_hard:
                break;
            case R.id.linear_send:
                Intent intent2 = new Intent(getActivity(), SendHdActivity.class);
                intent2.putExtra("sendNum", changeBalance);
                intent2.putExtra("hdWalletName", name);
                startActivity(intent2);
                break;
            case R.id.linear_receive:
                Intent intent3 = new Intent(getActivity(), ReceiveHDActivity.class);
                startActivity(intent3);
                break;
            case R.id.rel_wallet_detail:
                Intent intent4 = new Intent(getActivity(), HdWalletDetailActivity.class);
                intent4.putExtra("hdWalletName", textWalletName.getText().toString());
                startActivity(intent4);
                break;
            case R.id.rel_bi_detail:
                Intent intent5 = new Intent(getActivity(), TransactionDetailWalletActivity.class);
                intent5.putExtra("walletBalance", textBtcAmount.getText().toString());
                intent5.putExtra("walletDollar", textDollar.getText().toString());
                startActivity(intent5);
                break;
            case R.id.img_add:

                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if (!TextUtils.isEmpty(msgVote) && msgVote.length() != 2 && msgVote.contains("{")) {
            setValue(msgVote);
        }
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
                        textDollar.setText(String.format("$%s", usd));
                    } else if (fiat.contains("CNY")) {
                        String cny = fiat.substring(0, fiat.indexOf(" "));
                        tetAmount.setText(String.format("￥%s", cny));
                        textDollar.setText(String.format("￥%s", cny));
                    } else {
                        tetAmount.setText(String.format("%s", fiat));
                        textDollar.setText(String.format("%s", fiat));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tetAmount.setVisibility(View.VISIBLE);
            textStar.setVisibility(View.GONE);
        } else {
            tetAmount.setVisibility(View.GONE);
            textStar.setVisibility(View.VISIBLE);
        }
    }
}
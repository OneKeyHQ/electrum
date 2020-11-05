package org.haobtc.onekey.onekeys.homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.HomeHdAdapter;
import org.haobtc.onekey.bean.HomeWalletBean;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.fragment.mainwheel.WheelViewpagerFragment;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;
import org.haobtc.onekey.onekeys.homepage.process.ReceiveHDActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;
import org.haobtc.onekey.onekeys.homepage.process.TransactionDetailWalletActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        EventBus.getDefault().register(this);
        preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
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
    }

    private void getWalletBalance() {
        try {
            PyObject selectWallet = Daemon.commands.callAttr("select_wallet", "BTC-1");
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

    private void downWalletList() {
        ArrayList<String> amountList = new ArrayList<>();
//        amountList.add(num + preferences.getString("set_base_uint", ""));

        HomeHdAdapter homeHdAdapter = new HomeHdAdapter(amountList);
        recyclerview.setAdapter(homeHdAdapter);
        homeHdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), TransactionDetailWalletActivity.class);
                intent.putExtra("walletBalance", num + preferences.getString("set_base_uint", ""));
                startActivity(intent);
            }
        });

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
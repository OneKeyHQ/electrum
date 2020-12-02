package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.bean.AddressEvent;
import org.haobtc.onekey.event.LoadWalletlistEvent;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateDeriveChooseTypeActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HDWalletActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.text_wallet_num)
    TextView textWalletNum;
    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    @BindView(R.id.lin_not_wallet)
    LinearLayout linNotWallet;
    @BindView(R.id.recl_add_wallet)
    RelativeLayout reclAddWallet;
    @BindView(R.id.text_manage)
    TextView textManage;
    private ArrayList<AddressEvent> hdWalletList;
    private WalletListAdapter walletListAdapter;
    private String deleteHdWalletName = "";
    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_h_d_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    @Override
    public void initData() {
        reclWalletList.setNestedScrollingEnabled(false);
        //wallet name and balance list
        hdWalletList = new ArrayList<>();
        walletListAdapter = new WalletListAdapter(hdWalletList);
        reclWalletList.setAdapter(walletListAdapter);
        getHomeWalletList();
    }

    @OnClick({R.id.img_back, R.id.text_manage, R.id.recl_add_wallet, R.id.recl_add_hd_wallet, R.id.recl_recovery_wallet, R.id.img_what_hd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_manage:
                Intent intent1 = new Intent(HDWalletActivity.this, WalletManageActivity.class);
                intent1.putExtra("hd_num", hdWalletList.size());
                intent1.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent1);
                break;
            case R.id.recl_add_wallet:
                Intent intent = new Intent(HDWalletActivity.this, CreateDeriveChooseTypeActivity.class);
                intent.putExtra("walletType", "derive");
                startActivity(intent);
                break;
            case R.id.recl_add_hd_wallet:
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    Intent intent0 = new Intent(this, SetHDWalletPassActivity.class);
                    startActivity(intent0);
                } else {
                    Intent intent0 = new Intent(this, SetLongPassActivity.class);
                    startActivity(intent0);
                }
                break;
            case R.id.recl_recovery_wallet:
                Intent intent2 = new Intent(HDWalletActivity.this, RecoverHdWalletActivity.class);
                startActivity(intent2);
                break;
            case R.id.img_what_hd:
                whatIsHd(HDWalletActivity.this, R.layout.what_is_hd);
                break;
        }
    }

    private void whatIsHd(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            dialogBtoms.dismiss();
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

    private void getHomeWalletList() {
        hdWalletList.clear();
        PyObject getWalletsListInfo;
        //wallet list
        try {
            getWalletsListInfo = Daemon.commands.callAttr("list_wallets");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String toStrings = getWalletsListInfo.toString();
        Log.i("mWheelplanting", "toStrings: " + toStrings);
        if (getWalletsListInfo.toString().length() > 2) {
            JSONArray jsonDatas = com.alibaba.fastjson.JSONObject.parseArray(toStrings);
            for (int i = 0; i < jsonDatas.size(); i++) {
                Map jsonToMap = (Map) jsonDatas.get(i);
                Set keySets = jsonToMap.keySet();
                Iterator ki = keySets.iterator();
                AddressEvent addressEvent = new AddressEvent();

                while (ki.hasNext()) {
                    try {
                        //get key
                        String key = (String) ki.next();
                        String value = jsonToMap.get(key).toString();
                        JSONObject jsonObject = new JSONObject(value);
                        String addr = jsonObject.getString("addr");
                        String type = jsonObject.getString("type");
                        if (type.contains("hd") || type.contains("derived")) {
                            addressEvent.setName(key);
                            addressEvent.setType(type);
                            addressEvent.setAmount(addr);
                            hdWalletList.add(addressEvent);
                        }
                        if ("btc-hd-standard".equals(type)) {
                            deleteHdWalletName = key;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            textWalletNum.setText(String.valueOf(hdWalletList.size()));
            if (hdWalletList != null && hdWalletList.size() > 0) {
                walletListAdapter.notifyDataSetChanged();
            } else {
                reclAddWallet.setVisibility(View.GONE);
                linNotWallet.setVisibility(View.VISIBLE);
                textManage.setVisibility(View.GONE);
            }
        } else {
            textWalletNum.setText(String.valueOf(hdWalletList.size()));
            reclAddWallet.setVisibility(View.GONE);
            linNotWallet.setVisibility(View.VISIBLE);
            textManage.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onLoad(LoadWalletlistEvent event) {
        getHomeWalletList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
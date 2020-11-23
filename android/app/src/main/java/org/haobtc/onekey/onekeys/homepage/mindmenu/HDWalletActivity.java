package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.executorService;

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
    private ArrayList<AddressEvent> hdWalletList;
    private WalletListAdapter walletListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_h_d_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        //wallet name and balance list
        hdWalletList = new ArrayList<>();
        walletListAdapter = new WalletListAdapter(hdWalletList);
        reclWalletList.setAdapter(walletListAdapter);
        getHomeWalletList();
    }

    @OnClick({R.id.img_back, R.id.text_manage, R.id.recl_add_wallet, R.id.recl_add_hd_wallet, R.id.recl_recovery_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_manage:
                Intent intent1 = new Intent(HDWalletActivity.this, WalletManageActivity.class);
                intent1.putExtra("hd_num", hdWalletList.size());
                startActivity(intent1);
                break;
            case R.id.recl_add_wallet:
                Intent intent = new Intent(HDWalletActivity.this, CreateDeriveChooseTypeActivity.class);
                intent.putExtra("walletType", "derive");
                startActivity(intent);
                break;
            case R.id.recl_add_hd_wallet:
                Intent intent0 = new Intent(HDWalletActivity.this, SetHDWalletPassActivity.class);
                startActivity(intent0);
                break;
            case R.id.recl_recovery_wallet:
                Intent intent2 = new Intent(HDWalletActivity.this, RecoverHdWalletActivity.class);
                startActivity(intent2);
                break;
        }
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
            }
        } else {
            reclAddWallet.setVisibility(View.GONE);
            linNotWallet.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onFinish(LoadWalletlistEvent event) {
        getHomeWalletList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
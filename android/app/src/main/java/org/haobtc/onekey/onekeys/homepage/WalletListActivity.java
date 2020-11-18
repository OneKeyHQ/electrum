package org.haobtc.onekey.onekeys.homepage;

import android.annotation.SuppressLint;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.bean.AddressEvent;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateDeriveChooseTypeActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.onekeys.homepage.process.SetDeriveWalletNameActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
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

public class WalletListActivity extends BaseActivity {

    @BindView(R.id.recl_wallet_detail)
    LinearLayout reclWalletDetail;
    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    @BindView(R.id.text_wallet_num)
    TextView textWalletNum;
    @BindView(R.id.view_all)
    ImageView viewAll;
    @BindView(R.id.view_btc)
    ImageView viewBtc;
    @BindView(R.id.view_eth)
    ImageView viewEth;
    @BindView(R.id.tet_None)
    TextView tetNone;
    @BindView(R.id.recl_add_wallet)
    RelativeLayout reclAddWallet;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.recl_add_hd_wallet)
    RelativeLayout reclAddHdWallet;
    @BindView(R.id.recl_recovery_wallet)
    RelativeLayout reclRecoveryWallet;
    private ArrayList<AddressEvent> hdWalletList;
    private ArrayList<AddressEvent> btcList;
    private ArrayList<AddressEvent> ethList;
    private ArrayList<AddressEvent> eosList;
    private SharedPreferences.Editor edit;
    private int createWalletType = 0;//create derive wallet type

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();

    }

    @Override
    public void initData() {
        //wallet name and balance list
        hdWalletList = new ArrayList<>();
        //btc wallet list
        btcList = new ArrayList<>();
        //btc wallet list
        ethList = new ArrayList<>();
        //btc wallet list
        eosList = new ArrayList<>();
        getHomeWalletList();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({R.id.img_close, R.id.recl_wallet_detail, R.id.lin_pair_wallet, R.id.lin_add_wallet, R.id.view_all, R.id.view_btc, R.id.view_eth, R.id.recl_add_wallet, R.id.img_add, R.id.img_w, R.id.recl_add_hd_wallet, R.id.recl_recovery_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.recl_wallet_detail:
                mIntent(HomeOnekeyActivity.class);
                break;
            case R.id.lin_pair_wallet:
                Intent intent = new Intent(this, SearchDevicesActivity.class);
                intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
                startActivity(intent);
                break;
            case R.id.lin_add_wallet:
                createWalletChooseDialog(WalletListActivity.this, R.layout.add_wallet);
                break;
            case R.id.view_all:
                viewAll.setImageDrawable(getDrawable(R.drawable.hd_wallet_1));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                textWalletNum.setText(String.valueOf(hdWalletList.size()));
                reclWalletDetail.setVisibility(View.VISIBLE);
                imgAdd.setVisibility(View.GONE);
                if (hdWalletList == null || hdWalletList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.GONE);
                    reclWalletDetail.setVisibility(View.GONE);
                    reclAddWallet.setVisibility(View.GONE);
                    reclAddHdWallet.setVisibility(View.VISIBLE);
                    reclRecoveryWallet.setVisibility(View.VISIBLE);
                } else {
                    reclAddHdWallet.setVisibility(View.GONE);//add hd wallet
                    reclRecoveryWallet.setVisibility(View.GONE);//recovery wallet
                    reclAddWallet.setVisibility(View.VISIBLE);
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter walletListAdapter = new WalletListAdapter(hdWalletList);
                    reclWalletList.setAdapter(walletListAdapter);
                    walletListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            String name = hdWalletList.get(position).getName();
                            edit.putString("loadWalletName", name);
                            edit.apply();
                            mIntent(HomeOnekeyActivity.class);
                        }
                    });
                }
                break;
            case R.id.view_btc:
                createWalletType = 0;//createWalletType = 0   ----> create btc derive wallet
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                reclAddHdWallet.setVisibility(View.GONE);//add hd wallet
                reclRecoveryWallet.setVisibility(View.GONE);//recovery wallet
                textWalletNum.setText(String.valueOf(btcList.size()));
                reclAddWallet.setVisibility(View.GONE);
                reclWalletDetail.setVisibility(View.GONE);
                imgAdd.setVisibility(View.VISIBLE);
                if (btcList == null || btcList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter btcListAdapter = new WalletListAdapter(btcList);
                    reclWalletList.setAdapter(btcListAdapter);
                    btcListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            String name = btcList.get(position).getName();
                            edit.putString("loadWalletName", name);
                            edit.apply();
                            mIntent(HomeOnekeyActivity.class);
                        }
                    });
                }

                break;
            case R.id.view_eth:
                createWalletType = 1;//createWalletType = 1   ----> create eth derive wallet
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.token_eth));
                textWalletNum.setText(String.valueOf(ethList.size()));
                reclAddHdWallet.setVisibility(View.GONE);//add hd wallet
                reclRecoveryWallet.setVisibility(View.GONE);//recovery wallet
                reclAddWallet.setVisibility(View.GONE);
                reclWalletDetail.setVisibility(View.GONE);
                imgAdd.setVisibility(View.VISIBLE);
                if (ethList == null || ethList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter ethListAdapter = new WalletListAdapter(ethList);
                    reclWalletList.setAdapter(ethListAdapter);
                    ethListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            String name = ethList.get(position).getName();
                            edit.putString("loadWalletName", name);
                            edit.apply();
                            mIntent(HomeOnekeyActivity.class);
                        }
                    });
                }
                break;
            case R.id.img_add:
                Intent intents = new Intent(WalletListActivity.this, SetDeriveWalletNameActivity.class);
                intents.putExtra("walletType", "derive");
                if (createWalletType == 0) {
                    intents.putExtra("currencyType", "btc");
                } else {
                    intents.putExtra("currencyType", "eth");
                }
                startActivity(intents);
                break;
            case R.id.recl_add_wallet:
                Intent intent1 = new Intent(WalletListActivity.this, CreateDeriveChooseTypeActivity.class);
                intent1.putExtra("walletType", "derive");
                startActivity(intent1);
                break;
            case R.id.img_w:
                whatIsHd(WalletListActivity.this, R.layout.what_is_hd);
                break;
            case R.id.recl_add_hd_wallet:
                Intent intent0 = new Intent(WalletListActivity.this, SetHDWalletPassActivity.class);
                startActivity(intent0);
                break;
            case R.id.recl_recovery_wallet:
                Intent intent2 = new Intent(WalletListActivity.this, RecoverHdWalletActivity.class);
                startActivity(intent2);
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
                    Log.i("mWheelplanting", "toStrings: " + toStrings);
                    JSONArray jsonDatas = com.alibaba.fastjson.JSONObject.parseArray(toStrings);
                    for (int i = 0; i < jsonDatas.size(); i++) {
                        Map jsonToMap = (Map) jsonDatas.get(i);
                        Set keySets = jsonToMap.keySet();
                        Iterator ki = keySets.iterator();
                        AddressEvent addressEvent = new AddressEvent();
                        AddressEvent btcEvent = new AddressEvent();
                        AddressEvent ethEvent = new AddressEvent();
                        AddressEvent eosEvent = new AddressEvent();

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
                                if (type.contains("btc")) {
                                    btcEvent.setName(key);
                                    btcEvent.setType(type);
                                    btcEvent.setAmount(addr);
                                    btcList.add(btcEvent);
                                } else if (type.contains("eth")) {
                                    ethEvent.setName(key);
                                    ethEvent.setType(type);
                                    ethEvent.setAmount(addr);
                                    ethList.add(ethEvent);
                                } else if (type.contains("eos")) {
                                    eosEvent.setName(key);
                                    eosEvent.setType(type);
                                    eosEvent.setAmount(addr);
                                    eosList.add(eosEvent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    textWalletNum.setText(String.valueOf(hdWalletList.size()));
                    if (hdWalletList == null || hdWalletList.size() == 0) {
                        reclWalletDetail.setVisibility(View.GONE);
                        reclAddWallet.setVisibility(View.GONE);
                        reclAddHdWallet.setVisibility(View.VISIBLE);
                        reclRecoveryWallet.setVisibility(View.VISIBLE);
                    } else {
                        WalletListAdapter walletListAdapter = new WalletListAdapter(hdWalletList);
                        reclWalletList.setAdapter(walletListAdapter);
                        walletListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                String name = hdWalletList.get(position).getName();
                                edit.putString("loadWalletName", name);
                                edit.apply();
                                mIntent(HomeOnekeyActivity.class);
                            }
                        });
                    }
                } else {
                    reclWalletDetail.setVisibility(View.GONE);
                    reclAddWallet.setVisibility(View.GONE);
                    reclAddHdWallet.setVisibility(View.VISIBLE);
                    reclRecoveryWallet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateWalletChooseTypeActivity.class);
            startActivity(intent);
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_import).setOnClickListener(v -> {
            Intent intent = new Intent(context, ImprotSingleActivity.class);
            startActivity(intent);
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

}
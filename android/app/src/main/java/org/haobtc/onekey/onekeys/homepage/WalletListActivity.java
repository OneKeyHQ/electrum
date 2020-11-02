package org.haobtc.onekey.onekeys.homepage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.bean.AddressEvent;
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
    RelativeLayout reclWalletDetail;
    @BindView(R.id.img_add_wallet)
    ImageView imgAddWallet;
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
    @BindView(R.id.view_eos)
    ImageView viewEos;
    @BindView(R.id.tet_None)
    TextView tetNone;
    @BindView(R.id.recl_add_wallet)
    RelativeLayout reclAddWallet;
    private ArrayList<AddressEvent> hdWalletList;
    private ArrayList<AddressEvent> btcList;
    private ArrayList<AddressEvent> ethList;
    private ArrayList<AddressEvent> eosList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
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
    @OnClick({R.id.img_close, R.id.img_add_wallet, R.id.recl_wallet, R.id.lin_pair_wallet, R.id.lin_add_wallet, R.id.view_all, R.id.view_btc, R.id.view_eth, R.id.view_eos})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_add_wallet:
                break;
            case R.id.recl_wallet:
                break;
            case R.id.lin_pair_wallet:
                break;
            case R.id.lin_add_wallet:
                createWalletChooseDialog(WalletListActivity.this, R.layout.add_wallet);
                break;
            case R.id.view_all:
                viewAll.setImageDrawable(getDrawable(R.drawable.hd_wallet_1));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                viewEos.setImageDrawable(getDrawable(R.drawable.eos_icon));
                textWalletNum.setText(String.valueOf(hdWalletList.size()));
                reclAddWallet.setVisibility(View.VISIBLE);
                if (hdWalletList == null || hdWalletList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter walletListAdapter = new WalletListAdapter(hdWalletList);
                    reclWalletList.setAdapter(walletListAdapter);
                }
                break;
            case R.id.view_btc:
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                viewEos.setImageDrawable(getDrawable(R.drawable.eos_icon));
                textWalletNum.setText(String.valueOf(btcList.size()));
                reclAddWallet.setVisibility(View.GONE);
                if (btcList == null || btcList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter btcListAdapter = new WalletListAdapter(btcList);
                    reclWalletList.setAdapter(btcListAdapter);
                }

                break;
            case R.id.view_eth:
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.token_eth));
                viewEos.setImageDrawable(getDrawable(R.drawable.eos_icon));
                textWalletNum.setText(String.valueOf(ethList.size()));
                reclAddWallet.setVisibility(View.GONE);
                if (ethList == null || ethList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter ethListAdapter = new WalletListAdapter(ethList);
                    reclWalletList.setAdapter(ethListAdapter);
                }
                break;
            case R.id.view_eos:
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                viewEos.setImageDrawable(getDrawable(R.drawable.token_eos));
                textWalletNum.setText(String.valueOf(eosList.size()));
                reclAddWallet.setVisibility(View.GONE);
                if (eosList == null || eosList.size() == 0) {
                    reclWalletList.setVisibility(View.GONE);
                    tetNone.setVisibility(View.VISIBLE);
                } else {
                    reclWalletList.setVisibility(View.VISIBLE);
                    tetNone.setVisibility(View.GONE);
                    WalletListAdapter eosListAdapter = new WalletListAdapter(eosList);
                    reclWalletList.setAdapter(eosListAdapter);
                }
                break;
        }
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

                    if (toStrings.length() != 2) {
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
                                    if (type.contains("hd")) {
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
                            reclWalletList.setVisibility(View.GONE);
                            tetNone.setVisibility(View.VISIBLE);
                        } else {
                            WalletListAdapter walletListAdapter = new WalletListAdapter(hdWalletList);
                            reclWalletList.setAdapter(walletListAdapter);
                        }
                    }
                }
            }
        });
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.recl_add_wallet)
    public void onViewClicked() {
    }
}
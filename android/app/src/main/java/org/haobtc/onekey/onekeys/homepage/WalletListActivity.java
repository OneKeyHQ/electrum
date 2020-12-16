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

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.LoadWalletlistEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HDWalletActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateDeriveChooseTypeActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

/**
 * @author jinxiaomin
 */
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
    @BindView(R.id.text_wallet_type)
    TextView textWalletType;
    @BindView(R.id.img_w)
    ImageView imgW;
    private ArrayList<LocalWalletInfo> hdWalletList;
    private ArrayList<LocalWalletInfo> btcList;
    private ArrayList<LocalWalletInfo> ethList;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
    }

    @Override
    public void initData() {
        reclWalletList.setNestedScrollingEnabled(false);
        //wallet name and balance list
        hdWalletList = new ArrayList<>();
        //btc wallet list
        btcList = new ArrayList<>();
        //btc wallet list
        ethList = new ArrayList<>();
        //get wallet list
        getHomeWalletList();
    }

    @SingleClick
    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({R.id.img_close, R.id.recl_wallet_detail, R.id.lin_pair_wallet, R.id.lin_add_wallet, R.id.view_all, R.id.view_btc, R.id.view_eth, R.id.recl_add_wallet, R.id.img_add, R.id.img_w, R.id.recl_add_hd_wallet, R.id.recl_recovery_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.recl_wallet_detail:
                Intent intent4 = new Intent(WalletListActivity.this, HDWalletActivity.class);
                startActivity(intent4);
                break;
            case R.id.lin_pair_wallet:
                Intent intent = new Intent(this, SearchDevicesActivity.class);
                intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
                startActivity(intent);
                finish();
                break;
            case R.id.lin_add_wallet:
                createWalletChooseDialog(WalletListActivity.this, R.layout.add_wallet);
                break;
            case R.id.view_all:
                textWalletType.setText(getString(R.string.hd_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.hd_wallet_1));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                textWalletNum.setText(String.valueOf(hdWalletList.size()));
                reclWalletDetail.setVisibility(View.VISIBLE);
                imgAdd.setVisibility(View.GONE);
                imgW.setVisibility(View.VISIBLE);
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
                            edit.putString(Constant.CURRENT_SELECTED_WALLET_NAME, name);
                            edit.apply();
                            mIntent(HomeOneKeyActivity.class);
                        }
                    });
                }
                break;
            case R.id.view_btc:
                textWalletType.setText(getString(R.string.btc_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                reclAddHdWallet.setVisibility(View.GONE);//add hd wallet
                reclRecoveryWallet.setVisibility(View.GONE);//recovery wallet
                textWalletNum.setText(String.valueOf(btcList.size()));
                reclAddWallet.setVisibility(View.GONE);
                reclWalletDetail.setVisibility(View.GONE);
                imgW.setVisibility(View.GONE);
                if (hdWalletList == null || hdWalletList.size() == 0) {
                    imgAdd.setVisibility(View.GONE);
                } else {
                    imgAdd.setVisibility(View.VISIBLE);
                }
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
                            edit.putString(Constant.CURRENT_SELECTED_WALLET_NAME, name);
                            edit.apply();
                            mIntent(HomeOneKeyActivity.class);
                        }
                    });
                }
                break;
            case R.id.view_eth:
                textWalletType.setText(getString(R.string.eth_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.token_eth));
                textWalletNum.setText(String.valueOf(ethList.size()));
                reclAddHdWallet.setVisibility(View.GONE);//add hd wallet
                reclRecoveryWallet.setVisibility(View.GONE);//recovery wallet
                reclAddWallet.setVisibility(View.GONE);
                reclWalletDetail.setVisibility(View.GONE);
                imgAdd.setVisibility(View.VISIBLE);
                imgW.setVisibility(View.GONE);
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
                            edit.putString(Constant.CURRENT_SELECTED_WALLET_NAME, name);
                            edit.apply();
                            mIntent(HomeOneKeyActivity.class);
                        }
                    });
                }
                break;
            case R.id.img_add:
                Intent intent00 = new Intent(this, CreateWalletChooseTypeActivity.class);
                intent00.putExtra("ifHaveHd", hdWalletList.size());
                startActivity(intent00);
                break;
            case R.id.recl_add_wallet:
                Intent intent1 = new Intent(WalletListActivity.this, CreateDeriveChooseTypeActivity.class);
                intent1.putExtra(CURRENT_SELECTED_WALLET_TYPE, "derive");
                startActivity(intent1);
                break;
            case R.id.img_w:
                whatIsHd(WalletListActivity.this, R.layout.what_is_hd);
                break;
            case R.id.recl_add_hd_wallet:
                if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                    Intent intent0 = new Intent(this, SetHDWalletPassActivity.class);
                    startActivity(intent0);
                } else {
                    Intent intent0 = new Intent(this, SetLongPassActivity.class);
                    startActivity(intent0);
                }
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
        hdWalletList.clear();
        btcList.clear();
        ethList.clear();
//        PyObject getWalletsListInfo;
//        wallet list
        Map<String, ?> wallets = PreferencesManager.getAll(this, Constant.WALLETS);
        Log.i("walletslist", "getHomeWalletList: " + wallets);
        if (wallets.isEmpty()) {
            reclWalletDetail.setVisibility(View.GONE);
            reclAddWallet.setVisibility(View.GONE);
            reclAddHdWallet.setVisibility(View.VISIBLE);
            reclRecoveryWallet.setVisibility(View.VISIBLE);
        } else {
            wallets.entrySet().forEach(stringEntry -> {
                LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
                String type = info.getType();
                if ("btc-hd-standard".equals(type) || "btc-derived-standard".equals(type)) {
                    hdWalletList.add(info);
                }
                if (type.contains("btc")) {
                    btcList.add(info);
                } else if (type.contains("eth")) {
                    ethList.add(info);
                }
            });
            textWalletNum.setText(String.valueOf(hdWalletList.size()));
            if (hdWalletList == null || hdWalletList.size() == 0) {
                reclWalletDetail.setVisibility(View.GONE);
                reclAddWallet.setVisibility(View.GONE);
                reclAddHdWallet.setVisibility(View.VISIBLE);
                reclRecoveryWallet.setVisibility(View.VISIBLE);
            } else {
                reclAddWallet.setVisibility(View.VISIBLE);
                WalletListAdapter walletListAdapter = new WalletListAdapter(hdWalletList);
                reclWalletList.setAdapter(walletListAdapter);
                walletListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @SingleClick
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        String name = hdWalletList.get(position).getName();
                        edit.putString(Constant.CURRENT_SELECTED_WALLET_NAME, name);
                        edit.apply();
                        mIntent(HomeOneKeyActivity.class);
                    }
                });
            }
        }
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateWalletChooseTypeActivity.class);
            intent.putExtra("ifHaveHd", hdWalletList.size());
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
package org.haobtc.onekey.onekeys.homepage.process;
import android.view.View;

import com.lxj.xpopup.XPopup;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;
import org.haobtc.onekey.utils.NavUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.utils.LogUtil;

public class CreateDeriveChooseTypeActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_derive_choose_type;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_type_btc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_type_btc:
                new XPopup.Builder(mContext)
                        .asCustom(new SelectWalletTypeDialog(mContext, new SelectWalletTypeDialog.onClickListener() {
                            @Override
                            public void onClick (int mode) {
                                int purpose = 0;
                                switch (mode) {
                                    case SelectWalletTypeDialog.RecommendType:
                                        purpose = 49;
                                        break;
                                    case SelectWalletTypeDialog.NativeType:
                                        purpose = 84;
                                        break;
                                    case SelectWalletTypeDialog.NormalType:
                                        purpose = 44;
                                        break;
                                    default:
                                        break;
                                }
                                LogUtil.d("purpose  :" + purpose);
                                PreferencesManager.getSharedPreferences(mContext, Constant.myPreferences).edit().putInt(Constant.Wallet_Purpose, purpose).apply();
                                NavUtils.gotoSoftWalletNameSettingActivity(mContext, purpose);
                                finish();
                            }
                        })).show();
                break;
            default:
                break;
        }
    }
}
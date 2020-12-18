package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.view.View;

import com.lxj.xpopup.XPopup;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
                            public void onClick(int mode) {
                                switch (mode) {
                                    case SelectWalletTypeDialog.RecommendType:
                                    case SelectWalletTypeDialog.NativeType:
                                    case SelectWalletTypeDialog.NormalType:
                                        Intent intent = new Intent(mContext, SoftWalletNameSettingActivity.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                }
                            }
                        })).show();
                break;
            default:
                break;
        }
    }
}
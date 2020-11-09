package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MnemonicInfo;
import org.haobtc.onekey.mvp.base.BaseMvpActivity;
import org.haobtc.onekey.mvp.presenter.ImportMnemonicToDevicePresenter;
import org.haobtc.onekey.mvp.view.IImportMnemonicToDeviceView;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.GiveNameFragment;
import org.haobtc.onekey.ui.fragment.ImportMnemonicToDeviceFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePINFragment;
import org.haobtc.onekey.ui.listener.IAddAssetListener;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.IImportMnemonicToDeviceListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

import java.util.List;

import butterknife.BindView;

public class ImportMnemonicToDeviceActivity extends BaseMvpActivity<ImportMnemonicToDevicePresenter>
        implements View.OnClickListener, IImportMnemonicToDeviceListener, IImportMnemonicToDeviceView
        , ISetDevicePassListener, IColdDeviceConfirmListener, IGiveNameListener, IAddAssetListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new ImportMnemonicToDeviceFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onImport(List<MnemonicInfo> list) {
        if (mPresenter != null) {
            mPresenter.importMnemonics(list);
        }
    }


    @Override
    protected ImportMnemonicToDevicePresenter initPresenter() {
        return new ImportMnemonicToDevicePresenter(this);
    }

    @Override
    public void onImportMnemonicSuccess() {
        startFragment(new SetDevicePINFragment());
    }

    @Override
    public void onSetDevicePassSuccess() {
        startFragment(new ColdDeviceConfirmFragment());
    }

    @Override
    public void toNext() {
        startFragment(new GiveNameFragment());
    }

    @Override
    public void onWalletInitSuccess() {
        startFragment(new AddAssetFragment());
    }

    @Override
    public void onAddAssetsComplete() {
        toActivity(HomeOnekeyActivity.class);
    }
}

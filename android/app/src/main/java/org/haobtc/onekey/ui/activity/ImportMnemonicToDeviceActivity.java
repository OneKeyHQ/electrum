package org.haobtc.onekey.ui.activity;

import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.ImportMnemonicToDeviceFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class ImportMnemonicToDeviceActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public void init() {
        startFragment(new ImportMnemonicToDeviceFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked() {
        finish();
    }
}

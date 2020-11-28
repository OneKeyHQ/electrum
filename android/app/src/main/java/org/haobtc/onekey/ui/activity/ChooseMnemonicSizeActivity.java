package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.mvp.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/19/20
 */
/**
 * 选择助记词强度页面（12、24）
 * */
@Deprecated
class ChooseMnemonicSizeActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;

    @Override
    public void init() {
        ButterKnife.bind(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.choose_mnemonic_size;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tv1, R.id.tv2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv1:
                //TODO： 选择了12
                break;
            case R.id.tv2:
                //TODO： 选择了24
                break;
        }
    }
}

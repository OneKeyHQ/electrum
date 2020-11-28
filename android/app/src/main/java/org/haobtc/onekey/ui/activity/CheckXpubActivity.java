package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.utils.ClipboardUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/26/20
 */

public class CheckXpubActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.xpub_text)
    TextView xpubText;
    @BindView(R.id.copy)
    FrameLayout copy;
    private String xpub;

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.get_xpub);
        xpub = getIntent().getStringExtra(Constant.EXTEND_PUBLIC_KEY);
        xpubText.setText(xpub);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.check_xpub;
    }
    @SingleClick
    @OnClick({R.id.img_back, R.id.copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.copy:
                ClipboardUtils.copyText(this, xpub);
                break;
        }
    }
}

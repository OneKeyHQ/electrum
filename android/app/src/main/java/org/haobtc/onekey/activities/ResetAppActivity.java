package org.haobtc.onekey.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.FileNameConstant;
import org.haobtc.onekey.ui.dialog.custom.CustomResetBottomPopup;
import org.haobtc.onekey.utils.FileUtils;
import org.haobtc.onekey.utils.NavUtils;
import org.haobtc.onekey.utils.NoLeakHandler;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class ResetAppActivity extends BaseActivity implements OnCheckedChangeListener, NoLeakHandler.HandlerCallback {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    @BindView(R.id.btn_forward)
    Button btnForward;
    protected NoLeakHandler mHandler;
    private final String TAG = "ResetAppActivity";
    private static final int Reset_Code = 100;
    private RxPermissions rxPermissions;

    public static void gotoResetAppActivity (Context context) {
        context.startActivity(new Intent(context, ResetAppActivity.class));
    }

    @Override
    public int getLayoutId () {
        return R.layout.activity_reset_app;
    }

    @Override
    public void initView () {
        checkboxOk.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData () {
        rxPermissions = new RxPermissions(this);
        mHandler = new NoLeakHandler(this);
    }

    @SingleClick
    @OnClick({R.id.btn_forward})
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.btn_forward:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept (Boolean granted) throws Exception {
                                if (granted) {
                                    showDialog();
                                } else {
                                    Toast.makeText(mContext, R.string.photopersion, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }

    private void showDialog () {
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .isDestroyOnDismiss(true)
                .moveUpToKeyboard(false)
                .asCustom(new CustomResetBottomPopup(ResetAppActivity.this, () -> new Thread(() -> {
                    boolean isSuccess = FileUtils.resetApp(getFilesDir() + FileNameConstant.data_Path, getDataDir() + FileNameConstant.Sp_Path);
                    Message message = new Message();
                    message.what = Reset_Code;
                    message.obj = isSuccess;
                    mHandler.sendMessage(message);
                }).start(), CustomResetBottomPopup.resetApp))
                .show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            btnForward.setEnabled(true);
            btnForward.setBackground(getDrawable(R.drawable.delete_wallet_yes));
        } else {
            btnForward.setEnabled(false);
            btnForward.setBackground(getDrawable(R.drawable.delete_wallet_no));
        }
    }

    @Override
    public void handleMessage (Message msg) {
        switch (msg.what) {
            case Reset_Code:
                NavUtils.reStartApp(mContext);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mHandler.removeCallbacks(null);
        mHandler = null;
    }
}
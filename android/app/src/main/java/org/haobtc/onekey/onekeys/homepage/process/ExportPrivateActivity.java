package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;

import com.yzq.zxinglibrary.encode.CodeCreator;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.utils.ScreenShotListenManager;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExportPrivateActivity extends BaseActivity {

    @BindView(R.id.ima_private_code)
    ImageView imaPrivateCode;
    @BindView(R.id.text_private_key)
    TextView textPrivateKey;
    @BindView(R.id.text_show_code)
    TextView textShowCode;
    private boolean show = false;
    private ScreenShotListenManager screenShotListenManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_export_private;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().post(new SecondEvent("finishInputPass"));
        screenShotListenManager = new ScreenShotListenManager(this);
        startScreenShotListen();
    }

    @Override
    public void initData() {
        String privateKey = getIntent().getStringExtra("privateKey");
        textPrivateKey.setText(privateKey);
        Bitmap bitmap = CodeCreator.createQRCode(privateKey, 250, 250, null);
        imaPrivateCode.setImageBitmap(bitmap);
    }

    @OnClick({R.id.img_back, R.id.btn_next, R.id.img_copy, R.id.text_show_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_next:
                finish();
                break;
            case R.id.img_copy:
                //copy text
                ClipboardManager cm2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm2, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textPrivateKey.getText()));
                Toast.makeText(ExportPrivateActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.text_show_code:
                if (!show) {
                    imaPrivateCode.setVisibility(View.VISIBLE);
                    textShowCode.setText(getString(R.string.hide_code));
                    show = true;
                } else {
                    imaPrivateCode.setVisibility(View.GONE);
                    textShowCode.setText(getString(R.string.show_code));
                    show = false;
                }

                break;
        }
    }

    //禁止截屏监听
    private void startScreenShotListen() {
        if (screenShotListenManager != null) {
            screenShotListenManager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
                @Override
                public void onShot(String imagePath) {
                    screenTipDialog(ExportPrivateActivity.this, R.layout.screened);
                    screenShotListenManager.stopListen();
                    startScreenShotListen();
                }
            });
            screenShotListenManager.startListen();
        }
    }

    private void screenTipDialog(Context context, @LayoutRes int resource) {
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
}
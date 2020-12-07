package org.haobtc.onekey.onekeys.homepage.process;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.ImageUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveHDActivity extends AppCompatActivity {

    @BindView(R.id.img_Orcode)
    ImageView imgOrcode;
    @BindView(R.id.text_receive_address)
    TextView textReceiveAddress;
    @BindView(R.id.linear_check)
    LinearLayout linearCheck;
    @BindView(R.id.img_type)
    ImageView imgType;
    @BindView(R.id.text_send_type)
    TextView textSendType;
    @BindView(R.id.img_share_orcode)
    ImageView imgShareOrcode;
    @BindView(R.id.text_wallet_address_text)
    TextView textWalletAddressText;
    @BindView(R.id.text_wallet_address)
    TextView textWalletAddress;
    @BindView(R.id.lin_screen)
    LinearLayout linScreen;
    private RxPermissions rxPermissions;
    private Bitmap bitmap;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_h_d);
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String showWalletType = preferences.getString(Constant.CURRENT_SELECTED_WALLET_TYPE, "");
        rxPermissions = new RxPermissions(this);
        if (showWalletType.contains("eth")) {
            imgType.setImageDrawable(getDrawable(R.drawable.token_eth));
            textSendType.setText(String.format("%s ETH", getString(R.string.scan_send)));
            textWalletAddressText.setText(String.format("ETH %s", getString(R.string.wallet_address)));
        } else {
            textSendType.setText(String.format("%s BTC", getString(R.string.scan_send)));
            textWalletAddressText.setText(String.format("BTC %s", getString(R.string.wallet_address)));
        }
        mInitState();
        initData();

    }

    private void initData() {
        //get receive address
        mGeneratecode();

    }

    public void mInitState() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void mGeneratecode() {
        PyObject walletAddressShowUi = null;
        try {
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (walletAddressShowUi != null) {
            String strCode = walletAddressShowUi.toString();
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            String qrData = getCodeAddressBean.getQrData();
            String addr = getCodeAddressBean.getAddr();
            Log.i("strCode", "mGenerate--: " + strCode);
            textReceiveAddress.setText(addr);
            textWalletAddress.setText(addr);
            bitmap = CodeCreator.createQRCode(qrData, 250, 250, null);
            imgOrcode.setImageBitmap(bitmap);
            imgShareOrcode.setImageBitmap(bitmap);
        }

    }

    @OnClick({R.id.img_back, R.id.linear_check, R.id.linear_copy, R.id.linear_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.linear_check:
                break;
            case R.id.linear_copy:
                //copy text
                ClipboardManager cm2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm2, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textReceiveAddress.getText()));
                Toast.makeText(ReceiveHDActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.linear_share:
                try {
                    rxPermissions
                            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) { // Always true pre-M
                                    String shareImg = ImageUtils.viewSaveToImage(linScreen, "images");
                                    if (!TextUtils.isEmpty(shareImg)) {
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(shareImg));
                                        shareIntent.setType("image/*");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, textReceiveAddress.getText().toString());
                                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        shareIntent = Intent.createChooser(shareIntent, "Here is the title of Select box");
                                        startActivity(shareIntent);

                                    } else {
                                        Toast.makeText(this, getString(R.string.pictrue_fail), Toast.LENGTH_SHORT).show();
                                    }

                                } else { // Oups permission denied
                                    Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                                }
                            }).dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
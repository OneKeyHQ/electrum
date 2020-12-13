package org.haobtc.onekey.activities.personalwallet;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CurrentAddressDetail;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.utils.Daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppWalletCreateFinishActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_Orcode)
    ImageView imgOrcode;
    @BindView(R.id.tet_bigMessage)
    TextView tetBigMessage;
    @BindView(R.id.tet_Preservation)
    TextView tetPreservation;
    @BindView(R.id.rel_Finish)
    Button relFinish;
    @BindView(R.id.tet_Walleyname)
    TextView tetWalleyname;
    private RxPermissions rxPermissions;
    private Bitmap bitmap;

    @Override
    public int getLayoutId() {
        return R.layout.activity_app_wallet_create_finish;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String strName = intent.getStringExtra("strName");
        tetWalleyname.setText(strName);
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public void initData() {
        //Generate QR code
        mGeneratecode();


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
//            Log.i("strCode", "mGenerate--: " + strCode);
            Gson gson = new Gson();
            CurrentAddressDetail currentAddressDetail = gson.fromJson(strCode, CurrentAddressDetail.class);
            String qrData = currentAddressDetail.getQrData();
            bitmap = CodeCreator.createQRCode(qrData, 248, 248, null);
            imgOrcode.setImageBitmap(bitmap);
        }

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tet_Preservation, R.id.rel_Finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_Preservation:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                boolean toGallery = saveBitmap(bitmap);
                                if (toGallery) {
                                    mToast(getString(R.string.preservationbitmappic));
                                } else {
                                    Toast.makeText(this, R.string.preservationfail, Toast.LENGTH_SHORT).show();
                                }

                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.rel_Finish:
                EventBus.getDefault().post(new FirstEvent("11"));
                mIntent(MainActivity.class);
                finishAffinity();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public boolean saveBitmap(Bitmap bitmap) {
        try {
            File filePic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePic.getAbsolutePath())));
            return success;

        } catch (IOException ignored) {
            return false;
        }
    }

}

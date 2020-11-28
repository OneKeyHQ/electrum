package org.haobtc.onekey.activities.personalwallet;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.onekey.adapter.PublicPersonAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.bean.XpubItem;
import org.haobtc.onekey.event.AddBixinKeyEvent;
import org.haobtc.onekey.utils.Daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatFinishPersonalActivity extends BaseActivity {

    @BindView(R.id.tetWalletname)
    TextView tetWalletname;
    @BindView(R.id.img_Orcode)
    ImageView imgOrcode;
    @BindView(R.id.tet_Preservation)
    TextView tetPreservation;
    @BindView(R.id.recl_keyView)
    RecyclerView reclKeyView;
    private Bitmap bitmap;
    private String walletNames;
    private String flagTag;
    private ArrayList<XpubItem> keyList;
    private Intent intent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_creat_finish_personal;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //Whether to set pin after activation
        intent = getIntent();
        walletNames = intent.getStringExtra("walletNames");
        flagTag = intent.getStringExtra("flagTag");
        tetWalletname.setText(walletNames);

    }

    @Override
    public void initData() {
        boolean needBackup = intent.getBooleanExtra("needBackup", false);
        if (needBackup) {
            //show set PIN dialog
            showBackupDialog();
        }

        keyList = new ArrayList<>();
        //get wallet QR code
        mGeneratecode();
        //all bixinkey
        checkAllBixinkey();

    }

    //show Whether to set pin after activation dialog
    private void showBackupDialog() {
        View view1 = LayoutInflater.from(this).inflate(R.layout.to_backup, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view1).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view1.findViewById(R.id.test_no_yet).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        view1.findViewById(R.id.test_to_backup).setOnClickListener(v -> {
            Intent intent = new Intent(CreatFinishPersonalActivity.this, BackupRecoveryActivity.class);
            intent.putExtra("home_un_backup", "create_to_backup");
            startActivity(intent);
            finish();
        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    private void checkAllBixinkey() {
        if ("personal".equals(flagTag)) {
            String strBixinname = intent.getStringExtra("strBixinname");
            keyList.add(new XpubItem(strBixinname, ""));
            //public person
            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(keyList);
            reclKeyView.setAdapter(publicPersonAdapter);

        } else {
            List bixinKeylist = (List) intent.getSerializableExtra("strBixinlist");
            for (int i = 0; i < bixinKeylist.size(); i++) {
                keyList.add((XpubItem) bixinKeylist.get(i));
            }
            //public person
            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(keyList);
            reclKeyView.setAdapter(publicPersonAdapter);

        }

    }

    @SingleClick
    @OnClick({R.id.tet_Preservation, R.id.bn_complete_add_cosigner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tet_Preservation:
                boolean toGallery = saveBitmap(bitmap);
                if (toGallery) {
                    mToast(getString(R.string.preservationbitmappic));
                } else {
                    Toast.makeText(this, R.string.preservationfail, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bn_complete_add_cosigner:
                mIntent(MainActivity.class);
                finishAffinity();
                break;
            default:
        }
    }

    private void mGeneratecode() {
        PyObject walletAddressShowUi = null;
        try {
            Daemon.commands.callAttr("select_wallet", walletNames);
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (walletAddressShowUi != null) {
            String strCode = walletAddressShowUi.toString();
            Log.i("strCode", "mGenerate--: " + strCode);
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            String qrData = getCodeAddressBean.getQrData();
            bitmap = CodeCreator.createQRCode(qrData, 268, 268, null);
            imgOrcode.setImageBitmap(bitmap);
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

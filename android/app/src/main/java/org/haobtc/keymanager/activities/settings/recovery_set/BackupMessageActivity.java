package org.haobtc.keymanager.activities.settings.recovery_set;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.FinishEvent;
import org.haobtc.keymanager.event.HandlerEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class BackupMessageActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_Keyname)
    TextView tetKeyname;
    @BindView(R.id.backup_image)
    ImageView backupImage;
    @BindView(R.id.tet_preversation)
    TextView tetPreversation;
    @BindView(R.id.backup_message)
    TextView backupMessage;
    @BindView(R.id.copy_tet)
    TextView copyTet;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.tet_bigMessage)
    TextView tetBigMessage;
    private RxPermissions rxPermissions;
    private Intent intent;
    private String tag;
    private String message;
    private Bitmap bitmap;
    private String bleName;
    public final static String TAG = BackupMessageActivity.class.getSimpleName();

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_message;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);
        intent = getIntent();
        inits();
    }

    private void inits() {

        String strKeyname = intent.getStringExtra("label");
        tag = intent.getStringExtra("tag");
        message = intent.getStringExtra("message");
        bleName = intent.getStringExtra("ble_name");
        tetKeyname.setText(strKeyname);
        backupMessage.setText(message);
        if (!TextUtils.isEmpty(tag)) {
            if ("recovery".equals(tag)) {
                btnContinue.setText(getString(R.string.recover_backup));
            } else {
                btnContinue.setText(getString(R.string.finish));
            }
        }
        if (!TextUtils.isEmpty(message)) {
            bitmap = syncEncodeQRCode(message, dp2px(250), Color.parseColor("#000000"), Color.parseColor("#ffffff"), null);
            backupImage.setImageBitmap(bitmap);
        }
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static final Map<EncodeHintType, Object> HINTS = new EnumMap<>(EncodeHintType.class);

    static {
        HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        HINTS.put(EncodeHintType.MARGIN, 0);
    }

    public Bitmap syncEncodeQRCode(String content, int size, int foregroundColor, int backgroundColor, Bitmap logo) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, HINTS);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * size + x] = foregroundColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            if ("Data too big".equals(e.getMessage())) {
                backupImage.setVisibility(View.GONE);
                tetPreversation.setVisibility(View.GONE);
                tetBigMessage.setVisibility(View.VISIBLE);
            }
            return null;
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tet_preversation, R.id.copy_tet, R.id.btn_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                EventBus.getDefault().post(new FinishEvent());
                finish();
                break;
            case R.id.tet_preversation:
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
            case R.id.copy_tet:
                //copy text
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                assert cm != null;
                cm.setPrimaryClip(ClipData.newPlainText(null, backupMessage.getText()));
                Toast.makeText(BackupMessageActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_continue:
                if ("recovery".equals(tag)) {
                    // new version code
                    if (Ble.getInstance().getConnetedDevices().size() != 0) {
                        if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                            EventBus.getDefault().postSticky(new HandlerEvent());
                        }
                    }
                    Intent intent = new Intent(this, CommunicationModeSelector.class);
                    intent.putExtra("tag", TAG);
                    intent.putExtra("extras", message);
                    startActivity(intent);
                } else {
                    EventBus.getDefault().post(new FinishEvent());
                    finish();
                }
                break;
            default:
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}

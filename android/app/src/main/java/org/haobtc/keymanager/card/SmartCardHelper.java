package org.haobtc.keymanager.card;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.Backup2KeyLiteSuccess;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.card.entries.CardResponse;
import org.haobtc.keymanager.card.entries.SecureChanelParam;
import org.haobtc.keymanager.card.gpchannel.GPChannelNatives;
import org.haobtc.keymanager.card.utils.Utils;
import org.haobtc.keymanager.event.ExitEvent;
import org.haobtc.keymanager.utils.JsonParseUtils;
import org.haobtc.keymanager.utils.NfcUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class SmartCardHelper extends BaseActivity {

    public static final String TAG = SmartCardHelper.class.getSimpleName();
    private final static long CLA_SELECT = 0x00;
    private final static long INS_SELECT = 0xA4;
    private final static long P1_SELECT = 0x04;
    private final static long P2_SELECT = 0x00;
    private final static String APP_AID = "D156000132834001";
    private final static String STATUS_SUCCESS = "9000";
    @BindView(R.id.radio_ble)
    RadioButton radioBle;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.text_prompt)
    TextView textPrompt;
    @BindView(R.id.touch_lite)
    ImageView imageView;
    private String actionName;
    private String extras;
    private String backupMessage;
    private IsoDep isoDep;
    private boolean retry;
    private String pin;
    private AnimationDrawable animationDrawable;
    private boolean needNewPIN;


    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_recovery_lite;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        radioBle.setVisibility(View.GONE);
        actionName = getIntent().getAction();
        int step = getIntent().getIntExtra("step", 0);
        if (step == 1) {
            textPrompt.setText(R.string.step_one);
        } else if (step == 3 && "backup".equals(actionName)) {
            textPrompt.setText(R.string.step_three);
        }
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
        animationDrawable.selectDrawable(0);
        animationDrawable.start();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
    }

    @Override
    public void initData() {
        extras = getIntent().getStringExtra("extras");
    }

    @OnClick(R.id.img_cancel)
    public void onViewClicked(View v) {
        if (v.getId() == R.id.img_cancel) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // get the action of the coming intent
        String action = intent.getAction();
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED)
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            animationDrawable.stop();
            CommunicationModeSelector.nfcTag = null;
            isoDep = IsoDep.get(tags);
            switch (actionName) {
                case "backup":
                    if (retry && !Strings.isNullOrEmpty(pin)) {
                        verifyPin(pin);
                        return;
                    }
                    doBackup();
                    break;
                case "recovery":
                    doRecovery();
                    break;
                default:
            }
        }
    }

    private void doBackup() {
        boolean ok = init();
        if (!ok) {
            finish();
            return;
        }
        boolean selected = selectBackupApp();
        if (!selected) {
            finish();
            return;
        }
        // 是否从bixinKey获取到了备份信息
        if (Strings.isNullOrEmpty(backupMessage)) {
            if (hasBackup()) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this).setTitle("warring")
                        .setMessage("卡片内已有一份备份，是否覆盖原有备份")
                        .setNegativeButton(R.string.cancel, (dialog1, which) -> {
                            finish();
                        })
                        .setPositiveButton(R.string.confirm, (dialog12, which) -> {
                            exportBixinKeySeed();
                        })
                        .create();
                dialog.show();
            } else {
                exportBixinKeySeed();
            }
        } else {
            // 80cb8000 05 dfff028105
            if (!selectIssuerSd()) {
                finish();
                return;
            }
            String apdu = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0xCB, 0x80, 0x00, "DFFF028105");
            Log.d("get rootKey status apdu", apdu);
            String status = send(apdu);
            if (Strings.isNullOrEmpty(status)) {
                finish();
                return;
            }
            Log.d("get rootKey status response", status);
            Intent intent1 = new Intent(this, CardPin.class);
            intent1.setAction("backup");
            if ("029000".equals(status)) {
                intent1.putExtra("set", true);
                needNewPIN = true;
            }
            startActivityForResult(intent1, 2);
        }
    }

    private void doRecovery() {
        // 2. select applet
        boolean ok1 = init();
        if (!ok1) {
            Log.d("Select failed", "Select failed");
            finish();
            return;
        }
        boolean selected1 = selectBackupApp();
        if (!selected1) {
            finish();
            return;
        }
        if (!hasBackup()) {
            Toast.makeText(this, R.string.backup_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String cert = getCardCert();
        if (Strings.isNullOrEmpty(cert)) {
            return;
        }
        // 开启安全通道并验证PIN
        // 80 2A 18 10
        if (openSecureChannelFailed()) {
            return;
        }
        pin = getIntent().getStringExtra("pin");
        if (Strings.isNullOrEmpty(pin)) {
            return;
        }
        // 验证PIN
        String verifyPin = GPChannelNatives.nativeGPCBuildSafeAPDU(0x80, 0x20, 0x00, 0x00, "06" + Utils.stringToHexString(pin));
        String response = send(verifyPin);
        if (Strings.isNullOrEmpty(response)) {
            return;
        }
        Log.d("pin verify", response);
        String data = verifyBixinKeyCert(extras);
        // 导出种子 80 4A 00 00
        String export = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x4A, 0x00, 0x00, data);
        String res1 = send(export);
        if (Strings.isNullOrEmpty(res1)) {
            return;
        }
        res1 = res1.substring(0, res1.length() - 4);
        // 导入指令 00F9010000
//        String data1 = Integer.toHexString(cert.length() / 2) + cert + res1;
//        String apdu = GPChannelNatives.nativeGPCBuildAPDU(0x00, 0xF9, 0x01, 0x00, "00" + data1);
//        Intent intent1 = new Intent(this, BackupHelper.class);
//        intent1.setAction("done");
//        intent1.putExtra("message", apdu);
//        startActivity(intent1);
//        finish();
    }

    private void exportBixinKeySeed() {
        String cert = getCardCert();
        if (Strings.isNullOrEmpty(cert)) {
            finish();
            return;
        }
        // #2.NFC:Get ePK.SD.ECKA and Sign.ePK.SD.ECKA  command = "805A0000" 获取智能卡公钥和签名
        String getAuth = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x5A, 0x00, 0x00, "");
        String rawAuthData = send(getAuth);
        if (Strings.isNullOrEmpty(rawAuthData)) {
            finish();
            return;
        }
        String authData = GPChannelNatives.nativeGPCParseAPDUResponse(rawAuthData);
        authData = CardResponse.objectFromData(authData).getResponse();
        String data = Integer.toHexString(cert.length() / 2) + cert + authData;
        // #3.BX:Send data to BiXin wallet 导出BixinKEY 种子 (需要先查看硬件是否已激活)
//        String bixinExport = GPChannelNatives.nativeGPCBuildAPDU(0x00, 0xF9, 0x00, 0x00, "00" + data);
//        Intent intent1 = new Intent(this, BackupHelper.class);
//        intent1.putExtra("step", 2);
//        intent1.putExtra("message", bixinExport);
//        intent1.setAction("backup2card");
//        startActivityForResult(intent1, 3);
    }

    private boolean hasBackup() {
        String backupStatus = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x6A, 0x00, 0x00, "");
        String res = send(backupStatus);
        if (Strings.isNullOrEmpty(res)) {
            return false;
        }
        if (!res.endsWith(STATUS_SUCCESS)) {
            return false;
        }
        return "019000".equals(res);
    }

    private boolean init() {
        // 1. 初始化安全通道设置
        int status1 = GPChannelNatives.nativeGPCInitialize(JsonParseUtils.getJsonStr(this, "initParams.json"));
        if (status1 != 0) {
            Log.d("GP init", "init error");
            return false;
        }
        return true;
    }

    private String getCardCert() {
        // #1.NFC:GET CERT.SD.ECKA 获取智能卡证书
        String getCertCommand = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0xCA, 0xBF, 0x21, "A60483021518");
        Log.d("Get cert==", getCertCommand);
        String rawCert = send(getCertCommand);
        if (Strings.isNullOrEmpty(rawCert)) {
            Log.d("cert cert return", null);
            return null;
        }
        Log.d("raw cert==", rawCert);
        String cert = GPChannelNatives.nativeGPCParseAPDUResponse(rawCert);
        cert = GPChannelNatives.nativeGPCTLVDecode(CardResponse.objectFromData(cert).getResponse());
        cert = CardResponse.objectFromData(cert).getResponse();
        return cert;
    }

    private boolean selectIssuerSd() {
        String selectSd = GPChannelNatives.nativeGPCBuildAPDU(CLA_SELECT, INS_SELECT, P1_SELECT, P2_SELECT, "");
        Log.d("Select Issuer Domain", selectSd);
        String res = send(selectSd);
        if (Strings.isNullOrEmpty(res)) {
            return false;
        }
        Log.d("Select Issuer Domain res", res);
        return res.endsWith(STATUS_SUCCESS);
    }

    private boolean selectBackupApp() {
        String selectApp = GPChannelNatives.nativeGPCBuildAPDU(CLA_SELECT, INS_SELECT, P1_SELECT, P2_SELECT, APP_AID);
        Log.d("Select App", selectApp);
        String res = send(selectApp);
        if (Strings.isNullOrEmpty(res)) {
           return false;
        }
        Log.d("Select App", res);
        return STATUS_SUCCESS.equals(res);
    }

    private String send(String request) {
        String response = null;
        try {
            if (!isoDep.isConnected()) {
                isoDep.connect();
            }
            response = Utils.byteArr2HexStr(isoDep.transceive(Utils.hexString2Bytes(request)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            Optional.ofNullable(data).ifPresent((d) -> {
                pin = d.getStringExtra("pin");
                verifyPin(pin);
            });
        } else if (resultCode == Activity.RESULT_OK && requestCode == 3) {
            Optional.ofNullable(data).ifPresent((d) -> {
                animationDrawable.start();
                textPrompt.setText(R.string.step_three);
                backupMessage = d.getStringExtra("res");
            });
        }
    }

    private void verifyPin(String pin) {
        // 80 2A 18 10
        boolean ok = init();
        if (!ok) {
           retry = true;
           return;
        }
        if (needNewPIN) {
            if (openSecureChannelFailed()) {
                return;
            }
            String changePin = GPChannelNatives.nativeGPCBuildSafeAPDU(0x80, 0xCB, 0x80, 0x00, "DFFE0B8204080006" + Utils.stringToHexString(pin));
            String res = send(changePin);
            if (Strings.isNullOrEmpty(res)) {
                retry = true;
                return;
            }
            Log.d("Change pin res", res);
            if (!res.endsWith(STATUS_SUCCESS)) {
                retry = true;
                return;
            }
//            res = GPChannelNatives.nativeGPCParseAPDUResponse(res);
            Intent intent1 = new Intent(this, CardPin.class);
            intent1.setAction("backup");
            needNewPIN = false;
            startActivityForResult(intent1, 2);

        } else {
            assert pin != null;
            if (!selectBackupApp()) {
                retry = true;
                return;
            }
            if (openSecureChannelFailed()) {
                retry = true;
                return;
            }
            String verifyPin = GPChannelNatives.nativeGPCBuildSafeAPDU(0x80, 0x20, 0x00, 0x00, "06" + Utils.stringToHexString(pin));
            Log.d("pin verify apdu", verifyPin);
            String response = send(verifyPin);
            if (Strings.isNullOrEmpty(response)) {
                retry = true;
                return;
            }
            Log.d("pin verify res", response);
            if (response.endsWith("9000")) {
                response = GPChannelNatives.nativeGPCParseSafeAPDUResponse(response);
                Log.d("card pin verify", response);

            } else {
                retry = true;
                Toast.makeText(this, "card pin verify failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证BixinKEY证书
            String data = verifyBixinKeyCert(backupMessage);
            if (Strings.isNullOrEmpty(data)) {
                retry = true;
                return;
            }
            // 导入到卡
            String importSeed = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x3A, 0x00, 0x00, data);
            String importRes = send(importSeed);
            if (Strings.isNullOrEmpty(importRes)) {
                retry = true;
                return;
            }
            Log.d("import seed to card", importRes);
            if (!STATUS_SUCCESS.equals(importRes)) {
                retry = true;
                return;
            }
            startActivity(new Intent(this, Backup2KeyLiteSuccess.class));
            finish();
        }
    }

    private String verifyBixinKeyCert(String rawData) {
        int certLen = Integer.parseInt(rawData.substring(0, 4), 16) * 2;
        int originCertLen = certLen;
        String cert = rawData.substring(0, 4 + certLen);
        int offset = 0;
        int blockSize = 480;
        // 数据过长，需要分包
        while (certLen > blockSize) {
            String verifyCertPortion = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x30, 0x01, 0x00, cert.substring(offset, offset + blockSize));
            String res = send(verifyCertPortion);
            if (Strings.isNullOrEmpty(res)) {
                return null;
            }
            Log.d("verify cert first step", res);
            if (!STATUS_SUCCESS.equals(res)) {
                return null;
            }
            offset = offset + blockSize;
            certLen = certLen - blockSize;
        }
        String verifyCertLeft = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x30, 0x00, 0x00, cert.substring(offset));
        String res = send(verifyCertLeft);
        if (Strings.isNullOrEmpty(res)) {
            return null;
        }
        Log.d("verify cert second step", res);
        if (!STATUS_SUCCESS.equals(res)) {
            return null;
        }
        return rawData.substring(4 + originCertLen);
    }

    private boolean openSecureChannelFailed() {
        String param = JsonParseUtils.getJsonStr(this, "initParams.json");
        SecureChanelParam chanelParam = SecureChanelParam.objectFromData(param);
        // prepare to open secure channel
        String step1 = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x2A, 0x18, 0x10, chanelParam.getCrt());
        Log.d("Open secure channel step 1 apdu=======", step1);
        // 0x80, 0x82, 0x18, 0x15
        String res1 = send(step1);
        if (Strings.isNullOrEmpty(res1)) {
            Log.e("Open secure channel step 1", "Failed");
            retry = true;
            return true;
        }
        Log.d("Open secure channel step 1 response", res1);
        String authData = GPChannelNatives.nativeGPCBuildMutualAuthData();
        String step2 = GPChannelNatives.nativeGPCBuildAPDU(0x80, 0x82, 0x18, 0x15, authData);
        Log.d("Open secure channel step 2 apdu", step2);
        String authRes = send(step2);
        if (Strings.isNullOrEmpty(authRes)) {
            Log.e("Open secure channel step 2", "Failed");
            retry = true;
            return true;
        }
        Log.d("Open secure channel step 2 response", authRes);
        String res = CardResponse.objectFromData(GPChannelNatives.nativeGPCParseAPDUResponse(authRes)).getResponse();
        int status = GPChannelNatives.nativeGPCOpenSecureChannel(res);
        if (status != 0) {
            Log.e("Open secure channel step 3", "Failed");
            retry = true;
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (retry && !Strings.isNullOrEmpty(pin)) {
            animationDrawable.start();
            textPrompt.setText(R.string.step_four);
        }
    }
    @Subscribe
    public void onExit(ExitEvent event) {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }
}

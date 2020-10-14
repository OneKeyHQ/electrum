package org.haobtc.onekey.activities.settings.recovery_set;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.adapter.BixinkeyBackupAdapter;
import org.haobtc.onekey.aop.SingleClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoveryActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.edit_text1)
    EditText editText1;
    @BindView(R.id.bn_scan)
    ImageView bnScan;
    @BindView(R.id.bn_paste)
    TextView bnPaste;
    @BindView(R.id.btn_recovery)
    Button btnRecovery1;
    public final static String TAG = "org.haobtc.onekey.activities.settings.recovery_set.RecoveryActivity";
    @BindView(R.id.backup_list)
    RecyclerView backupList;
    private List<String> deviceValue;
    private boolean isScan;

    @Override
    public int getLayoutId() {
        return R.layout.recovery;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceValue = new ArrayList<>();
        SharedPreferences backup = getSharedPreferences("backup", MODE_PRIVATE);
        Map<String, ?> backAll = backup.getAll();
        //key
        for (Map.Entry<String, ?> entry : backAll.entrySet()) {
            String mapValue = (String) entry.getValue();
            if (!TextUtils.isEmpty(mapValue)) {
                deviceValue.add(entry.getKey() + ":" + mapValue);
            }
        }
        if (deviceValue != null) {
            BixinkeyBackupAdapter backupAdapter = new BixinkeyBackupAdapter(deviceValue);
            backupList.setAdapter(backupAdapter);
            backupAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @SingleClick
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (view.getId()) {
                        case R.id.relativeLayout_bixinkey:
//                            Intent intent = new Intent(RecoveryActivity.this, BackupMessageActivity.class);
//                            intent.putExtra("label", Strings.isNullOrEmpty(deviceValue.get(position).getLabel()) ? deviceValue.get(position).getBleName(): deviceValue.get(position).getLabel());
//                            intent.putExtra("message",deviceValue.get(position).getBackupMessage());
//                            intent.putExtra("tag","recovery");
//                            startActivity(intent);
                            editText1.getText().clear();
                            editText1.setText(deviceValue.get(position).split(":", 3)[2]);
                            break;
                        case R.id.linear_delete:
//                            String blename = deviceValue.get(position).getBleName();
//                            edit.remove(blename);
//                            edit.apply();
//                            deviceValue.remove(position);
//                            bixinkeyManagerAdapter.notifyItemChanged(position);
//                            bixinkeyManagerAdapter.notifyDataSetChanged();
//                            mToast(getString(R.string.delete_succse));
                            mlToast("delete is forbidden");
                            break;
                        default:
                    }
                }
            });
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.bn_scan, R.id.bn_paste, R.id.btn_recovery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.bn_scan:
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(this, CaptureActivity.class);
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);
                                config.setShake(true);
                                config.setDecodeBarCode(false);
                                config.setFullScreenScan(true);
                                config.setShowAlbum(false);
                                config.setShowbottomLayout(false);
                                intent2.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent2, 0);
                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.bn_paste:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData data = clipboard.getPrimaryClip();
                    if (data != null && data.getItemCount() > 0) {
                        editText1.setText(data.getItemAt(0).getText());
                    }
                }
                break;
            case R.id.btn_recovery:
                if (editText1.getText().length() == 0) {
                    Toast.makeText(this, getString(R.string.dont_none), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("extras", editText1.getText().toString());
                intent.putExtra("tag", TAG);
                startActivity(intent);
                isScan = false;
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                isScan = true;
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editText1.setText(content);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isScan) {
            finish();
        }
    }
}


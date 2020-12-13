package org.haobtc.onekey.activities.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.BixinkeyManagerAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class OneKeyManageActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.recl_bixinKey_list)
    RecyclerView reclBixinKeyList;
    private List<HardwareFeatures> deviceValue;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bixin_keymenage;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getKeyList();
    }

    private void getKeyList() {
        deviceValue = new ArrayList<>();
        Map<String, ?> devicesAll = PreferencesManager.getAll(this, Constant.DEVICES);;
        //key
        for (Map.Entry<String, ?> entry : devicesAll.entrySet()) {
            String mapValue = (String) entry.getValue();
            HardwareFeatures hardwareFeatures = new Gson().fromJson(mapValue, HardwareFeatures.class);
            deviceValue.add(hardwareFeatures);
        }
        if (deviceValue != null) {
            BixinkeyManagerAdapter bixinkeyManagerAdapter = new BixinkeyManagerAdapter(deviceValue);
            reclBixinKeyList.setAdapter(bixinkeyManagerAdapter);
            bixinkeyManagerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @SingleClick
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.relativeLayout_bixinkey) {
                        String firmwareVersion = deviceValue.get(position).getMajorVersion() + "." + deviceValue.get(position).getMinorVersion() + "." + deviceValue.get(position).getPatchVersion();
                        String nrfVersion = deviceValue.get(position).getBleVer();
                        String label = Strings.isNullOrEmpty(deviceValue.get(position).getLabel()) ?
                                deviceValue.get(position).getBleName() : deviceValue.get(position).getLabel();
                        Intent intent = new Intent(OneKeyManageActivity.this, HardwareDetailsActivity.class);
                        intent.putExtra(Constant.TAG_LABEL, label);
                        intent.putExtra(Constant.TAG_BLE_NAME, deviceValue.get(position).getBleName());
                        intent.putExtra(Constant.TAG_FIRMWARE_VERSION, firmwareVersion);
                        intent.putExtra(Constant.TAG_NRF_VERSION, nrfVersion);
                        intent.putExtra(Constant.DEVICE_ID, deviceValue.get(position).getDeviceId());
                        intent.putExtra(Constant.TAG_HARDWARE_VERIFY, deviceValue.get(position).isVerify());
                        intent.putExtra(Constant.TAG_IS_BACKUP_ONLY, deviceValue.get(position).isBackupOnly());
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixBixinkeyNameEvent event) {
        getKeyList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}








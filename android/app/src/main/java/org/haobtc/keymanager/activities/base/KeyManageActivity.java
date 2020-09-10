package org.haobtc.keymanager.activities.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.key.KeySettingActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.activities.settings.HardwareDetailsActivity;
import org.haobtc.keymanager.adapter.HomeKeyAdapter;
import org.haobtc.keymanager.bean.HardwareFeatures;
import org.haobtc.keymanager.event.AddKeyEvent;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KeyManageActivity extends BaseActivity {

    public static final String TAG = KeyManageActivity.class.getSimpleName();
    @BindView(R.id.recl_keyList)
    RecyclerView reclKeyList;
    @BindView(R.id.tet_None)
    TextView tetNone;
    private ArrayList<HardwareFeatures> deviceValue;
    private HomeKeyAdapter homeKeyAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_manage;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        String firstRun = "is_first_run";
        edit.putBoolean(firstRun, true);
        edit.apply();

    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initData() {
        deviceValue = new ArrayList<>();
        homeKeyAdapter = new HomeKeyAdapter(deviceValue);
        reclKeyList.setAdapter(homeKeyAdapter);
        keyListData();

    }

    private void keyListData() {
        deviceValue.clear();
        SharedPreferences devices = getSharedPreferences("devices", MODE_PRIVATE);
        Map<String, ?> devicesAll = devices.getAll();
        //key
        for (Map.Entry<String, ?> entry : devicesAll.entrySet()) {
            String mapValue = (String) entry.getValue();
            HardwareFeatures hardwareFeatures = new Gson().fromJson(mapValue, HardwareFeatures.class);
            deviceValue.add(hardwareFeatures);
        }
        reclKeyList.setLayoutManager(new GridLayoutManager(KeyManageActivity.this, 2));
        homeKeyAdapter.notifyDataSetChanged();
        if (deviceValue.size() == 0) {
            tetNone.setVisibility(View.VISIBLE);
            reclKeyList.setVisibility(View.GONE);
        } else {
            tetNone.setVisibility(View.GONE);
            reclKeyList.setVisibility(View.VISIBLE);
        }

        homeKeyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String firmwareVersion = "V" + deviceValue.get(position).getMajorVersion() + "." + deviceValue.get(position).getMinorVersion() + "." + deviceValue.get(position).getPatchVersion();
                Intent intent = new Intent(KeyManageActivity.this, HardwareDetailsActivity.class);
                intent.putExtra("label", deviceValue.get(position).getLabel());
                intent.putExtra("bleName", deviceValue.get(position).getBleName());
                intent.putExtra("firmwareVersion", firmwareVersion);
                intent.putExtra("bleVerson", "V" + deviceValue.get(position).getMajorVersion() + "." + deviceValue.get(position).getPatchVersion());
                intent.putExtra("device_id", deviceValue.get(position).getDeviceId());
                intent.putExtra("whereIntent", true);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.img_back, R.id.tet_Addmoney})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                mIntent(KeySettingActivity.class);
                break;
            case R.id.tet_Addmoney:
                Intent intent1 = new Intent(this, CommunicationModeSelector.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("tag", TAG);
                startActivity(intent1);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(AddKeyEvent event) {
        keyListData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}


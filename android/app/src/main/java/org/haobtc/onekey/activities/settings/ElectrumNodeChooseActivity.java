package org.haobtc.onekey.activities.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.ElectrumListAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CNYBean;
import org.haobtc.onekey.bean.DefaultNodeBean;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.SendMoreAddressEvent;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ElectrumNodeChooseActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.recl_nodeChose)
    RecyclerView reclNodeChose;
    @BindView(R.id.btn_Finish)
    Button btnFinish;
    @BindView(R.id.edit_ip)
    EditText editIp;
    @BindView(R.id.edit_port)
    EditText editPort;
    private PyObject get_server_list;
    private ArrayList<CNYBean> electrumList;
    private int electrumNode;
    private ArrayList<SendMoreAddressEvent> addressEvents;

    @Override
    public int getLayoutId() {
        return R.layout.activity_electrum_node_choose;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        electrumNode = preferences.getInt("electrumNode", 0);
        reclNodeChose.setNestedScrollingEnabled(false);
        TextWatcher1 textWatcher1 = new TextWatcher1();
        editIp.addTextChangedListener(textWatcher1);
        editPort.addTextChangedListener(textWatcher1);
    }

    @Override
    public void initData() {
        electrumList = new ArrayList<>();
        addressEvents = new ArrayList<>();
        //get default node
        getDefaultNode();
        //get electrum list
        getElectrumData();

    }

    private void getDefaultNode() {
        try {
            PyObject defaultServer = Daemon.commands.callAttr("get_default_server");
            Log.i("defaultServerdddddd", "getDefaultNode: =====   " + defaultServer);
            Gson gson = new Gson();
            DefaultNodeBean defaultNodeBean = gson.fromJson(defaultServer.toString(), DefaultNodeBean.class);
            editIp.setText(defaultNodeBean.getHost());
            editPort.setText(defaultNodeBean.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getElectrumData() {
        try {
            get_server_list = Daemon.commands.callAttr("get_server_list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (get_server_list != null) {
            SendMoreAddressEvent sendMoreAddressEvent = new SendMoreAddressEvent();
            String get_server = get_server_list.toString();
            Log.i("get_server_list", "get_server_list: " + get_server);
            Map<String, Object> jsonToMap = JSONObject.parseObject(get_server);
            Set<String> keySets = jsonToMap.keySet();
            for (String k : keySets) {
                //get key
                String v = jsonToMap.get(k).toString();
                Map<String, Object> vToMap = JSONObject.parseObject(v);
                Set<String> vkeySets = vToMap.keySet();
                for (String vk : vkeySets) {
                    if ("s".equals(vk)) {
                        String vvalue = vToMap.get(vk).toString();
                        String strElectrum = k + ":" + vvalue;
                        sendMoreAddressEvent.setInputAddress(k);
                        sendMoreAddressEvent.setInputAmount(vvalue);
                        addressEvents.add(sendMoreAddressEvent);
                        CNYBean cnyBean = new CNYBean(strElectrum, false);
                        electrumList.add(cnyBean);
                    }
                }
            }
            ElectrumListAdapter electrumListAdapter = new ElectrumListAdapter(ElectrumNodeChooseActivity.this, electrumList, electrumNode);
            reclNodeChose.setAdapter(electrumListAdapter);
            electrumListAdapter.setOnLisennorClick(new ElectrumListAdapter.onLisennorClick() {
                @Override
                public void ItemClick(int pos) {
                    editIp.setText(addressEvents.get(pos).getInputAddress());
                    editPort.setText(addressEvents.get(pos).getInputAmount());
                }
            });
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_Finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_Finish:
                try {
                    Daemon.commands.callAttr("set_server", editIp.getText().toString(), editPort.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("ValueError")) {
                        mToast(getString(R.string.ipOrportWrong));
                    }
                    return;
                }
                mToast(getString(R.string.add_finished));
                break;
            default:
        }
    }

    class TextWatcher1 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if ((editIp.length() > 0 && editPort.length() > 0)) {
                btnFinish.setEnabled(true);
                btnFinish.setBackground(getDrawable(R.drawable.btn_checked));
            } else {
                btnFinish.setEnabled(false);
                btnFinish.setBackground(getDrawable(R.drawable.btn_no_check));
            }
        }
    }
}

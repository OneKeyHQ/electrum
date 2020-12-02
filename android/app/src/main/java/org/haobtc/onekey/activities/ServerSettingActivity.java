package org.haobtc.onekey.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.settings.AgentServerActivity;
import org.haobtc.onekey.activities.settings.AnyskServerSetActivity;
import org.haobtc.onekey.activities.settings.BlockChooseActivity;
import org.haobtc.onekey.activities.settings.ElectrumNodeChooseActivity;
import org.haobtc.onekey.activities.settings.QuotationServerActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.DefaultNodeBean;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.utils.Daemon;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServerSettingActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.rel_quotationChoose)
    RelativeLayout relQuotationChoose;
    @BindView(R.id.rel_Electrum_Choose)
    RelativeLayout relElectrumChoose;
    @BindView(R.id.tet_defaultServer)
    TextView tetDefaultServer;
    @BindView(R.id.testBlockcheck)
    TextView testBlockcheck;
    @BindView(R.id.testElectrumNode)
    TextView testElectrumNode;
    @BindView(R.id.testNodeType)
    TextView testNodeType;
    private SharedPreferences preferences;
    private String exchangeName;
    private String blockServerLine;

    @Override
    public int getLayoutId() {
        return R.layout.server_setting;
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        exchangeName = preferences.getString("exchangeName", "");
        blockServerLine = preferences.getString("blockServerLine", "https://btc.com/");
        inits();

    }

    private void inits() {
        //get default Server
        if (!TextUtils.isEmpty(exchangeName)) {
            tetDefaultServer.setText(exchangeName);
        } else {
            getdefaultServer();
        }
        //get block Browser
        if (!TextUtils.isEmpty(blockServerLine)) {
            testBlockcheck.setText(blockServerLine);
        }

    }

    @Override
    public void initData() {
        //get electrum list
        getElectrumData();
        //get now server address
        getServerAddress();
    }

    private void getElectrumData() {
        try {
            PyObject defaultServer = Daemon.commands.callAttr("get_default_server");
            Gson gson = new Gson();
            DefaultNodeBean defaultNodeBean = gson.fromJson(defaultServer.toString(), DefaultNodeBean.class);
            testElectrumNode.setText(String.format("%s:%s", defaultNodeBean.getHost(), defaultNodeBean.getPort()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get now server address
    private void getServerAddress() {
        try {
            PyObject get_sync_server_host = Daemon.commands.callAttr("get_sync_server_host");
            testNodeType.setText(get_sync_server_host.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //get default Server
    private void getdefaultServer() {
        PyObject getExchanges = null;
        try {
            getExchanges = Daemon.commands.callAttr("get_exchanges");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getExchanges != null) {
            Log.i("get_exchanges", "getExchangelist: " + getExchanges);
            String content = getExchanges.toString();
            String unit = content.replaceAll("\"", "");
            String[] pathArr = (unit.substring(1, unit.length() - 1)).split(",");
            List<String> pathList = Arrays.asList(pathArr);
            String defalutServer = pathList.get(0);
            tetDefaultServer.setText(defalutServer);
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_quotationChoose, R.id.rel_blockChoose, R.id.rel_Electrum_Choose, R.id.relAgent_Choose, R.id.rel_syn_server})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_quotationChoose:
                mIntent(QuotationServerActivity.class);
                break;
            case R.id.rel_blockChoose:
                mIntent(BlockChooseActivity.class);
                break;
            case R.id.rel_Electrum_Choose:
                mIntent(ElectrumNodeChooseActivity.class);
                break;
            case R.id.relAgent_Choose:
                mIntent(AgentServerActivity.class);
                break;
            case R.id.rel_syn_server:
                Intent intent = new Intent(ServerSettingActivity.this, AnyskServerSetActivity.class);
                intent.putExtra("ip_port", testNodeType.getText().toString());
                startActivity(intent);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(FirstEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("defaultServer".equals(msgVote)) {
            exchangeName = preferences.getString("exchangeName", "");
            tetDefaultServer.setText(exchangeName);
        } else if ("block_check".equals(msgVote)) {
            blockServerLine = preferences.getString("blockServerLine", "");
            testBlockcheck.setText(blockServerLine);
        } else if ("add_anysk_server".equals(msgVote)) {
            //get now server address
            getServerAddress();
        } else if ("fixElectrumNode".equals(msgVote)) {
            //get electrum list
            getElectrumData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

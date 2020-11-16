package org.haobtc.onekey.activities.personalwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.ImportHistryWalletAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.InputHistoryWalletEvent;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseHistryWalletActivity extends BaseActivity {

    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    @BindView(R.id.recl_importWallet)
    RecyclerView reclImportWallet;
    @BindView(R.id.btn_Finish)
    Button btnFinish;
    @BindView(R.id.test_no_wallet)
    TextView testNoWallet;
    private String historyXpub;
    private ArrayList<InputHistoryWalletEvent> walletList;
    private ImportHistryWalletAdapter histryWalletAdapter;
    private ArrayList<InputHistoryWalletEvent> list;
    private ArrayList<ArrayList<Object>> listDates;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_histry_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        historyXpub = intent.getStringExtra("history_xpub");
        Log.i("history_xpub", "initView: " + historyXpub);

    }

    @Override
    public void initData() {
        listDates = new ArrayList<>();//choose wallet data list
        walletList = new ArrayList<>();
        list = new ArrayList<>();
        histryWalletAdapter = new ImportHistryWalletAdapter(ChooseHistryWalletActivity.this, list);
        reclImportWallet.setAdapter(histryWalletAdapter);
        //get histry wallet
        getHistryWallet();

    }

    private void getHistryWallet() {
        PyObject infoFromServer = null;
        try {
            infoFromServer = Daemon.commands.callAttr("get_wallet_info_from_server", historyXpub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infoFromServer != null) {
            String strfromServer = infoFromServer.toString();
            Log.i("infoFromServer", "initData: " + infoFromServer);
            if (strfromServer.length() != 2) {
                try {
                    JSONArray jsonArray = new JSONArray(strfromServer);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String walletType = jsonObject.getString("walletType");
                        String xpubs = jsonObject.getString("xpubs");
                        String name = jsonObject.getString("walletName");
                        InputHistoryWalletEvent inputHistoryWalletEvent = new InputHistoryWalletEvent();
                        inputHistoryWalletEvent.setType(walletType);
                        inputHistoryWalletEvent.setXpubs(xpubs);
                        inputHistoryWalletEvent.setName(name);
                        walletList.add(inputHistoryWalletEvent);
                    }

                    boolean flag = false;
                    for (int i = 0; i < walletList.size(); i++) {
                        String c = walletList.get(i).getName();
                        if (list.size() > 0) {
                            InputHistoryWalletEvent inputHistoryWalletEvent = new InputHistoryWalletEvent();
                            int index = 1;
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getName().contains(c)) {
                                    list.get(j).setName(c + "(" + index + ")");
                                    flag = true;
                                    index++;
                                }
                            }
                            inputHistoryWalletEvent.setType(walletList.get(i).getType());
                            inputHistoryWalletEvent.setXpubs(walletList.get(i).getXpubs());
                            if (flag) {
                                inputHistoryWalletEvent.setName(c + "(" + index + ")");
                                list.add(inputHistoryWalletEvent);
                            } else {
                                inputHistoryWalletEvent.setName(c);
                                list.add(inputHistoryWalletEvent);
                            }
                        } else {
                            InputHistoryWalletEvent inputHistoryWalletEvent = new InputHistoryWalletEvent();
                            inputHistoryWalletEvent.setName(c);
                            inputHistoryWalletEvent.setType(walletList.get(i).getType());
                            inputHistoryWalletEvent.setXpubs(walletList.get(i).getXpubs());
                            list.add(inputHistoryWalletEvent);
                        }
                        flag = false;
                    }
                    histryWalletAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mToast(getString(R.string.no_import_wallet));
        }
    }

    @SingleClick
    @OnClick({R.id.img_backCreat, R.id.btn_Finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                finish();
                break;
            case R.id.btn_Finish:
                ArrayList<Object> onlyData;
                listDates.clear();
                Map<Integer, Boolean> map = histryWalletAdapter.getMap();
                for (int i = 0; i < list.size(); i++) {
                    onlyData = new ArrayList<>();
                    if (map.get(i)) {
                        String type = list.get(i).getType();
                        String m = type.substring(0, type.indexOf("-"));
                        String n = type.substring(type.indexOf("-") + 1);
                        onlyData.add(Integer.parseInt(m));
                        onlyData.add(Integer.parseInt(n));
                        onlyData.add(list.get(i).getName());
                        onlyData.add(list.get(i).getXpubs());
                        listDates.add(onlyData);
                    }
                }
                if (listDates != null && listDates.size() > 0) {
                    importWallet(new Gson().toJson(listDates));
                } else {
                    mToast(getString(R.string.please_import_wallet));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void importWallet(String listDates) {
        try {
            PyObject bulkCreateWallet = Daemon.commands.callAttr("bulk_create_wallet", listDates);
            String errorStr = bulkCreateWallet.toString();
            if (!TextUtils.isEmpty(errorStr)) {
                mToast(getString(R.string.some_wallet_existence));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
            return;
        }
        mIntent(MainActivity.class);

    }
}

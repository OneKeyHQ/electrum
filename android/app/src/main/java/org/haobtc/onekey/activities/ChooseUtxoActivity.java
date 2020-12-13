package org.haobtc.onekey.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.ChooseUtxoAdapter;
import org.haobtc.onekey.event.ChooseUtxoEvent;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.WALLET_BALANCE;

public class ChooseUtxoActivity extends BaseActivity {

    @BindView(R.id.text_all_num)
    TextView textAllNum;
    @BindView(R.id.text_unit)
    TextView textUnit;
    @BindView(R.id.recl_choose_utxo)
    RecyclerView reclChooseUtxo;
    @BindView(R.id.text_no_content)
    TextView textNoContent;
    @BindView(R.id.text_standard)
    TextView textStandard;
    private ArrayList<ChooseUtxoEvent> chooseUtxoList;
    private ArrayList<Map<String, String>> listDates;
    private ChooseUtxoAdapter chooseUtxoAdapter;
    private BigDecimal totalAmount;
    private String sendNum;
    private String mBtcUnit;
    private ArrayList<String> utxoPosList;
    private ArrayList<String> utxoPositionData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_utxo;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        sendNum = getIntent().getStringExtra(WALLET_BALANCE);//total num
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        mBtcUnit = preferences.getString("base_unit", "mBtc");
        textUnit.setText(mBtcUnit);
        textStandard.setText(String.format("%s %s %s", getString(R.string.utxo_tips), sendNum, mBtcUnit));
        utxoPositionData = getIntent().getStringArrayListExtra("utxoPositionData");
    }

    @Override
    public void initData() {
        utxoPosList = new ArrayList<>();
        listDates = new ArrayList<>();//choose UTXO data list
        chooseUtxoList = new ArrayList<>();
        //get utxo data
        getUtxoData();
        mOnlickFinish();
        chooseUtxoAdapter = new ChooseUtxoAdapter(ChooseUtxoActivity.this, chooseUtxoList, utxoPositionData);
        reclChooseUtxo.setAdapter(chooseUtxoAdapter);
    }

    private void getUtxoData() {
        try {
            PyObject getUnspendUtxos = Daemon.commands.callAttr("get_unspend_utxos");
            Log.i("utxoListDates", "getUtxoData---: " + getUnspendUtxos);
            if (getUnspendUtxos != null && getUnspendUtxos.size() != 0) {
                JSONArray jsonArray = new JSONArray(getUnspendUtxos.toString());
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ChooseUtxoEvent chooseUtxoEvent = new ChooseUtxoEvent();
                        chooseUtxoEvent.setHash(jsonObject.getString("prevout_hash"));
                        chooseUtxoEvent.setAddress(jsonObject.getString("address"));
                        chooseUtxoEvent.setValue(jsonObject.getString("value"));
                        chooseUtxoList.add(chooseUtxoEvent);
                    }
                    chooseUtxoAdapter.notifyDataSetChanged();

                } else {
                    reclChooseUtxo.setVisibility(View.GONE);
                    textNoContent.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reclChooseUtxo.setVisibility(View.GONE);
            textNoContent.setVisibility(View.VISIBLE);
        }

    }

    @OnClick({R.id.img_back, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_finish:
                Log.i("btn_finishbtn_finish", "onViewClicked: " + utxoPosList.toString());
                if (listDates != null && listDates.size() > 0) {
                    if (totalAmount.compareTo(new BigDecimal(sendNum)) == 1) {
                        Intent intent = new Intent();
                        intent.putExtra("chooseNum", listDates.size() + "");
                        intent.putExtra("listDates", new Gson().toJson(listDates));
                        intent.putStringArrayListExtra("UtxoPosData", utxoPosList);
                        intent.putExtra("sumUtxo", totalAmount.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        mToast(String.format("%s%s%s%s", getString(R.string.choose), getString(R.string.utxo_tips), sendNum, mBtcUnit));
                    }
                } else {
                    mToast(getString(R.string.please_choose));
                }
                break;
        }
    }

    private void mOnlickFinish() {
        utxoPosList.clear();
        totalAmount = new BigDecimal("0");
        Map<String, String> pramas = null;
        listDates.clear();
        Map<Integer, Boolean> map = chooseUtxoAdapter.getMap();
        for (int i = 0; i < chooseUtxoList.size(); i++) {
            pramas = new HashMap();
            if (map.get(i)) {
                String bitAmount = chooseUtxoList.get(i).getValue().substring(0, chooseUtxoList.get(i).getValue().indexOf(" "));
                BigDecimal bignum1 = new BigDecimal(bitAmount);
                //Total transfer quantity
                totalAmount = totalAmount.add(bignum1);
                pramas.put(chooseUtxoList.get(i).getHash(), chooseUtxoList.get(i).getAddress());
                if (!utxoPosList.contains(chooseUtxoList.get(i).getHash())) {
                    utxoPosList.add(chooseUtxoList.get(i).getHash());
                }
                listDates.add(pramas);
            }
        }
        textAllNum.setText(String.valueOf(totalAmount));
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(ChooseUtxoEvent updataHint) {
        mOnlickFinish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

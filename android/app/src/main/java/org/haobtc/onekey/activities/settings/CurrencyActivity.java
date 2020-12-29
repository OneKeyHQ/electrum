package org.haobtc.onekey.activities.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.CNYAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CNYBean;
import org.haobtc.onekey.event.CardUnitEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;
import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_SYMBOL;

public class CurrencyActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.radio_one)
    RadioGroup radioOne;
    @BindView(R.id.recl_cnyTable)
    RecyclerView reclCnyTable;
    @BindView(R.id.tet_CheckAll)
    TextView tetCheckAll;
    @BindView(R.id.img_btc_check)
    ImageView imgBtcCheck;
    @BindView(R.id.img_mbtc_check)
    ImageView imgMbtcCheck;
    @BindView(R.id.img_bits_check)
    ImageView imgBitsCheck;
    @BindView(R.id.img_sat_check)
    ImageView imgSatCheck;
    private SharedPreferences.Editor edit;
    private String base_unit;
    private ArrayList<CNYBean> listCNY;
    private int cny_unit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        base_unit = preferences.getString("base_unit", "mBTC");
        cny_unit = preferences.getInt("cny_unit", 0);
        edit = preferences.edit();
    }

    @Override
    public void initData() {
        reclCnyTable.setNestedScrollingEnabled(false);
        listCNY = new ArrayList<>();
        //BTC
        radioSelectOne();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //CNY
                radioSelectTwo();
            }
        }, 200);
    }

    private void radioSelectOne() {
        RadioButton[] radioOnearray = new RadioButton[radioOne.getChildCount()];
        for (int i = 0; i < radioOnearray.length; i++) {
            radioOnearray[i] = (RadioButton) radioOne.getChildAt(i);
        }
        switch (base_unit) {
            case "BTC":
                radioOnearray[0].setChecked(true);
                imgBtcCheck.setVisibility(View.VISIBLE);
                break;
            case "mBTC":
                radioOnearray[1].setChecked(true);
                imgMbtcCheck.setVisibility(View.VISIBLE);
                break;
            case "bits":
                radioOnearray[2].setChecked(true);
                imgBitsCheck.setVisibility(View.VISIBLE);
                break;
            case "sat":
                radioOnearray[3].setChecked(true);
                imgSatCheck.setVisibility(View.VISIBLE);
                break;
            default:
        }
        radioOne.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_btcOne:
                        try {
                            Daemon.commands.callAttr("set_base_uint", "BTC");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        EventBus.getDefault().post(new FirstEvent("11"));
                        EventBus.getDefault().post(new FirstEvent("22"));
                        imgBtcCheck.setVisibility(View.VISIBLE);
                        imgMbtcCheck.setVisibility(View.GONE);
                        imgBitsCheck.setVisibility(View.GONE);
                        imgSatCheck.setVisibility(View.GONE);
                        edit.putString("base_unit", "BTC");
                        edit.apply();
                        break;
                    case R.id.btn_btcTwo:
                        try {
                            Daemon.commands.callAttr("set_base_uint", "mBTC");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        EventBus.getDefault().post(new FirstEvent("11"));
                        EventBus.getDefault().post(new FirstEvent("22"));
                        imgBtcCheck.setVisibility(View.GONE);
                        imgMbtcCheck.setVisibility(View.VISIBLE);
                        imgBitsCheck.setVisibility(View.GONE);
                        imgSatCheck.setVisibility(View.GONE);
                        edit.putString("base_unit", "mBTC");
                        edit.apply();
                        break;
                    case R.id.btn_btcThree:
                        try {
                            Daemon.commands.callAttr("set_base_uint", "bits");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        EventBus.getDefault().post(new FirstEvent("11"));
                        EventBus.getDefault().post(new FirstEvent("22"));
                        imgBtcCheck.setVisibility(View.GONE);
                        imgMbtcCheck.setVisibility(View.GONE);
                        imgBitsCheck.setVisibility(View.VISIBLE);
                        imgSatCheck.setVisibility(View.GONE);
                        edit.putString("base_unit", "bits");
                        edit.apply();
                        break;
                    case R.id.btn_btcFour:
                        try {
                            Daemon.commands.callAttr("set_base_uint", "sat");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        EventBus.getDefault().post(new FirstEvent("11"));
                        EventBus.getDefault().post(new FirstEvent("22"));
                        imgBtcCheck.setVisibility(View.GONE);
                        imgMbtcCheck.setVisibility(View.GONE);
                        imgBitsCheck.setVisibility(View.GONE);
                        imgSatCheck.setVisibility(View.VISIBLE);
                        edit.putString("base_unit", "sat");
                        edit.apply();
                        break;
                    default:
                }
            }
        });
    }

    private void radioSelectTwo() {
        Resources resources = getResources();
        String[] currencyArray = resources.getStringArray(R.array.currency);
        String[] currencySymbolArray = resources.getStringArray(R.array.currency_symbol);
        for (int i = 0; i < currencyArray.length; i++) {
            CNYBean cnyBean = new CNYBean(currencyArray[i], false);
            cnyBean.setSymbol(currencySymbolArray[i]);
            listCNY.add(cnyBean);
        }
        reclCnyTable.setVisibility(View.VISIBLE);
        CNYAdapter cnyAdapter = new CNYAdapter(CurrencyActivity.this, listCNY, cny_unit);
        reclCnyTable.setAdapter(cnyAdapter);
        cnyAdapter.setOnLisennorClick(new CNYAdapter.onLisennorClick() {
            @Override
            public void itemClick(int pos) {
                try {
                    Daemon.commands.callAttr("set_currency", listCNY.get(pos).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                edit.putString(CURRENT_CURRENCY_SYMBOL, listCNY.get(pos).getName());
                edit.putString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, listCNY.get(pos).getSymbol());
                edit.putInt("cny_unit", pos);
                edit.apply();
                EventBus.getDefault().post(new FirstEvent("11"));
                EventBus.getDefault().post(new FirstEvent("22"));
                EventBus.getDefault().post(new CardUnitEvent());
            }
        });
    }


    @SingleClick
    @OnClick({R.id.img_back, R.id.tet_CheckAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_CheckAll:
                String strOpen = tetCheckAll.getText().toString();
                if (strOpen.equals(getString(R.string.more))) {
                    LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) reclCnyTable.getLayoutParams();
                    linearParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    reclCnyTable.setLayoutParams(linearParams1);
                    tetCheckAll.setText(getString(R.string.retract));
                } else {
                    LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) reclCnyTable.getLayoutParams();
                    linearParams1.height = 530;
                    reclCnyTable.setLayoutParams(linearParams1);
                    tetCheckAll.setText(getString(R.string.more));
                }
                break;
            default:
        }
    }

}

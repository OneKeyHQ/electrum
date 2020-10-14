package org.haobtc.onekey.activities.settings;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CurrencyActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.radio_one)
    RadioGroup radioOne;
    @BindView(R.id.recl_cnyTable)
    RecyclerView reclCnyTable;
    @BindView(R.id.tet_CheckAll)
    TextView tetCheckAll;

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
                break;
            case "mBTC":
                radioOnearray[1].setChecked(true);
                break;
            case "bits":
                radioOnearray[2].setChecked(true);
                break;
            case "sat":
                radioOnearray[3].setChecked(true);
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
                        edit.putString("base_unit", "sat");
                        edit.apply();
                        break;
                    default:
                }
            }
        });
    }

    private void radioSelectTwo() {
        PyObject get_currencies;
        List<PyObject> pyObjects;
        try {
            get_currencies = Daemon.commands.callAttr("get_currencies");
            Log.i("get_currenciesget_currencies", "radioSelectTwo:-- " + get_currencies);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("set_currency", "set_base_unit:==== " + e.getMessage());
            return;
        }
        listCNY.add(new CNYBean(getString(R.string.money_cny), false));
        listCNY.add(new CNYBean(getString(R.string.doller), false));
        listCNY.add(new CNYBean(getString(R.string.korean_money), false));
        if (get_currencies != null) {
            pyObjects = get_currencies.asList();
            for (int i = 0; i < pyObjects.size(); i++) {
                if (!"CNY".equals(pyObjects.get(i).toString()) && !"USD".equals(pyObjects.get(i).toString()) && !"KMR".equals(pyObjects.get(i).toString())) {
                    listCNY.add(new CNYBean(String.valueOf(pyObjects.get(i)), false));
                }
            }
            reclCnyTable.setVisibility(View.VISIBLE);
            CNYAdapter cnyAdapter = new CNYAdapter(CurrencyActivity.this, listCNY, cny_unit);
            reclCnyTable.setAdapter(cnyAdapter);
            cnyAdapter.setOnLisennorClick(new CNYAdapter.onLisennorClick() {
                @Override
                public void itemClick(int pos) {
                    if (pos == 0) {
                        try {
                            Daemon.commands.callAttr("set_currency", "CNY");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        edit.putString("cny_strunit", "CNY");
                    } else if (pos == 1) {
                        try {
                            Daemon.commands.callAttr("set_currency", "USD");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        edit.putString("cny_strunit", "USD");
                    } else if (pos == 2) {
                        try {
                            Daemon.commands.callAttr("set_currency", "KRW");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        edit.putString("cny_strunit", "KRW");
                    } else {
                        try {
                            Daemon.commands.callAttr("set_currency", listCNY.get(pos).getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        edit.putString("cny_strunit", listCNY.get(pos).getName());
                    }
                    edit.putInt("cny_unit", pos);
                    edit.apply();
                    EventBus.getDefault().post(new FirstEvent("11"));
                    EventBus.getDefault().post(new FirstEvent("22"));
                    EventBus.getDefault().post(new CardUnitEvent());
                }
            });
        }
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
                if (strOpen.equals(getString(R.string.check_all))) {
                    RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) reclCnyTable.getLayoutParams();
                    linearParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    reclCnyTable.setLayoutParams(linearParams1);
                    tetCheckAll.setText(getString(R.string.retract));
                } else {
                    RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) reclCnyTable.getLayoutParams();
                    linearParams1.height = 530;
                    reclCnyTable.setLayoutParams(linearParams1);
                    tetCheckAll.setText(getString(R.string.check_all));
                }
                break;
            default:
        }
    }

}

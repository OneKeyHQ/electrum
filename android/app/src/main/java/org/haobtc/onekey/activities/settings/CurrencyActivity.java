package org.haobtc.onekey.activities.settings;

import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.CNYAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CNYBean;
import org.haobtc.onekey.bean.FiatUnitSymbolBean;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.event.CardUnitEvent;
import org.haobtc.onekey.event.FirstEvent;

import java.util.ArrayList;

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
    @BindView(R.id.img_btc_check)
    ImageView imgBtcCheck;
    @BindView(R.id.img_mbtc_check)
    ImageView imgMbtcCheck;
    @BindView(R.id.img_bits_check)
    ImageView imgBitsCheck;
    @BindView(R.id.img_sat_check)
    ImageView imgSatCheck;
    private ArrayList<CNYBean> listCNY;
    private SystemConfigManager mSystemConfigManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency;
    }

    @Override
    public void initView() {
        mSystemConfigManager = new SystemConfigManager(this);
        ButterKnife.bind(this);
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
        String baseUnit = mSystemConfigManager.getCurrentBaseUnit();
        handleUnitCheck(baseUnit, true);
        radioOne.setOnCheckedChangeListener((group, checkedId) -> {
            String newBaseUnit;
            switch (checkedId) {
                default:
                case R.id.btn_btcOne:
                    newBaseUnit = "BTC";
                    break;
                case R.id.btn_btcTwo:
                    newBaseUnit = "mBTC";
                    break;
                case R.id.btn_btcThree:
                    newBaseUnit = "bits";
                    break;
                case R.id.btn_btcFour:
                    newBaseUnit = "sat";
                    break;
            }
            boolean success = mSystemConfigManager.setCurrentBaseUnit(newBaseUnit);
            if (!success) {
                return;
            }
            EventBus.getDefault().post(new FirstEvent("11"));
            EventBus.getDefault().post(new FirstEvent("22"));
            handleUnitCheck(newBaseUnit, false);
        });
    }

    private void handleUnitCheck(String baseUnit, boolean handlerCheck) {
        imgBtcCheck.setVisibility(View.GONE);
        imgMbtcCheck.setVisibility(View.GONE);
        imgBitsCheck.setVisibility(View.GONE);
        imgSatCheck.setVisibility(View.GONE);
        switch (baseUnit) {
            default:
            case "BTC":
                if (handlerCheck) {
                    radioOne.check(R.id.btn_btcOne);
                }
                imgBtcCheck.setVisibility(View.VISIBLE);
                break;
            case "mBTC":
                if (handlerCheck) {
                    radioOne.check(R.id.btn_btcTwo);
                }
                imgMbtcCheck.setVisibility(View.VISIBLE);
                break;
            case "bits":
                if (handlerCheck) {
                    radioOne.check(R.id.btn_btcThree);
                }
                imgBitsCheck.setVisibility(View.VISIBLE);
                break;
            case "sat":
                if (handlerCheck) {
                    radioOne.check(R.id.btn_btcFour);
                }
                imgSatCheck.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void radioSelectTwo() {
        FiatUnitSymbolBean currentFiatUnitSymbol = mSystemConfigManager.getCurrentFiatUnitSymbol();

        Resources resources = getResources();
        String[] currencyArray = resources.getStringArray(R.array.currency);
        String[] currencySymbolArray = resources.getStringArray(R.array.currency_symbol);
        int selectPosition = 0;
        for (int i = 0; i < currencyArray.length; i++) {
            CNYBean cnyBean = new CNYBean(currencyArray[i], false);
            cnyBean.setSymbol(currencySymbolArray[i]);
            listCNY.add(cnyBean);
            if (cnyBean.getName().equals(currentFiatUnitSymbol.getUnit())) {
                selectPosition = i;
            }
        }

        reclCnyTable.setVisibility(View.VISIBLE);
        CNYAdapter cnyAdapter = new CNYAdapter(CurrencyActivity.this, listCNY, selectPosition);
        reclCnyTable.setAdapter(cnyAdapter);
        cnyAdapter.setOnLisennorClick(position -> {
            CNYBean item = listCNY.get(position);
            boolean success = mSystemConfigManager.setCurrentFiatUnitSymbol(new FiatUnitSymbolBean(
                    item.getName(),
                    item.getSymbol()
            ));
            if (success) {
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

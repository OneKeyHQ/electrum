package org.haobtc.onekey.onekeys;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.homepage.DiscoverFragment;
import org.haobtc.onekey.onekeys.homepage.MindFragment;
import org.haobtc.onekey.onekeys.homepage.WalletFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeOnekeyActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.linear_mains)
    FrameLayout linearMains;
    @BindView(R.id.sj_radiogroup)
    RadioGroup sjRadiogroup;
    private long firstTime = 0;
    private WalletFragment walletFragment;
    private DiscoverFragment discoverFragment;
    private MindFragment mindFragment;
    private RadioButton radioButton[];
    private ArrayList<Fragment> fragments;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home_onekey;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
        fragments = new ArrayList<>();
        walletFragment = new WalletFragment();
        discoverFragment = new DiscoverFragment();
        mindFragment = new MindFragment();

        //默认让主页被选中
        switchFragment(walletFragment);

        //radiobutton长度
        radioButton = new RadioButton[sjRadiogroup.getChildCount()];
        for (int i = 0; i < radioButton.length; i++) {
            radioButton[i] = (RadioButton) sjRadiogroup.getChildAt(i);
        }
        radioButton[0].setChecked(true);
        sjRadiogroup.setOnCheckedChangeListener(this);
    }

    //switch fragment
    public void switchFragment(Fragment fragment) {
        if (!fragments.contains(fragment)) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.linear_mains, fragment);
            transaction.commit();
            fragments.add(fragment);
        } else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            hideAllFragment(manager);
            transaction.show(fragment);
            transaction.commit();
        }

    }

    private void hideAllFragment(FragmentManager manager) {
        FragmentTransaction transaction = manager.beginTransaction();
        for (Fragment fragment : fragments) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_one:
                switchFragment(walletFragment);
                break;
            case R.id.radio_two:
                switchFragment(discoverFragment);
                break;
            case R.id.radio_three:
                switchFragment(mindFragment);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(HomeOnekeyActivity.this, R.string.dowbke_to_exit, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
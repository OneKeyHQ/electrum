package org.haobtc.onekey.onekeys;

import android.view.KeyEvent;
import android.widget.Toast;
import butterknife.BindView;
import java.util.ArrayList;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.FragmentMainAdapter;
import org.haobtc.onekey.bean.TabEntity;
import org.haobtc.onekey.business.update.AutoCheckUpdate;
import org.haobtc.onekey.manager.HardwareCallbackHandler;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.widget.NoScrollViewPager;
import org.haobtc.onekey.ui.widget.tablayout.CommonTabLayout;
import org.haobtc.onekey.ui.widget.tablayout.CustomTabEntity;
import org.haobtc.onekey.ui.widget.tablayout.OnTabSelectListener;

/** @author liyan */
public class HomeOneKeyActivity extends BaseActivity {

    public static final String EXT_RESTART = "ext_restart";

    @BindView(R.id.scrollView)
    NoScrollViewPager scrollViewPager;

    @BindView(R.id.common_tab)
    CommonTabLayout tabLayout;

    private long firstTime = 0;

    private String[] mTitles;
    private int[] mIconUnSelectIds = {
        R.drawable.wallet_normal, R.drawable.wallet_tab_find_un, R.mipmap.mindno
    };
    private int[] mIconSelectIds = {
        R.drawable.wallet_highlight, R.drawable.wallet_tab_find, R.mipmap.mindyes
    };
    private FragmentMainAdapter fragmentMainAdapter;
    private ArrayList<CustomTabEntity> mTabEntities;
    private AutoCheckUpdate mAutoCheckUpdate;

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_home_onekey;
    }

    @Override
    public boolean needEvents() {
        return false;
    }

    /** init */
    @Override
    public void init() {
        HardwareCallbackHandler callbackHandler = HardwareCallbackHandler.getInstance(this);
        PyEnv.setHandle(callbackHandler);
        initPage();
        mAutoCheckUpdate = AutoCheckUpdate.getInstance(this);
        getUpdateInfo();
    }

    private void initPage() {
        mTitles =
                new String[] {
                    getString(R.string.wallet),
                    getString(R.string.tab_found),
                    getString(R.string.mind)
                };
        mTabEntities = new ArrayList<>();
        fragmentMainAdapter = new FragmentMainAdapter(getSupportFragmentManager(), mTitles);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        scrollViewPager.setAdapter(fragmentMainAdapter);
        scrollViewPager.setOffscreenPageLimit(mTitles.length);
        tabLayout.setTabData(mTabEntities);
        tabLayout.setOnTabSelectListener(
                new OnTabSelectListener() {
                    @Override
                    public void onTabSelect(int position) {
                        scrollViewPager.setCurrentItem(position);
                    }

                    @Override
                    public void onTabReselect(int position) {}
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(HomeOneKeyActivity.this, R.string.dowbke_to_exit, Toast.LENGTH_SHORT)
                        .show();
                firstTime = secondTime;
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getUpdateInfo() {
        if (getIntent().getBooleanExtra(EXT_RESTART, false)) {
            return;
        }
        mAutoCheckUpdate.checkUpdate(getSupportFragmentManager(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAutoCheckUpdate.onDestroy();
    }
}

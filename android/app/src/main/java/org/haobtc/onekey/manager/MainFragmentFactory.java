package org.haobtc.onekey.manager;
import androidx.fragment.app.Fragment;

import org.haobtc.onekey.onekeys.homepage.MindFragment;
import org.haobtc.onekey.onekeys.homepage.WalletFragment;
import org.haobtc.onekey.ui.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public class MainFragmentFactory {
    private static Map<Integer, BaseFragment> mMap = new HashMap<>();

    public static Fragment createFragment (int position) {
        BaseFragment fragment = mMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new WalletFragment();
                    break;
                case 1:
                    fragment = new MindFragment();
                    break;
            }
            mMap.put(position, fragment);
        }
        return fragment;
    }

}

package org.haobtc.onekey.onekeys.homepage;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.AboutActivity;
import org.haobtc.onekey.activities.LanguageSettingActivity;
import org.haobtc.onekey.activities.ServerSettingActivity;
import org.haobtc.onekey.activities.TransactionsSettingActivity;
import org.haobtc.onekey.activities.settings.CurrencyActivity;
import org.haobtc.onekey.activities.settings.OneKeyManageActivity;
import org.haobtc.onekey.activities.settings.SelectorActivity;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.onekeys.homepage.mindmenu.AllAssetsActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.FixHdPassActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HDWalletActivity;
import org.haobtc.onekey.ui.activity.PinVerifyWaySelector;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author jinxiaomin
 */
public class MindFragment extends BaseFragment {


    @BindView(R.id.all_assets)
    RelativeLayout allAssets;
    @BindView(R.id.rel_hd_wallet)
    RelativeLayout relHdWallet;
    @BindView(R.id.rel_all_device)
    RelativeLayout relAllDevice;
    @BindView(R.id.rel_link_method)
    RelativeLayout relLinkMethod;
    @BindView(R.id.rel_pass)
    RelativeLayout relPass;
    @BindView(R.id.face_id)
    RelativeLayout faceId;
    @BindView(R.id.fingerprint)
    RelativeLayout fingerprint;
    @BindView(R.id.rel_language)
    RelativeLayout relLanguage;
    @BindView(R.id.rel_currency)
    RelativeLayout relCurrency;
    @BindView(R.id.rel_internet)
    RelativeLayout relInternet;
    @BindView(R.id.rel_tx_set)
    RelativeLayout relTxSet;
    @BindView(R.id.rel_about)
    RelativeLayout relAbout;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_mind;
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {

    }

    @OnClick({R.id.all_assets, R.id.rel_hd_wallet, R.id.rel_all_device, R.id.rel_link_method, R.id.rel_pass, R.id.face_id, R.id.fingerprint, R.id.rel_language, R.id.rel_currency, R.id.rel_internet, R.id.rel_tx_set, R.id.rel_about, R.id.pin_verify_way})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_assets:
                Intent intent6 = new Intent(getActivity(), AllAssetsActivity.class);
                startActivity(intent6);
                break;
            case R.id.rel_hd_wallet:
                Intent intent3 = new Intent(getActivity(), HDWalletActivity.class);
                startActivity(intent3);
                break;
            case R.id.rel_all_device:
                Intent intent7 = new Intent(getActivity(), OneKeyManageActivity.class);
                startActivity(intent7);
                break;
            case R.id.rel_link_method:
                Intent intent4 = new Intent(getActivity(), SelectorActivity.class);
                startActivity(intent4);
                break;
            case R.id.rel_pass:
                boolean isHaveWallet = PreferencesManager.getAll(getContext(), Constant.WALLETS).isEmpty();
                if (!isHaveWallet) {
                    Intent intent8 = new Intent(getActivity(), FixHdPassActivity.class);
                    startActivity(intent8);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.please_create_wallet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.face_id:
                break;
            case R.id.fingerprint:
                break;
            case R.id.rel_language:
                Intent intent1 = new Intent(getActivity(), LanguageSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.rel_currency:
                Intent intent2 = new Intent(getActivity(), CurrencyActivity.class);
                startActivity(intent2);
                break;
            case R.id.rel_internet:
                Intent intent9 = new Intent(getActivity(), ServerSettingActivity.class);
                startActivity(intent9);
                break;
            case R.id.rel_tx_set:
                startActivity(new Intent(getActivity(), TransactionsSettingActivity.class));
                break;
            case R.id.rel_about:
                Intent intent5 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent5);
                break;
            case R.id.pin_verify_way:
//                showToast(R.string.support_less_promote);
               startActivity(new Intent(getActivity(), PinVerifyWaySelector.class));
                break;
        }
    }
}
package org.haobtc.onekey.onekeys.homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.AboutActivity;
import org.haobtc.onekey.activities.LanguageSettingActivity;
import org.haobtc.onekey.activities.ServerSettingActivity;
import org.haobtc.onekey.activities.TransactionsSettingActivity;
import org.haobtc.onekey.activities.settings.BixinKEYManageActivity;
import org.haobtc.onekey.activities.settings.CurrencyActivity;
import org.haobtc.onekey.activities.settings.SelectorActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.AllAssetsActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.FixHdPassActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HDWalletActivity;
import org.haobtc.onekey.onekeys.homepage.process.ChooseCurrencyActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;


public class MindFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mind, container, false);
        preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        RelativeLayout txSet = view.findViewById(R.id.rel_tx_set);
        txSet.setOnClickListener(this);
        RelativeLayout txLanguage = view.findViewById(R.id.rel_language);
        txLanguage.setOnClickListener(this);
        RelativeLayout txCurrency = view.findViewById(R.id.rel_currency);
        txCurrency.setOnClickListener(this);
        RelativeLayout hdWallet = view.findViewById(R.id.rel_hd_wallet);
        hdWallet.setOnClickListener(this);
        RelativeLayout relLinkMethod = view.findViewById(R.id.rel_link_method);
        relLinkMethod.setOnClickListener(this);
        RelativeLayout relAbout = view.findViewById(R.id.rel_about);
        relAbout.setOnClickListener(this);
        RelativeLayout relAllAssets = view.findViewById(R.id.all_assets);
        relAllAssets.setOnClickListener(this);
        RelativeLayout relAllDevice = view.findViewById(R.id.rel_all_device);
        relAllDevice.setOnClickListener(this);
        RelativeLayout relPass = view.findViewById(R.id.rel_pass);
        relPass.setOnClickListener(this);
        RelativeLayout relInternet = view.findViewById(R.id.rel_internet);
        relInternet.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_tx_set:
                startActivity(new Intent(getActivity(), TransactionsSettingActivity.class));
                break;
            case R.id.rel_language:
                Intent intent1 = new Intent(getActivity(), LanguageSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.rel_currency:
                Intent intent2 = new Intent(getActivity(), CurrencyActivity.class);
                startActivity(intent2);
                break;
            case R.id.rel_hd_wallet:
                Intent intent3 = new Intent(getActivity(), HDWalletActivity.class);
                startActivity(intent3);
                break;
            case R.id.rel_link_method:
                Intent intent4 = new Intent(getActivity(), SelectorActivity.class);
                startActivity(intent4);
                break;
            case R.id.rel_about:
                Intent intent5 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent5);
                break;
            case R.id.all_assets:
                Intent intent6 = new Intent(getActivity(), AllAssetsActivity.class);
                startActivity(intent6);
                break;
            case R.id.rel_all_device:
                Intent intent7 = new Intent(getActivity(), BixinKEYManageActivity.class);
                startActivity(intent7);
                break;
            case R.id.rel_pass:
                boolean isHaveWallet = preferences.getBoolean("isHaveWallet", false);
                if (isHaveWallet){
                    Intent intent8 = new Intent(getActivity(), FixHdPassActivity.class);
                    startActivity(intent8);
                }else{
                    Toast.makeText(getActivity(), getString(R.string.please_create_wallet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rel_internet:
                Intent intent9 = new Intent(getActivity(), ServerSettingActivity.class);
                startActivity(intent9);
                break;

        }
    }
}
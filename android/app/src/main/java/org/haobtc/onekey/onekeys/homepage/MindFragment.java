package org.haobtc.onekey.onekeys.homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.LanguageSettingActivity;
import org.haobtc.onekey.activities.TransactionsSettingActivity;
import org.haobtc.onekey.activities.settings.CurrencyActivity;
import org.haobtc.onekey.onekeys.homepage.process.ChooseCurrencyActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;


public class MindFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mind, container, false);
        RelativeLayout txSet = view.findViewById(R.id.rel_tx_set);
        txSet.setOnClickListener(this);
        RelativeLayout txLanguage = view.findViewById(R.id.rel_language);
        txLanguage.setOnClickListener(this);
        RelativeLayout txCurrency = view.findViewById(R.id.rel_currency);
        txCurrency.setOnClickListener(this);

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

        }
    }
}
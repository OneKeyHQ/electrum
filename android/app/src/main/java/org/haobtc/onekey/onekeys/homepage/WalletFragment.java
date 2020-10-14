package org.haobtc.onekey.onekeys.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.HomeHdAdapter;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.ReceiveHDActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;

public class WalletFragment extends Fragment implements View.OnClickListener {
    TextView textWalletName;
    private RecyclerView recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        textWalletName = view.findViewById(R.id.text_wallet_name);
        TextView tetAmount = view.findViewById(R.id.text_amount);
        recyclerview = view.findViewById(R.id.recl_hd_list);
        RelativeLayout relRecovery = view.findViewById(R.id.rel_recovery_hd);
        RelativeLayout relCreateHd = view.findViewById(R.id.rel_create_hd);
        RelativeLayout relCheckWallet = view.findViewById(R.id.rel_check_wallet);
        ImageView imgScan = view.findViewById(R.id.img_scan);
        RelativeLayout relPairHard = view.findViewById(R.id.rel_pair_hard);
        LinearLayout linearSend = view.findViewById(R.id.linear_send);
        LinearLayout linearReceive = view.findViewById(R.id.linear_receive);

        relCreateHd.setOnClickListener(this);
        relRecovery.setOnClickListener(this);
        relCheckWallet.setOnClickListener(this);
        imgScan.setOnClickListener(this);
        relPairHard.setOnClickListener(this);
        linearSend.setOnClickListener(this);
        linearReceive.setOnClickListener(this);
        initdata();
        return view;
    }

    private void initdata() {
        //get wallet balance
        getWalletBalance();
        ArrayList<String> amountList = new ArrayList<>();
        amountList.add("13.5");
        amountList.add("19.1");
        HomeHdAdapter homeHdAdapter = new HomeHdAdapter(amountList);
        recyclerview.setAdapter(homeHdAdapter);

    }

    private void getWalletBalance() {
        try {
            PyObject loadAllWallet = Daemon.commands.callAttr("load_all_wallet");
            PyObject selectWallet = Daemon.commands.callAttr("select_wallet","BTC-1");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_check_wallet:
                Intent intent1 = new Intent(getActivity(), WalletListActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_scan:
                break;
            case R.id.rel_create_hd:
                createHdwallet();
                break;
            case R.id.rel_recovery_hd:
                Intent intent = new Intent(getActivity(), RecoverHdWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_pair_hard:
                break;
            case R.id.linear_send:
                Intent intent2 = new Intent(getActivity(), SendHdActivity.class);
                startActivity(intent2);
                break;
            case R.id.linear_receive:
                Intent intent3 = new Intent(getActivity(), ReceiveHDActivity.class);
                startActivity(intent3);
                break;

        }
    }

    private void createHdwallet() {
        Intent intent = new Intent(getActivity(), SetHDWalletPassActivity.class);
        startActivity(intent);

    }

}
package org.haobtc.onekey.fragment.mainwheel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.chaquo.python.Kwarg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.personalwallet.hidewallet.CheckHideWalletActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.CheckHideWalletEvent;
import org.haobtc.onekey.utils.Daemon;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.xpub;

public class CheckHideWalletFragment extends WheelViewpagerFragment {
    public static final String TAG = CheckHideWalletFragment.class.getSimpleName();

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_hide_wallet, container, false);
        EventBus.getDefault().register(this);
        initview(view);
        return view;
    }

    private void initview(View view) {
        CardView cardCheckHidewallet = view.findViewById(R.id.wallet_card_add);
        cardCheckHidewallet.setOnClickListener(new View.OnClickListener() {
            @SingleClick
            @Override
            public void onClick(View v) {
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(null);
                CommunicationModeSelector.runnables.add(null);
                Intent intent = new Intent(getActivity(), CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setValue(String msgVote) {
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(CheckHideWalletEvent updataHint) {
        String xpub = updataHint.getXpub();
        String deviceId = updataHint.getDeviceId();
        String strXpub = "[[\"" + xpub + "\",\"" + deviceId + "\"]]";
        try {
            Daemon.commands.callAttr("import_create_hw_wallet", "隐藏钱包", 1, 1, strXpub, new Kwarg("hide_type", true));
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            if ("BaseException: file already exists at path".equals(message)) {
                Toast.makeText(getActivity(), getString(R.string.changewalletname), Toast.LENGTH_SHORT).show();
            } else {
                assert message != null;
                if (message.contains("The same xpubs have create wallet")) {
                    String haveWalletName = message.substring(message.indexOf("name=") + 5);
                    Toast.makeText(getActivity(), getString(R.string.xpub_have_wallet) + haveWalletName, Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
        Intent intent = new Intent(getActivity(), CheckHideWalletActivity.class);
        startActivity(intent);
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

package org.haobtc.keymanager.activities.personalwallet.mnemonic_word;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.adapter.MnemonicAddrAdapter;
import org.haobtc.keymanager.event.FromSeedEvent;
import org.haobtc.keymanager.event.MnemonicAddrEvent;
import org.haobtc.keymanager.utils.MyDialog;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportMnemonicWalletActivity extends BaseActivity {

    @BindView(R.id.recl_mnemonic)
    RecyclerView reclMnemonic;
    private String strNewSeed;
    private MyDialog myDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_mnemonic_wallet;

    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        myDialog = MyDialog.showDialog(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        strNewSeed = intent.getStringExtra("strNewseed");
    }

    @Override
    public void initData() {
        myDialog.show();
    }

    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void event(FromSeedEvent updataHint) {
        EventBus.getDefault().removeStickyEvent(updataHint);
        Log.i("updataHint", "event: " + updataHint.getFromSeed());
        String fromSeed = updataHint.getFromSeed();
        if (!TextUtils.isEmpty(fromSeed)) {
            //get from seed data
            getFromSeedData(fromSeed);
        } else {
            myDialog.dismiss();
        }
    }

    private void getFromSeedData(String fromSeeds) {
        myDialog.dismiss();
        ArrayList<MnemonicAddrEvent> addressevents = new ArrayList<>();
        Map maps = JSON.parseObject(fromSeeds);
        for (Object obj : maps.keySet()) {
            Map mapTwo = JSON.parseObject(maps.get(obj).toString());
            MnemonicAddrEvent mnemonicAddrEvent = new MnemonicAddrEvent();
            mnemonicAddrEvent.setType(obj.toString());
            mnemonicAddrEvent.setAddress((mapTwo.get("addr")).toString());
            String derivation = mapTwo.get("derivation").toString();
            if (!TextUtils.isEmpty(derivation)) {
                mnemonicAddrEvent.setDerivation(derivation);
            } else {
                mnemonicAddrEvent.setDerivation("");
            }
            addressevents.add(mnemonicAddrEvent);
        }
        MnemonicAddrAdapter mnemonicAddrAdapter = new MnemonicAddrAdapter(addressevents);
        reclMnemonic.setAdapter(mnemonicAddrAdapter);
        mnemonicAddrAdapter.setOnItemClickListener((adapter, view, position) -> {
            String derivation = addressevents.get(position).getDerivation();
            Intent intent = new Intent(ImportMnemonicWalletActivity.this, CreateHelpWordWalletActivity.class);
            intent.putExtra("newSeed", strNewSeed);
            intent.putExtra("mnemonic_wallet_derivation", derivation);
            startActivity(intent);

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

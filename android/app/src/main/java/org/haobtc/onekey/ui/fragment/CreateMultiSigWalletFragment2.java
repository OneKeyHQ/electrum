package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.AddedXpubAdapter;
import org.haobtc.onekey.bean.XpubItem;
import org.haobtc.onekey.event.AddXpubEvent;
import org.haobtc.onekey.event.CreateMultiSigWalletEvent;
import org.haobtc.onekey.event.CreateWalletEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.dialog.ChooseAddXpubWayDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/24/20
 */

public class CreateMultiSigWalletFragment2 extends BaseFragment {

    @BindView(R.id.state_add)
    TextView stateAdd;
    @BindView(R.id.xpub_rec)
    RecyclerView xpubRec;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.add_xpub)
    RelativeLayout addXpub;
    @BindView(R.id.bn_complete_add_cosigner)
    Button bnCompleteAddCosigner;
    private int cosignerNum;
    private List<XpubItem> addedXpubList;

    public CreateMultiSigWalletFragment2(int cosignerNum) {
        this.cosignerNum = cosignerNum;
    }
    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
       stateAdd.setText(String.format(Locale.ENGLISH, "0/%d", cosignerNum));
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.create_multi_sig_fragment_2;
    }

    @OnClick({R.id.bn_complete_add_cosigner, R.id.add_xpub})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_xpub:
                new ChooseAddXpubWayDialog().show(getChildFragmentManager(), "");
                break;
            case R.id.bn_complete_add_cosigner:
                EventBus.getDefault().post(new CreateMultiSigWalletEvent(addedXpubList));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddXpubEvent(AddXpubEvent event) {
        if (addedXpubList == null) {
            addedXpubList = new ArrayList<>();
        }
        addXpub(event.getName(), event.getXpub());
    }

    private void addXpub(String name, String xpub) {
        boolean exist = false;
        if (addedXpubList.size() != 0) {
            for (XpubItem item : addedXpubList) {
                if (xpub.equals(item.getXpub())) {
                    showToast(getString(R.string.please_change_xpub));
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                addedXpubList.add(new XpubItem(name, xpub));
            }
        } else {
            addedXpubList.add(new XpubItem(name, xpub));
        }
        //bixinKEY
        AddedXpubAdapter addedXpubAdapter = new AddedXpubAdapter(addedXpubList);
        xpubRec.setAdapter(addedXpubAdapter);
        stateAdd.setText(String.format("%d/%d", addedXpubList.size(), cosignerNum));

        if (addedXpubList.size() == cosignerNum) {
            bnCompleteAddCosigner.setEnabled(true);
            bnCompleteAddCosigner.setBackground(getContext().getDrawable(R.drawable.btn_checked));
            addXpub.setVisibility(View.GONE);
        }
        addedXpubAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.img_deleteKey) {
                addedXpubList.remove(position);
                addedXpubAdapter.notifyDataSetChanged();
                addXpub.setVisibility(View.VISIBLE);
                stateAdd.setText(String.format("%d/%d", addedXpubList.size(), cosignerNum));
                bnCompleteAddCosigner.setEnabled(false);
                bnCompleteAddCosigner.setBackground(getContext().getDrawable(R.drawable.btn_no_check));
            } else {
                throw new IllegalStateException("Unexpected value: " + view.getId());
            }
        });
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}

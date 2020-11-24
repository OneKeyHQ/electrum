package org.haobtc.onekey.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MnemonicInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.InitDeviceEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.adapter.MnemonicsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ImportMnemonicToDeviceFragment extends BaseFragment implements MnemonicsAdapter.CallBack {


    @BindView(R.id.mnemonics_list)
    protected RecyclerView mMnemonicsListView;
    private List<MnemonicInfo> mMnemonicsList = new ArrayList<>();
    @BindView(R.id.btn_import)
    protected Button mImport;


    @Override
    public void init(View view) {

        mMnemonicsListView.setNestedScrollingEnabled(false);

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        manager.setOrientation(RecyclerView.VERTICAL);
        mMnemonicsListView.setLayoutManager(manager);

        for (int i = 1; i < Constant.MNEMONIC_SIZE + 1; i++) {
            mMnemonicsList.add(new MnemonicInfo(i, ""));
        }
        MnemonicsAdapter adapter = new MnemonicsAdapter(getContext(), mMnemonicsList, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.HORIZONTAL);
        dividerItemDecoration.setDrawable(getContext().getDrawable(R.drawable.divider));
        mMnemonicsListView.addItemDecoration(dividerItemDecoration);
        mMnemonicsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_import_mnemonic_to_device;
    }


    @Override
    public void onCheckState() {
        mImport.setEnabled(check());
    }


    private boolean check() {
        for (int i = 0; i < Constant.MNEMONIC_SIZE; i++) {
            if (TextUtils.isEmpty(mMnemonicsList.get(i).getMnemonic())) {
                return false;
            }
        }
        return true;
    }

    @OnClick(R.id.btn_import)
    public void onViewClicked() {
        StringBuilder builder = new StringBuilder();
        mMnemonicsList.forEach((s) -> builder.append(s.getMnemonic()));
        EventBus.getDefault().post(new InitDeviceEvent(false, builder.toString()));
    }
}

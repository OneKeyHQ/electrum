package org.haobtc.onekey.ui.fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.PublicAdapter;
import org.haobtc.onekey.adapter.SignNumAdapter;
import org.haobtc.onekey.bean.CNYBean;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 * @date 11/24/20
 */

public class CreateMultiSigWalletFragment extends BaseFragment {

    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.co_signer_num_rec)
    RecyclerView coSignerNumRec;
    @BindView(R.id.sign_num_rec)
    RecyclerView signNumRec;
    @BindView(R.id.next)
    Button next;
    private String coSignerNum = "3";
    private String sigNum = "2";
    private String name;

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        initCoSignerNumChooseView();
    }

    private void initCoSignerNumChooseView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        coSignerNumRec.setLayoutManager(layoutManager);
        ArrayList<CNYBean> pubList = new ArrayList<>();
        for (int i = 2; i < 13; i++) {
            CNYBean cnyBean = new CNYBean(i + "", false);
            pubList.add(cnyBean);
        }
        PublicAdapter publicAdapter = new PublicAdapter(getContext(), pubList, 1);
        coSignerNumRec.setAdapter(publicAdapter);
        publicAdapter.setOnLisennorClick(pos -> {
            coSignerNum = pubList.get(pos).getName();
            initSigNumChooseView(Integer.parseInt(coSignerNum));
        });
        initSigNumChooseView(3);
    }

    private void initSigNumChooseView(int num) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        signNumRec.setLayoutManager(layoutManager);
        ArrayList<CNYBean> signNumList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            CNYBean cnyBean = new CNYBean(i + "", false);
            signNumList.add(cnyBean);
        }
        SignNumAdapter signNumAdapter;
        if (Integer.parseInt(sigNum) > Integer.parseInt(coSignerNum)) {
            signNumAdapter = new SignNumAdapter(getContext(), signNumList, -1, num);
            sigNum = "";
        } else {
            signNumAdapter = new SignNumAdapter(getContext(), signNumList, Integer.parseInt(sigNum) - 1, num);
        }

        signNumRec.setAdapter(signNumAdapter);
        signNumAdapter.setOnLisennorClick(new SignNumAdapter.onLisennorClick() {
            @Override
            public void itemClick(int pos) {
                sigNum = signNumList.get(pos).getName();
            }
        });
    }
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.create_multi_sig_fragment_1;
    }


    @OnClick(R.id.next)
    public void onViewClicked(View view) {
        EventBus.getDefault().post(new NextFragmentEvent(R.layout.create_multi_sig_fragment_2, Integer.parseInt(coSignerNum), Integer.parseInt(sigNum),name));
    }
    @OnTextChanged(R.id.name_edit)
    public void onTextChanged(Editable editable) {

        name = editable.toString();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(sigNum)) {
            next.setEnabled(true);
            next.setBackground(getContext().getDrawable(R.drawable.btn_checked));

        } else {
            next.setEnabled(false);
            next.setBackground(getContext().getDrawable(R.drawable.btn_no_check));
        }
    }
}

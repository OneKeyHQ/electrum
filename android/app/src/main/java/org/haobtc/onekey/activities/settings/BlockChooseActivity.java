package org.haobtc.onekey.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.BlockBrowserChooseAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.business.blockBrowser.BlockBrowser;
import org.haobtc.onekey.business.blockBrowser.BlockBrowserManager;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.FirstEvent;

public class BlockChooseActivity extends BaseActivity {

    private static final String EXT_COIN_TYPE = "EXT_COIN_TYPE";

    public static void start(Context context, Vm.CoinType coinType) {
        Intent intent = new Intent(context, BlockChooseActivity.class);
        intent.putExtra(EXT_COIN_TYPE, coinType.callFlag);
        context.startActivity(intent);
    }

    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.recl_Quetation)
    RecyclerView reclQuetation;

    private Vm.CoinType mCoinType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_block_choose;
    }

    @Override
    public void initView() {
        mCoinType = Vm.CoinType.convertByCallFlag(getIntent().getStringExtra(EXT_COIN_TYPE));
        ButterKnife.bind(this);

        tvTitle.setText(String.format(getString(R.string.block_choose), mCoinType.coinName));
    }

    @Override
    public void initData() {
        Map<String, BlockBrowser> blockBrowserList =
                BlockBrowserManager.INSTANCE.getBlockBrowserList(mCoinType);
        BlockBrowser currentBlockBrowser =
                BlockBrowserManager.INSTANCE.getCurrentBlockBrowser(mCoinType);

        BlockBrowserChooseAdapter quetationChooseAdapter =
                new BlockBrowserChooseAdapter(
                        BlockChooseActivity.this,
                        new ArrayList<>(blockBrowserList.values()),
                        currentBlockBrowser);
        quetationChooseAdapter.addHeaderView(generateHeadView());
        reclQuetation.setAdapter(quetationChooseAdapter);
        quetationChooseAdapter.setOnLisennorClick(
                (pos, blockBrowser) -> {
                    BlockBrowserManager.INSTANCE.setBlockBrowser(
                            mCoinType, blockBrowser.uniqueTag());
                    switch (mCoinType) {
                        case ETH:
                            EventBus.getDefault()
                                    .post(new FirstEvent(FirstEvent.MSG_SET_ETH_BLOCK));
                            break;
                        case BTC:
                            EventBus.getDefault()
                                    .post(new FirstEvent(FirstEvent.MSG_SET_BTC_BLOCK));
                            break;
                    }
                });
    }

    private View generateHeadView() {
        View inflate = View.inflate(this, R.layout.view_block_browser_hint, null);
        TextView text = inflate.findViewById(R.id.tv_content);
        text.setText(String.format(getString(R.string.block_choose_tip), mCoinType.coinName));
        return inflate;
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}

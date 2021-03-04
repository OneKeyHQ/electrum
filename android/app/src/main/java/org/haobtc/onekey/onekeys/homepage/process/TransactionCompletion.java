package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Vm;

public class TransactionCompletion extends BaseActivity {
    private static final String EXT_TX_DETAIL = "ext_tx_detail";
    private static final String EXT_COIN_TYPE = "ext_oin_type";
    private static final String EXT_AMOUNT = "ext_amount";

    @BindView(R.id.text_amount)
    TextView textAmount;

    @BindView(R.id.btn_next)
    Button btnNext;

    private String txDetail;
    private Vm.CoinType mCoinType;

    public static void start(
            Context context, Vm.CoinType coinType, String signedTx, String amounts) {
        Intent intent = new Intent(context, TransactionCompletion.class);
        intent.putExtra(EXT_TX_DETAIL, signedTx);
        intent.putExtra(EXT_COIN_TYPE, coinType.callFlag);
        intent.putExtra(EXT_AMOUNT, amounts);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_completion;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        txDetail = getIntent().getStringExtra(EXT_TX_DETAIL);
        mCoinType = Vm.CoinType.convertByCallFlag(getIntent().getStringExtra(EXT_COIN_TYPE));
        String amount = getIntent().getStringExtra(EXT_AMOUNT);
        if (amount.contains("(")) {
            String txAmount = amount.substring(0, amount.indexOf("("));
            textAmount.setText(txAmount);
        } else {
            textAmount.setText(amount);
        }
    }

    @Override
    public void initData() {}

    @OnClick({R.id.img_back, R.id.text_check_detail, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_check_detail:
                switch (mCoinType) {
                    case BTC:
                        DetailTransactionActivity.startRawTx(TransactionCompletion.this, txDetail);
                        break;
                    case ETH:
                        DetailETHTransactionActivity.start(
                                TransactionCompletion.this, txDetail, "");
                        break;
                }
                finish();
                break;
            case R.id.btn_next:
                finish();
                break;
        }
    }
}

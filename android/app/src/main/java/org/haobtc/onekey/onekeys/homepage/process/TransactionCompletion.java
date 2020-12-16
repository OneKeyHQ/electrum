package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionCompletion extends BaseActivity {

    @BindView(R.id.text_amount)
    TextView textAmount;
    @BindView(R.id.btn_next)
    Button btnNext;
    private String txDetail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_completion;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        txDetail = getIntent().getStringExtra("txDetail");
        String amount = getIntent().getStringExtra("amounts");
        if (amount.contains("(")) {
            String txAmount = amount.substring(0, amount.indexOf("("));
            textAmount.setText(txAmount);
        } else {
            textAmount.setText(amount);
        }
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.img_back, R.id.text_check_detail, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_check_detail:
                Intent intent = new Intent(TransactionCompletion.this, DetailTransactionActivity.class);
                intent.putExtra("txDetail", txDetail);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_next:
                finish();
                break;
        }
    }
}
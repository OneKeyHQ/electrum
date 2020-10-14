package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendHdActivity extends BaseActivity {

    @BindView(R.id.edit_input_address)
    EditText editInputAddress;
    @BindView(R.id.edit_amount)
    EditText editAmount;
    @BindView(R.id.text_fee_50)
    TextView textFee50;
    @BindView(R.id.text_dollar_50)
    TextView textDollar50;
    @BindView(R.id.text_fee_20)
    TextView textFee20;
    @BindView(R.id.text_dollar_20)
    TextView textDollar20;
    @BindView(R.id.text_fee_10)
    TextView textFee10;
    @BindView(R.id.text_dollar_10)
    TextView textDollar10;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.checkbox_slow)
    CheckBox checkboxSlow;
    @BindView(R.id.view_slow)
    View viewSlow;
    @BindView(R.id.checkbox_recommend)
    CheckBox checkboxRecommend;
    @BindView(R.id.view_recommend)
    View viewRecommend;
    @BindView(R.id.checkbox_fast)
    CheckBox checkboxFast;
    @BindView(R.id.view_fast)
    View viewFast;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_hd;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.tet_choose_type, R.id.text_max, R.id.tet_custom_fee, R.id.lin_slow, R.id.lin_recommend, R.id.lin_fast, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_choose_type:
                Intent intent = new Intent(SendHdActivity.this, ChooseCurrencyActivity.class);
                startActivity(intent);
                break;
            case R.id.text_max:
                break;
            case R.id.tet_custom_fee:
                createWalletChooseDialog(SendHdActivity.this, R.layout.custom_fee);
                break;
            case R.id.lin_slow:
                viewSlow.setVisibility(View.VISIBLE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.VISIBLE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.GONE);
                break;
            case R.id.lin_recommend:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.VISIBLE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.VISIBLE);
                checkboxFast.setVisibility(View.GONE);
                break;
            case R.id.lin_fast:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.VISIBLE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_next:


                break;
        }
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });

        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

}
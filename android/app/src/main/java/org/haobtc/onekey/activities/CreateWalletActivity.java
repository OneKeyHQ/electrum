package org.haobtc.onekey.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;

import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.jointwallet.MultiSigWalletCreator;
import org.haobtc.onekey.activities.personalwallet.CreatAppWalletActivity;
import org.haobtc.onekey.activities.personalwallet.SingleSigWalletCreator;
import org.haobtc.onekey.activities.personalwallet.mnemonic_word.MnemonicWordActivity;
import org.haobtc.onekey.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateWalletActivity extends BaseActivity {
    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    private String intentWhere;

    @Override
    public int getLayoutId() {
        return R.layout.create_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        intentWhere = intent.getStringExtra("intentWhere");

    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_backCreat, R.id.lin_personal_walt, R.id.bn_import_wallet, R.id.bn_create_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                if (TextUtils.isEmpty(intentWhere)) {
                    finish();
                } else {
                    mIntent(MainActivity.class);
                }
                break;
            case R.id.lin_personal_walt:
                mIntent(SingleSigWalletCreator.class);
                break;
            case R.id.bn_import_wallet:
                mIntent(MultiSigWalletCreator.class);
                break;
            case R.id.bn_create_wallet:
                createWalletChooseDialog(CreateWalletActivity.this, R.layout.choose_wallet);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.create_app_wallet).setOnClickListener(v -> {
            mIntent(CreatAppWalletActivity.class);
            dialogBtoms.dismiss();
        });

        view.findViewById(R.id.test_input_help_word).setOnClickListener(v -> {
            mIntent(MnemonicWordActivity.class);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}

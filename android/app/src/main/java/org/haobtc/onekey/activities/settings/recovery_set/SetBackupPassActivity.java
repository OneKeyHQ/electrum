package org.haobtc.onekey.activities.settings.recovery_set;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetBackupPassActivity extends BaseActivity {

    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.btn_next)
    Button btnNext;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_backup_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {
        passEditStyle();
    }

    private void passEditStyle() {
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    btnNext.setEnabled(true);
                    btnNext.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }
        });
    }

    @OnClick({R.id.img_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_next:

                break;
            default:
        }
    }
}

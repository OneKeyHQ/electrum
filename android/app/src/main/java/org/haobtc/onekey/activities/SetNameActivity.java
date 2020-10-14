package org.haobtc.onekey.activities;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.InitEvent;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;
@Deprecated
public class SetNameActivity extends BaseActivity {
    public static final String TAG = "org.haobtc.onekey.activities.SetNameActivity";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.number)
    TextView number;

    @Override
    public int getLayoutId() {
        return R.layout.name_setting;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.next:
//                if (nameEdit.getText().length() == 0) {
//                    Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Intent intent = new Intent(this, ActivatedProcessing.class);
//                intent.putExtra("name", nameEdit.getText().toString());
//                intent.putExtra("use_se", getIntent().getStringExtra("use_se"));
//                startActivity(intent);
//                finish();
                boolean useSE = getIntent().getBooleanExtra("use_se", false);
                if (isNFC) {
                    Intent intent = new Intent(this, CommunicationModeSelector.class);
                    intent.putExtra("tag", TAG);
                    intent.putExtra("name", nameEdit.getText().toString());
                    intent.putExtra("use_se", useSE);
                    startActivity(intent);
                   return;
                }
                EventBus.getDefault().post(new InitEvent(nameEdit.getText().toString(), useSE));
//                Intent intent = new Intent(this, PinSettingActivity.class);
//                intent.putExtra("tag", TAG);
//                intent.putExtra("pin_type", 2);
//                startActivity(intent);
                break;
            default:
        }
    }

    @OnTextChanged(value = R.id.name_edit)
    public void onTextChanged(CharSequence text) {
        number.setText(String.format(Locale.ENGLISH, "%d/8", text.length()));
        if (text.length() == 8) {
            number.setTextColor(Color.RED);
        } else {
           number.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}

package org.haobtc.onekey.activities.personalwallet.hidewallet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.utils.Daemon;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.xpub;

public class HideWalletActivity extends BaseActivity {

    public static final String TAG = HideWalletActivity.class.getSimpleName();
    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.testCheckHidewallet)
    TextView testCheckHidewallet;
    @BindView(R.id.testCheckTips)
    TextView testCheckTips;
    // new version code
    public String pin = "";
    private Dialog dialogBtoms;
    private EditText editBixinName;
    private TextView textView;
    private SharedPreferences.Editor edit;
    private int defaultKeyNum;
    private int defaultKeyNameNum;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hide_wallet;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        defaultKeyNum = preferences.getInt("defaultKeyNum", 0);
    }

    @Override
    public void initData() {

    }
    @SingleClick
    @OnClick({R.id.img_backCreat, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                finish();
                break;
            case R.id.btnNext:
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(null);
                CommunicationModeSelector.runnables.add(runnable2);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private Runnable runnable2 = () -> showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub);

    private void showConfirmPubDialog(Context context, @LayoutRes int resource, String xpub) {
        //set see view
        View view = View.inflate(context, resource, null);
        dialogBtoms = new Dialog(context, R.style.dialog);
        editBixinName = view.findViewById(R.id.edit_keyName);
        TextView tetNum = view.findViewById(R.id.txt_textNum);
        textView = view.findViewById(R.id.text_public_key_cosigner_popup);
        textView.setText(xpub);
        defaultKeyNameNum = defaultKeyNum + 1;
        editBixinName.setText(String.format("pub%s", String.valueOf(defaultKeyNameNum)));
        editBixinName.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tetNum.setText(String.format(Locale.CHINA, "%d/20", input.length()));
                if (input.length() > 19) {
                    mToast(getString(R.string.moreinput_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String strBixinname = editBixinName.getText().toString();
            String strSweep = textView.getText().toString();
            if (TextUtils.isEmpty(strBixinname)) {
                mToast(getString(R.string.input_name));
                return;
            }
            String strXpub = "[\"" + strSweep + "\"]";
            try {
                Daemon.commands.callAttr("import_create_hw_wallet", strBixinname, 1, 1, strXpub, new Kwarg("hide_type", true));
            } catch (Exception e) {
                e.printStackTrace();
                String message = e.getMessage();
                if ("BaseException: file already exists at path".equals(message)) {
                    mToast(getString(R.string.changewalletname));
                }else {
                    assert message != null;
                    if (message.contains("The same xpubs have create wallet")){
                        String haveWalletName = message.substring(message.indexOf("name=")+5);
                        mToast(getString(R.string.xpub_have_wallet) + haveWalletName);

                    }
                }
                return;
            }
            edit.putInt("defaultKeyNum",defaultKeyNameNum);
            edit.apply();
            dialogBtoms.cancel();
            // todo: 弹窗关闭
            Intent intent = new Intent(HideWalletActivity.this, CheckHideWalletActivity.class);
            intent.putExtra("hideWalletName", strBixinname);
            startActivity(intent);
            finish();
        });

        //cancel dialog
        view.findViewById(R.id.img_cancle).setOnClickListener(v -> {
            dialogBtoms.cancel();
            // todo: 弹窗关闭
        });

        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.show();
    }
}

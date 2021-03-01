package org.haobtc.onekey.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;

/**
 * @author liyan
 * @date 12/17/20
 */
public class SoftPassActivity extends BaseActivity
        implements SoftPassFragment.PasswordTitleChangeCallback,
                SoftPassFragment.OnPasswordSuccessCallback {

    private static final String EXT_FROM_TYPE = StringConstant.FROM;
    private static final String EXT_RESULT_FROM_TYPE = "from_type";
    private static final String EXT_RESULT_PWD = "wallet_pwd";

    private static Intent obtainIntent(Context context, int from) {
        Intent intent = new Intent(context, SoftPassActivity.class);
        intent.putExtra(EXT_FROM_TYPE, from);
        return intent;
    }

    private static Intent obtainIntent(Context context, int operate, int from) {
        Intent intent = new Intent(context, SoftPassActivity.class);
        intent.putExtra(EXT_FROM_TYPE, from);
        intent.putExtra(Constant.OPERATE_TYPE, operate);
        return intent;
    }

    public static void start(Context context, int from) {
        context.startActivity(obtainIntent(context, from));
    }

    public static void startOperate(Context context, int operate) {
        context.startActivity(obtainIntent(context, operate, -1));
    }

    public static void startForResult(Activity activity, int resultCode, int from) {
        activity.startActivityForResult(obtainIntent(activity.getBaseContext(), from), resultCode);
    }

    public static class ResultDataBean {

        public final String password;
        public final int fromType;

        public ResultDataBean(String password, int fromType) {
            this.password = password;
            this.fromType = fromType;
        }
    }

    public static ResultDataBean decodeResultData(Intent intent) {
        int from = intent.getIntExtra(EXT_RESULT_FROM_TYPE, 44);
        String pwd = intent.getStringExtra(EXT_RESULT_PWD);
        return new ResultDataBean(pwd, from);
    }

    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.text_page_title)
    TextView textPageTitle;

    private int fromType = -1;

    @Override
    public void init() {
        fromType = getIntent().getIntExtra(EXT_FROM_TYPE, -1);

        int extOperate = getIntent().getIntExtra(Constant.OPERATE_TYPE, 1);
        SoftPassFragment softPassFragment = SoftPassFragment.newInstance(extOperate);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.view_container, softPassFragment)
                .commit();

        imgBack.setOnClickListener(
                v -> {
                    finish();
                });
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_soft_pass;
    }

    private void setResult(@NotNull String pwd, int fromType) {
        Intent intent = new Intent();
        intent.putExtra(EXT_RESULT_PWD, pwd);
        intent.putExtra(EXT_RESULT_FROM_TYPE, fromType);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void setTitle(String title) {
        if (textPageTitle == null) {
            return;
        }
        textPageTitle.setText(title);
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public void onSuccess(String pwd) {

        GotPassEvent gotPassEvent = new GotPassEvent(pwd);
        gotPassEvent.fromType = fromType;
        EventBus.getDefault().post(gotPassEvent);

        setResult(pwd, fromType);
        finish();
    }
}

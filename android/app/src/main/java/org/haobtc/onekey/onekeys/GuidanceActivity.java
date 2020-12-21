package org.haobtc.onekey.onekeys;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.NavUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author jinxiaomin
 */
public class GuidanceActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    @BindView(R.id.btn_begin)
    Button btnBegin;
    @BindView(R.id.text_user1)
    TextView userTV;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId () {
        return R.layout.activity_guidance;
    }

    @Override
    public void initView () {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        inits();
    }

    private void inits () {
        setAgreementPolicy();
        checkboxOk.setOnCheckedChangeListener(this);
    }

    private void setAgreementPolicy () {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.use_onekey));
        String userAgreement = getResources().getString(R.string.user_agreement_guide);
        SpannableString userAgreementSpannable = new SpannableString(userAgreement);
        userAgreementSpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick (@NonNull View widget) {
                NavUtils.gotoCheckChainDetailWebActivity(mContext, StringConstant.USER_AGREEMENT, StringConstant.USER_URL);
            }

            @Override
            public void updateDrawState (@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.onekey));
                ds.setUnderlineText(false);
            }
        }, 0, userAgreement.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(userAgreementSpannable);
        builder.append(getString(R.string.and));
        String privacyPolicy = getString(R.string.privacy_policy);
        SpannableString privacyPolicySpannable = new SpannableString(privacyPolicy);
        privacyPolicySpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick (@NonNull View widget) {
                NavUtils.gotoCheckChainDetailWebActivity(mContext, StringConstant.PRI_POLICY, StringConstant.POLICY_URL);
            }

            @Override
            public void updateDrawState (@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.onekey));
                ds.setUnderlineText(false);
            }
        }, 0, privacyPolicy.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(privacyPolicySpannable);
        userTV.setMovementMethod(LinkMovementMethod.getInstance());
        userTV.setHighlightColor(getResources().getColor(android.R.color.transparent, null));
        userTV.setText(builder);
    }

    @Override
    public void initData () {
        set();
    }

    private void set () {
        edit.putBoolean("bluetoothStatus", true);//open bluetooth
        edit.apply();
        try {
            Daemon.commands.callAttr("set_currency", "CNY");
            Daemon.commands.callAttr("set_base_uint", "BTC");
            edit.putString("base_unit", "BTC");
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_rbf", true);
            edit.putBoolean("set_rbf", true);
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_unconf", false);
            edit.putBoolean("set_unconf", true);
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_syn_server", true);
            edit.putBoolean("set_syn_server", true);//setting synchronize server
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Daemon.commands.callAttr("set_dust", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        edit.putBoolean("set_prevent_dust", false);
        edit.apply();
    }

    @OnClick({R.id.btn_begin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_begin:
                edit.putBoolean("is_first_run", true);
                edit.apply();
                mIntent(HomeOneKeyActivity.class);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            btnBegin.setBackground(getDrawable(R.drawable.btn_checked));
            btnBegin.setEnabled(true);
        } else {
            btnBegin.setBackground(getDrawable(R.drawable.btn_no_check));
            btnBegin.setEnabled(false);
        }
    }
}

package org.haobtc.onekey.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.settings.VerificationSuccessActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ResultEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;

public class VerificationKEYActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.first_promote)
    TextView firstPromote;
    @BindView(R.id.second_promote)
    TextView secondPromote;
    private final int MAX_LEVEL = 10000;
    @BindView(R.id.third_promote)
    TextView thirdPromote;

    @Override
    public int getLayoutId() {
        return isNFC ? R.layout.processing_nfc : R.layout.processing_ble;

    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        // 设置沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        EventBus.getDefault().register(this);
        List<Drawable> drawables = new ArrayList<>();
        if (isNFC) {
            secondPromote.setText(R.string.order_sending);
        } else {
            firstPromote.setText(R.string.order_sending);
        }
        drawables.addAll(Arrays.asList(firstPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(secondPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(thirdPromote.getCompoundDrawables()));
        drawables.stream().filter(Objects::nonNull)
                .forEach(drawable -> {
                    ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
                    animator.setDuration(800);
                    animator.setRepeatCount(-1);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                });
    }

    @Override
    public void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSuccess(ResultEvent event) {
        String result = event.getResult();
        EventBus.getDefault().removeStickyEvent(ResultEvent.class);
        verification(result);

    }

    private void verification(String result) {
        HashMap<String, String> pramas = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            pramas.put("data", jsonObject.getString("data"));
            pramas.put("signature", jsonObject.getString("signature"));
            pramas.put("cert", jsonObject.getString("cert"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().url("https://key.bixin.com/lengqian.bo/")
                .params(pramas)
                .build()
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.i("strVerification", "onError: ---- " + e.getMessage());
                        if (Objects.requireNonNull(e.getMessage()).contains("No address associated with hostname")) {
                            mToast(getString(R.string.internet_wrong));
                        }
                        Drawable drawableStartFail = getDrawable(R.drawable.shibai);
                        Objects.requireNonNull(drawableStartFail).setBounds(0, 0, drawableStartFail.getMinimumWidth(), drawableStartFail.getMinimumHeight());
                        if (isNFC) {
                            secondPromote.setText(R.string.connect_failed);
                            secondPromote.setCompoundDrawables(drawableStartFail, null, null, null);
                        } else {
                            firstPromote.setText(R.string.connect_failed);
                            firstPromote.setCompoundDrawables(drawableStartFail, null, null, null);
                        }
                        thirdPromote.setCompoundDrawables(drawableStartFail, null, null, null);
                        Intent intent = new Intent(VerificationKEYActivity.this, VerificationSuccessActivity.class);
                        intent.putExtra("verification_fail", "verificationConnect_fail");
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onResponse(String response) {
//                        Log.i("strVerification", "onResponse:------- " + response);
                        Drawable drawableStart = getDrawable(R.drawable.chenggong);
                        Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
                        firstPromote.setCompoundDrawables(drawableStart, null, null, null);
                        secondPromote.setCompoundDrawables(drawableStart, null, null, null);
                        thirdPromote.setCompoundDrawables(drawableStart, null, null, null);
                        if (response.contains("is_verified")) {
                            try {
                                Intent intent = new Intent(VerificationKEYActivity.this, VerificationSuccessActivity.class);
                                JSONObject jsonObject = new JSONObject(response);
                                boolean isVerified = jsonObject.getBoolean("is_verified");
                                if (isVerified) {
                                    String lastCheckTime = jsonObject.getString("last_check_time");
                                    if (!TextUtils.isEmpty(lastCheckTime)) {
                                        intent.putExtra("verification_fail", "verification");
                                        intent.putExtra("last_check_time", lastCheckTime);
                                    }
                                }
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(VerificationKEYActivity.this, VerificationSuccessActivity.class);
                                intent.putExtra("verification_fail", "verification_fail");
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Intent intent = new Intent(VerificationKEYActivity.this, VerificationSuccessActivity.class);
                            intent.putExtra("verification_fail", "verification_fail");
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}

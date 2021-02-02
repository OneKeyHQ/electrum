package org.haobtc.onekey.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.ui.base.BaseActivity;
import zendesk.chat.ChatEngine;
import zendesk.messaging.Engine;
import zendesk.messaging.MessagingActivity;
import zendesk.support.SupportEngine;
import zendesk.support.requestlist.RequestListActivity;

public class SupportActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SupportActivity.class));
    }

    @Override
    public void init() {
        setLeftTitle(getString(R.string.service_support));
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_supportctivity;
    }

    @Override
    protected boolean showToolBar() {
        return true;
    }

    @SingleClick
    @OnClick({R.id.commit, R.id.view_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit:
                Engine supportEngine = SupportEngine.engine();
                Engine chatEngine = ChatEngine.engine();

                MessagingActivity.builder().withEngines(supportEngine).show(mContext);
                break;
            case R.id.view_history:
                RequestListActivity.builder().show(mContext);
                break;
        }
    }
}

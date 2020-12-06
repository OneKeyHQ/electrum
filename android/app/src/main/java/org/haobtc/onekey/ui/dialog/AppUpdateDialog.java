package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azhon.appupdate.manager.DownloadManager;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/26/20
 */

public class AppUpdateDialog extends BaseDialogFragment {

    @BindView(R.id.version_code)
    TextView versionNameText;
    @BindView(R.id.update_description)
    TextView updateDescription;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;
    @BindView(R.id.progressBarLayout)
    public LinearLayout progressBarLayout;
    @BindView(R.id.close)
    ImageView close;
    private DownloadManager manager;
    private String versionName;
    private String description;

    public AppUpdateDialog(DownloadManager manager, String versionName, String description) {
        this.manager = manager;
        this.versionName = versionName;
        this.description = description;
    }

    @Override
    public void init() {
        versionNameText.setText(versionName);
        updateDescription.setText(description);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.app_upgrade_dialog;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
    @SingleClick
    @OnClick({R.id.update, R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update:
                progressBarLayout.setVisibility(View.VISIBLE);
                update.setVisibility(View.GONE);
                progressBar.setIndeterminate(true);
                manager.download();
                break;
            case R.id.close:
                dismiss();
                manager.cancel();
                break;
        }
    }

    @Override
    public boolean requireGravityCenter() {
        return true;
    }
}

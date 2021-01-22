package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.onekeys.walletprocess.createsoft.CreateSoftWalletActivity;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportSoftWalletActivity;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择导入钱包还是创建钱包的弹窗
 *
 * @author liyan
 * @date 12/18/20
 */
public class CreateWalletWaySelectorDialog extends BaseDialogFragment {
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.btn_create)
    Button btnNext;
    @BindView(R.id.btn_import)
    Button btnImport;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.add_wallet;
    }

    @OnClick({R.id.img_cancel, R.id.btn_create, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_create:
                CreateSoftWalletActivity.start(getContext());
                dismiss();
                break;
            case R.id.btn_import:
                ImportSoftWalletActivity.start(getContext());
                dismiss();
                break;
        }
    }
}

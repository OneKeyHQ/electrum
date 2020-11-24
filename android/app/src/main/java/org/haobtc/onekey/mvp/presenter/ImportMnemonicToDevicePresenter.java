package org.haobtc.onekey.mvp.presenter;

import org.haobtc.onekey.bean.MnemonicInfo;
import org.haobtc.onekey.mvp.base.BasePresenter;
import org.haobtc.onekey.mvp.view.IImportMnemonicToDeviceView;
import org.haobtc.onekey.manager.PyEnv;

import java.util.List;

public class ImportMnemonicToDevicePresenter extends BasePresenter<IImportMnemonicToDeviceView> {

    public ImportMnemonicToDevicePresenter(IImportMnemonicToDeviceView view) {
        super(view);
    }

    public void importMnemonics(List<MnemonicInfo> list){
        String mnemonicInfos = getMnemonics(list);

    }


    private String getMnemonics(List<MnemonicInfo> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i).getMnemonic());
            if (i < list.size() - 1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
}

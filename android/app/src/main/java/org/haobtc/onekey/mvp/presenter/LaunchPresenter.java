package org.haobtc.wallet.mvp.presenter;


import org.haobtc.wallet.mvp.base.BaseMvpPresenter;
import org.haobtc.wallet.mvp.model.ILaunchModel;
import org.haobtc.wallet.mvp.model.impl.LaunchModel;
import org.haobtc.wallet.mvp.view.ILaunchView;

public class LaunchPresenter extends BaseMvpPresenter<ILaunchView, ILaunchModel> {

    public LaunchPresenter(ILaunchView view) {
        super(view, new LaunchModel());
    }


    public void hello(){
        if(!mModel.isHello())return;
        if(getView() != null){
            getView().helloWord();
        }
    }
}

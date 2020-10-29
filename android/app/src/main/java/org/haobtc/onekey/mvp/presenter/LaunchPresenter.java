package org.haobtc.onekey.mvp.presenter;


import org.haobtc.onekey.mvp.base.BaseMvpPresenter;
import org.haobtc.onekey.mvp.model.ILaunchModel;
import org.haobtc.onekey.mvp.model.impl.LaunchModel;
import org.haobtc.onekey.mvp.view.ILaunchView;

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

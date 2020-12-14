package org.haobtc.onekey.utils;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.bean.EventObj;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 * @CreateDate: 2020/12/14$ 11:02 AM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/14$ 11:02 AM$
 * @UpdateRemark: 更新说明：
 */
public class EventBusUtils {

    public static void register(Object context){
        EventBus.getDefault().register(context);
    }

    public static void sendEvent(EventObj event) {
        EventBus.getDefault().post(event);
    }


    public static void unRegister(Object context){
        EventBus.getDefault().unregister(context);
    }
}

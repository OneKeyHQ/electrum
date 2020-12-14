package org.haobtc.onekey.manager;

import android.app.Activity;

import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 * @CreateDate: 2020/12/14$ 11:54 AM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/14$ 11:54 AM$
 * @UpdateRemark: 更新说明：
 */
public class ActivityHQManager {
    private static ActivityHQManager activity;

    public List<Activity> activities = new ArrayList<>();

    public ActivityHQManager() {

    }

    public static ActivityHQManager getInstance() {
        if (null == activity) {
            synchronized (ActivityHQManager.class) {
                if (null == activity) {
                    activity = new ActivityHQManager();
                }
            }
        }
        return activity;
    }

    // 返回首页
    public void gotoMain() {
        if (activities != null && activities.size() > 0) {
            for (int i = activities.size() - 1; i >= 0; i--) {
                if (activities.get(i) instanceof HomeOneKeyActivity) {

                } else {
                    activities.get(i).finish();
                    activities.remove(i);
                }
            }
        }
    }

    // 返回引导备份页面
    public void backToBackupGuideActivity() {
        if (activities != null && activities.size() > 0) {
            for (int i = activities.size() - 1; i >= 0; i--) {
                if (activities.get(i) instanceof BackupGuideActivity) {

                } else {
                    activities.get(i).finish();
                    activities.remove(i);
                }
            }
        }
    }


}

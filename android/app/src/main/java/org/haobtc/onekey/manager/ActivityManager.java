package org.haobtc.onekey.manager;
import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * @Description: 管理Activity的栈管理，可以方便的返回指定页面
 * @Author: peter Qin
 */
public class ActivityManager {
    private static ActivityManager mInstance;
    private Stack<WeakReference<Activity>> mActivityStack;

    public ActivityManager () {
    }

    public static ActivityManager getInstance () {
        if (null == mInstance) {
            synchronized (ActivityManager.class) {
                if (null == mInstance) {
                    mInstance = new ActivityManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加Activity 到栈中
     *
     * @param activity
     */
    public void addActivity (Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(new WeakReference<>(activity));
    }

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    public void checkWeakReference () {
        if (mActivityStack != null) {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> activityWeakReference = iterator.next();
                Activity temp = activityWeakReference.get();
                if (temp == null) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 获取当前Activity（栈中最后一个压入的）
     *
     * @return
     */
    public Activity getCurrentActivity () {
        checkWeakReference();
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            return mActivityStack.lastElement().get();
        }
        return null;
    }

    public void finishActivity () {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            // 关掉
            finishActivity(activity);
        }
    }

    public void finishActivity (Activity activity) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器安全删除
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> activityWeakReference = iterator.next();
                Activity temp = activityWeakReference.get();
                // 清理掉已经释放的activity
                if (temp == null) {
                    iterator.remove();
                    continue;
                }
                if (temp == activity) {
                    iterator.remove();
                }
            }
            activity.finish();
        }
    }

    /**
     * 关闭指定Activity之外所有的Activity
     *
     * @param cls
     */
    public void finishLeftActivity (Class<?> cls) {
        if (mActivityStack != null) {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            List<WeakReference<Activity>> list = new ArrayList<>();
            while (iterator.hasNext()) {
                WeakReference<Activity> activityReference = iterator.next();
                Activity activity = activityReference.get();
                // 清理掉已经释放的activity
                if (activity == null) {
                    iterator.remove();
                    continue;
                } else {
                    list.add(activityReference);
                }
                if (!activity.getClass().equals(cls)) {
                    iterator.remove();
                    activity.finish();
                }
            }
        }
    }

    /**
     * 关闭指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity (Class<?> cls) {
        if (mActivityStack != null) {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> activityReference = iterator.next();
                Activity activity = activityReference.get();
                // 清理掉已经释放的activity
                if (activity == null) {
                    iterator.remove();
                    continue;
                }
                if (activity.getClass().equals(cls)) {
                    iterator.remove();
                    activity.finish();
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity () {
        if (mActivityStack != null) {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> activityReference = iterator.next();
                Activity activity = activityReference.get();
                if (activity != null) {
                    activity.finish();
                }
            }
            mActivityStack.clear();
        }
    }

    /**
     * 退出应用程序
     */
    public void exitApp () {
        try {
            finishAllActivity();
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0);
            // 从系统中kill掉应用程序
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }
    }

}

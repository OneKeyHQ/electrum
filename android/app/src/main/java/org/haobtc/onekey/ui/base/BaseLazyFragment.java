package org.haobtc.onekey.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 可以懒加载的 Fragment
 *
 * @author Onekey@QuincySx
 * @create 2021-01-14 2:03 PM
 */
public abstract class BaseLazyFragment extends BaseFragment {
    private boolean isFirstLoad = true; // 是否第一次加载

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        isFirstLoad = true;
    }

    @Override
    public void onResume () {
        super.onResume();
        if (isFirstLoad) {
            // 将数据加载逻辑放到onResume()方法中
            onLazy();
            initEvent();
            isFirstLoad = false;
        }
    }

    /**
     * 初始化数据
     */
    protected void onLazy () {

    }

    /**
     * 初始化事件
     */
    protected void initEvent () {

    }
}

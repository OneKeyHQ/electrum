package org.haobtc.onekey.bean;
import org.haobtc.onekey.ui.widget.tablayout.CustomTabEntity;

/**
 * @Description: 底部tabLayout的Bean
 * @Author: peter Qin
 */
public class TabEntity implements CustomTabEntity {

    public String title;
    public int selectedIcon;
    public int unSelectedIcon;

    public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }


    @Override
    public String getTabTitle () {
        return title;
    }

    @Override
    public int getTabSelectedIcon () {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon () {
        return unSelectedIcon;
    }

}

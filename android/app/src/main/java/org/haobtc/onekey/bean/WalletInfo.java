package org.haobtc.onekey.bean;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @Description: 钱包信息的数据模型
 * @Author: peter Qin
 * @CreateDate: 2021/1/14 6:13 PM
 */
public class WalletInfo implements MultiItemEntity {
    /**
     * type : btc-hw-derived-1-1
     * addr : bcrt1q5jdpq0nkyd2f9gn4nzd3lsj3lc8m2qykr0fxh0
     * name : 3dd510b6535968c0214165238468c750bed086955a11155fe3cff665fe00c7e5
     * label : The
     * device_id : "A9CCAA79760C69FC47089E12"
     */
    public String type;
    public String addr;
    public String name;
    public String label;
    public String deviceId;
    public int itemType;

    @Override
    public int getItemType () {
        return itemType;
    }

}

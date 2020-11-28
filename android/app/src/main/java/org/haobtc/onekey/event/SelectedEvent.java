package org.haobtc.onekey.event;

import java.util.List;

/**
 * @author liyan
 * @date 11/27/20
 */

public class SelectedEvent {
    private List<String>  nameList;

    public SelectedEvent(List<String> nameList) {
        this.nameList = nameList;
    }

    public List<String> getNameList() {
        return nameList;
    }
}

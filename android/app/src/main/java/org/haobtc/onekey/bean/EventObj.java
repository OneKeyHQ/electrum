package org.haobtc.onekey.bean;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 * @CreateDate: 2020/12/14$ 11:05 AM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/14$ 11:05 AM$
 * @UpdateRemark: 更新说明：
 */
public class EventObj<T>  {
    private Event event;
    private T data;

    public EventObj(Event event, T data) {
        this.event = event;
        this.data = data;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

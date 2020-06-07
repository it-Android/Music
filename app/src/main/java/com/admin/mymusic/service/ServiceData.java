package com.admin.mymusic.service;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/6 14:44
 **/
public class ServiceData {
    private Object object;
    private int was;

    private int serviceId;

    public ServiceData(Object object, int was) {
        this.object = object;
        this.was = was;
    }


    public ServiceData(Object object, int was, int serviceId) {
        this.object = object;
        this.was = was;
        this.serviceId = serviceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }


    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getWas() {
        return was;
    }

    public void setWas(int was) {
        this.was = was;
    }
}

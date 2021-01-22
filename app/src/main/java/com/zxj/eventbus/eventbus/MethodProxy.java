package com.zxj.eventbus.eventbus;

import com.zxj.eventbus.interfaces.ThreadMode;

import java.lang.reflect.Method;

public class MethodProxy {

    /**
     * 参数类型
     */
    private Class<?> paramsType;

    /**
     * 方法本省
     */
    private Method method;

    /**
     * 方法上的注解
     */
    private ThreadMode threadMode;

    public MethodProxy(Class<?> paramsType, Method method, ThreadMode threadMode) {
        this.paramsType = paramsType;
        this.method = method;
        this.threadMode = threadMode;
    }

    public Class<?> getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class<?> paramsType) {
        this.paramsType = paramsType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }
}

package com.zxj.eventbus.eventbus;

import android.os.Handler;
import android.os.Looper;

import com.zxj.eventbus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class EventBus {

    /**
     * 对象方法集合
     * key为注册的对象
     * value为注解对象里所有标注了Subscribe注解的方法
     */
    private Map<Object, List<MethodProxy>> methodProxyMap;

    private static volatile EventBus eventBus;

    private EventBus() {
        methodProxyMap = new HashMap<>();
    }

    public static EventBus getDefault() {
        if (eventBus == null) {
            synchronized (EventBus.class) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    public void register(Object object) {
        List<MethodProxy> methodProxyList = methodProxyMap.get(object);
        if (methodProxyList == null || methodProxyList.size() == 0) {
            methodProxyList = new ArrayList<>();
            //获取到对象的类对象
            Class clazz = object.getClass();
            String clazzName = clazz.getName();
            //先过滤掉系统中的类，因为我们自己定义的注解不可能在系统类中出现
            if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android.")) {
                return;
            }
            //返回该类对象和其父类对象的所有共有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //获取方法上的Subscribe注解，如果方法上没有得到的值为null
                Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                if (subscribeAnnotation == null) {
                    continue;
                }

                //获取方法的返回类型
                Type genericReturnType = method.getGenericReturnType();
                if (!"void".equals(genericReturnType.toString())) {
                    throw new RuntimeException(method.getName() + " return value must not be !void type");
                }

                //返回方法的参数类型数组
                Class<?>[] parameterType = method.getParameterTypes();
                if (parameterType.length != 1) {
                    throw new RuntimeException(method.getName() + " parameter length must be 1");
                }

                MethodProxy methodProxy = new MethodProxy(parameterType[0], method, subscribeAnnotation.threadMode());
                methodProxyList.add(methodProxy);
            }
            methodProxyMap.put(object, methodProxyList);
        }

    }

    public void post(Object postObject) {
        Set<Object> registerObjectSet = methodProxyMap.keySet();
        for (Object registerObject : registerObjectSet) {
            List<MethodProxy> methodProxyList = methodProxyMap.get(registerObject);
            if (methodProxyList != null && methodProxyList.size() > 0) {
                for (MethodProxy methodProxy : methodProxyList) {
                    if (methodProxy.getParamsType().isAssignableFrom(postObject.getClass())) {
                        switch (methodProxy.getThreadMode()) {
                            case MAIN:
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    //如果当前发送事件是在主线程
                                    try {
                                        methodProxy.getMethod().invoke(registerObject, postObject);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //如果当前发送事件是在子线程
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                methodProxy.getMethod().invoke(registerObject, postObject);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                                break;
                            case BACKGROUND:
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    //如果当前发送事件是在主线程
                                    Executors.newFixedThreadPool(1).submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                methodProxy.getMethod().invoke(registerObject, postObject);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    //如果当前发送事件是在子线程
                                    try {
                                        methodProxy.getMethod().invoke(registerObject, postObject);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    public void unregister(Object object) {
        methodProxyMap.remove(object);
    }
}

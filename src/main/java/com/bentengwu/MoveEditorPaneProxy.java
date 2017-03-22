package com.bentengwu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前类用来获取接口处理移动画布的类.
 *
 * @Author <a href="bentengwu@163.com">thender.xu</a>
 * @Date 2017/3/20 16:50.
 */
public class MoveEditorPaneProxy {
    protected final static Logger logger = LoggerFactory.getLogger(MoveEditorPaneProxy.class);

    private static final MoveEditorPaneProxy instance = new MoveEditorPaneProxy();

    private static final Map<Class<?>, base_intf> collectionIntf = new HashMap<>();


    public static final synchronized MoveEditorPaneProxy getInstance() {
        return instance;
    }

    public <T> T getHandler(Class<T> clazz) {
        return (T)collectionIntf.get(clazz);
    }
}

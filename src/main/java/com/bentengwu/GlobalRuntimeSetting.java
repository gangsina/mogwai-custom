package com.bentengwu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这里用于存放程序运行中的一些值.
 * Created by thender on 2017/3/19.
 */
public abstract class GlobalRuntimeSetting {
    private static final Logger logger = LoggerFactory.getLogger(GlobalRuntimeSetting.class);
    /**
     * 用于标识ctrl是否处于按下的状态.
     */
    private static boolean ctrling = false;

    private final static int ctrlKeyCode = 23;//TODO make sure ?

    /**
     * Sets ctrling.
     *
     * @param isKeyDown the is key down
     * @param keyCode   the key code
     */
    public static synchronized void setCtrling(boolean isKeyDown, int keyCode) {
//        logger.info("The keyCode --> {},{}",isKeyDown,keyCode);
        if (ctrlKeyCode == keyCode && isKeyDown) {
            ctrling = true;
        }
    }


    /**
     * @author thender.xu
     * @date 20170320
     * @return
     */
    public static synchronized boolean isCtrling() {
        return ctrling;
    }
}

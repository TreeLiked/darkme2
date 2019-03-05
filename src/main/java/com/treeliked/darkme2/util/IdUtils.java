package com.treeliked.darkme2.util;

import java.util.UUID;

/**
 * id util
 *
 * @author lqs2
 * @date 2018/12/5, Wed
 */
public class IdUtils {

    /**
     * 获取32长id，去除小短横
     */
    public static String get32Id() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 获取8长id，去除小短横
     */
    public static String get8Id() {
        return get32Id().substring(0,8);
    }
}

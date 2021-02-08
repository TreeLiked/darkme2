package com.treeliked.darkme2.constant;

/**
 * session keys
 *
 * @author lqs2
 * @date 2018-12-19, Wed
 */
public class SessionConstant {

    /**
     * 在会话中存储用户名的key
     */
    public static final String KEY_OF_USER_NAME = "USER_NAME";
    public static final String KEY_OF_USER_ID = "USER_ID";


    /**
     * 临时登录，单位s
     */
    public static final int TIME_OF_TEMP = 3600;

    /**
     * 记住我
     */
    public static final int TIME_OF_REMEMBER = 3600 * 24 * 15;


    /**
     * 用来保存用户session id
     */
    public static final String COOKIE_OF_USER_INFO = "USER_INFO_COOKIE";

    /**
     * 会话超时返回的信息
     */
    public static final String TIME_OUT = "会话超时，请重新登录";
}

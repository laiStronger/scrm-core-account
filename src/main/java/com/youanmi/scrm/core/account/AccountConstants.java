/*
 * 文件名：AccountConstants.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月19日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account;

/**
 * 账号体系常量
 * 
 * @author tanguojun 2016年12月19日
 * @version 1.0.0
 */
public interface AccountConstants {

    /**
     * 用户常量
     * 
     * @author tanguojun on 2016年12月19日
     *
     */
    interface User {

        /**
         * 用户密码加密盐值
         */
        String PWD_SALT = "youanmiusersalt";
    }

    interface Org {
        /**
         * 最大层级数
         */
        Integer MAX_ORG_LEVEL = 11;
    }
}

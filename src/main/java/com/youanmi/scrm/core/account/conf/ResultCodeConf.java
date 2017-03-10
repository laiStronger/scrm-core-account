package com.youanmi.scrm.core.account.conf;

import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;
import com.youanmi.scrm.commons.util.cache.ApplicationConfCache;
import com.youanmi.scrm.commons.util.file.PropertiesUtils;


/**
 * resultCode配置
 * 
 * @author tanguojun
 * @since 2.2.4
 */
@Service
@DisconfUpdateService
public class ResultCodeConf implements IDisconfUpdate, InitializingBean {

    /**
     * result.code文件名称
     */
    public static final String RESULT_CODE_NAME = "common.result.code.properties";

    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(ResultCodeConf.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("init {}", RESULT_CODE_NAME);
        reload();
    }


    @Override
    public void reload() throws Exception {
        LOG.info("reload {" + RESULT_CODE_NAME + "}");
        // 初始化fastdfs配置
        Properties p = PropertiesUtils.getProperties(RESULT_CODE_NAME);
        if (p != null && !p.entrySet().isEmpty()) {
            for (Entry<Object, Object> s : p.entrySet()) {
                ApplicationConfCache.setConf(RESULT_CODE_NAME, (String) s.getKey(), (String) s.getValue());
            }
        }
    }
}

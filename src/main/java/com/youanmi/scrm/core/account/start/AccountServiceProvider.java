package com.youanmi.scrm.core.account.start;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class AccountServiceProvider {

    private static final Log log = LogFactory.getLog(AccountServiceProvider.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        try {
            // String[]
            // config={"classpath:webconfig/spring-context.xml","classpath:webconfig/spring-mvc.xml","classpath:webconfig/spring-server-provider.xml"};
            String[] config = {"classpath:application-context.xml"};
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(config);
            context.start();
            log.info("== DubboProvider started!");
            Thread.currentThread().join();
        }
        catch (Exception e) {
            log.error("== DubboProvider context start error:", e);
        }
    }

}
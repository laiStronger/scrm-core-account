package com.youanmi.scrm.core.account.service.user;

import com.youanmi.scrm.api.account.dto.user.UserInfoDto;
import com.youanmi.scrm.api.account.service.user.IUserInfoService;
import com.youanmi.scrm.core.account.AccountConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author laishaoqiang on 2016/12/21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class UserInfoServiceTest {
    @Resource
    private IUserInfoService userInfoService;

    @Test
    public void insertSelective() throws Exception {
        UserInfoDto dto = new UserInfoDto();
        dto.setUserName("fdsfsdf");
        dto.setPassword("fdsfsd");
        Long result = userInfoService.insertSelective(dto);
        System.out.println(result);
    }

    @Test
    public void selectUser() {
        Map<String, Object> conditions = new HashMap<String, Object>();

        List<UserInfoDto> userList = new ArrayList<UserInfoDto>();
        System.out.println("开始输出数据");
        conditions.put("searchCondition","1");
        try {
            userList = userInfoService.getOrgUserListByConditions(conditions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (UserInfoDto user:userList) {
            System.out.println("数据如下：");
            System.out.println(user.toString());
        }
    }

    /**
     * 
     * @Description: 测试商户编码
     * @author li.jinwen
     * @email lijinwen@youanmi.com
     * @date 2017年3月7日 上午10:13:17
     * @throws Exception
     */
    @Test
    public void testCommercialTenantCode() throws Exception{
    	System.out.println(userInfoService.getCommercialTenantCode(1L));
    }
    
}
package com.youanmi.scrm.core.account.service.org;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.youanmi.scrm.api.account.dto.org.OrgDetailInfoDto;
import com.youanmi.scrm.api.account.service.org.IOrgDetailInfoService;

/**
 * 添加类的一句话简单描述。
 * <p>
 * 详细描述
 *
 * @author Administrator on 2016/12/20
 * @since ${version}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class OrgDetailInfoServiceTest {

    @Resource(name = "orgDetailInfoService")
    private IOrgDetailInfoService orgDetailInfoService;

    @Test
    public void addOrgDetailInfo() throws Exception {
        OrgDetailInfoDto dto = new OrgDetailInfoDto();
        dto.setProvinceId(12L);
        dto.setCityId(121L);
        dto.setAreaId(1056L);
        dto.setAreaName("福田区");
        dto.setCityName("深圳市");
        dto.setProvinceName("广东省");
        dto.setBusinessLicense("12345789");
        dto.setAddress("车公庙天安数码城创新科技广场一期B座");
        dto.setOrgId(4L);
        orgDetailInfoService.addOrgDetailInfo(dto);
    }

}
package com.youanmi.scrm.core.account.service.org;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.constants.AccountTableConstants;
import com.youanmi.scrm.api.account.dto.OperatorDto;
import com.youanmi.scrm.api.account.dto.org.AddOrgDto;
import com.youanmi.scrm.api.account.dto.org.DepartmentTreeDto;
import com.youanmi.scrm.api.account.dto.org.OrgInfoDto;
import com.youanmi.scrm.api.account.dto.org.UpdateOrgNameDto;
import com.youanmi.scrm.api.account.service.org.IOrgInfoService;
import com.youanmi.scrm.commons.util.json.JsonUtils;

/**
 * @author sunxiaolong on 2016/12/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class OrgInfoServiceTest {
    @Resource
    private IOrgInfoService orgInfoService;

    @Test
    public void departmentTree() throws Exception {
        OperatorDto dto = new OperatorDto();

        dto.setOperatorTopOrgId(1L);
        DepartmentTreeDto departmentTreeDto = orgInfoService.departmentTree(dto);
        System.out.println(departmentTreeDto);
    }

    @Test
    public void getOrgById() throws Exception {

    }

    @Test
    public void addShopOrg() throws Exception {

        OrgInfoDto orgInfoDto = new OrgInfoDto();
        orgInfoDto.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_SHOP);
        orgInfoDto.setParentOrgId(3L);
        orgInfoDto.setOrgName("南山分公司");
        orgInfoService.addShopOrg(orgInfoDto);

        /*OrgInfoDto orgInfo = new OrgInfoDto();
        orgInfo.setOrgType(TableConstants.Org.ORG_TYPE_SINGLE_SHOP);
        orgInfo.setOrgName("绿兹原椰子鸡");
        orgInfoService.addShopOrg(orgInfo);*/
    }

    @Test
    public void getOrgChildOrgType() throws Exception {
        Byte orgChildOrgType = orgInfoService.getOrgChildOrgType(1L);
        System.out.println(orgChildOrgType);
    }

    @Test
    public void updateOrgName() throws Exception {
        UpdateOrgNameDto dto = new UpdateOrgNameDto();
        dto.setName("深圳总部");
        dto.setId(3L);
        orgInfoService.updateOrgName(dto);
    }

    @Test
    public void deleteOrg() throws Exception {

    }

    @Test
    public void getOrg() throws Exception {

    }

    @Test
    public void getOrgAndTopOrgInfoById() throws Exception {

    }

    @Test
    public void getDirectOrg() throws Exception {
        System.out.println(JsonUtils.toJSON(orgInfoService.getDirectOrg(1L)));
    }

    @Test
    public void getShopList() throws Exception {

        PageBean<OrgInfoDto> pageBean = new PageBean<>();
        pageBean.setPageIndex(1);
        pageBean.setPageSize(10);
        Map<String, Object> param = new HashMap<>();
        pageBean.setParamObject(param);
        System.out.println("-----------------------------");
        System.out.println(JsonUtils.toJSON(orgInfoService.getShopList(pageBean)));
    }

    @Test
    public void getOrgByStaffId() throws Exception {

    }

    @Test
    public void addOrg1() throws Exception {
        AddOrgDto dto = new AddOrgDto();
        dto.setName("广州总部");
        dto.setParentId(2L);
        orgInfoService.addOrg(dto);
    }

    @Test
    public void addOrg() throws Exception {
        AddOrgDto dto = new AddOrgDto();
        dto.setName("车公庙分部");
        dto.setParentId(3L);
        orgInfoService.addOrg(dto);
    }

}
package com.youanmi.scrm.core.account.service.org;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.dto.org.OrgStaffDetailDto;
import com.youanmi.scrm.api.account.service.shop.IShopStaffService;

/**
 * 
 * @ClassName: TestPageBean
 * @Description: 测试分页
 * @author li.jinwen
 * @email lijinwen@youanmi.com
 * @date 2017年2月8日 下午7:56:33
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class TestPageBean {

	@Autowired
	private IShopStaffService shopStaffService;

	/**
	 * 系统原有分页查询测试。
	 */
	@Test
	public void getPageBean() {
		PageBean<OrgStaffDetailDto> page = new PageBean<>();
		page.setPageIndex(1);
		page.setPageSize(10);

		Map<String, Object> params = new HashMap<>();
		params.put("topOrgId", "1");

		page.setParamObject(params);

		page = shopStaffService.getStaffList(page);
		System.err.println(JSONObject.toJSONString(page));
	}

	
	/**
	 * 代码拦截参数分页实现。
	 */
	@Test
	public void getPageBean22() {
		PageBean<OrgStaffDetailDto> page = new PageBean<>();
		page.setPageIndex(2);
		page.setPageSize(3);

		Map<String, Object> params = new HashMap<>();
		params.put("topOrgId", "1");

		page.setParamObject(params);

		page = shopStaffService.getStaffList222(page);
		System.err.println(JSONObject.toJSONString(page));
	}

}

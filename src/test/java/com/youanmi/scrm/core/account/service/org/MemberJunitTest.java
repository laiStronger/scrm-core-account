package com.youanmi.scrm.core.account.service.org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.youanmi.scrm.api.account.dto.member.MemberInfoDto;
import com.youanmi.scrm.api.account.service.member.IMemberInfoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class MemberJunitTest {

	@Resource
	private IMemberInfoService memberInfoService;
	
	@Test
	public void add(){
		MemberInfoDto dto = new MemberInfoDto();
		dto.setBirthday(System.currentTimeMillis());
		dto.setArea("深圳");
		dto.setUserName("lijinwen");
		dto.setGender(Byte.valueOf("1"));
		int i = memberInfoService.add(dto);
		System.out.println(i);
	}
	
	@Test
	public void insert(){
		MemberInfoDto dto = new MemberInfoDto();
		dto.setId(2L);
		dto.setBirthday(System.currentTimeMillis());
		dto.setArea("深圳");
		dto.setUserName("lijinwen");
		dto.setGender(Byte.valueOf("1"));
		dto.setHeadUrl("www.baidu.com");
		dto.setThumHeadUrl("www.baidu.com");
		dto.setWxNo("lijinwen");
		dto.setWxName("李进文");
		dto.setCreateTime(System.currentTimeMillis());
		dto.setUpdateTime(System.currentTimeMillis());
		dto.setIsDelete(Byte.valueOf("1"));
		int i = memberInfoService.insert(dto);
		System.out.println(i);
	}
	
	@Test
	public void selectById(){
		MemberInfoDto dto = memberInfoService.findById(1L);
		if(dto instanceof MemberInfoDto){
			System.out.println("类型：MemberInfoDto");
		}
		System.out.println("data:"+JSONObject.toJSONString(dto));
	}
	
	@Test
	public void selectByParams(){
		Map<String, Object> params = new HashMap<>();
		params.put("gender", Byte.valueOf("1"));
		List<MemberInfoDto> dtos = memberInfoService.findByParams(params);
		System.out.println("data:"+JSONObject.toJSONString(dtos));
	}
	
	@Test
	public void updateById(){
		MemberInfoDto dto = new MemberInfoDto();
		dto.setId(1L);
		dto.setBirthday(System.currentTimeMillis());
		dto.setArea("深圳2222");
		dto.setUserName("lijinwen2222");
		dto.setGender(Byte.valueOf("1"));
		int i = memberInfoService.modifyById(dto);
		System.out.println(i);
	}
	
	@Test
	public void deleteById(){
		int i = memberInfoService.deleteById(1L);
		System.out.println(i);
	}
	
}
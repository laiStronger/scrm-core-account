package com.youanmi.scrm.core.account.service.member;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.youanmi.commons.base.exception.BaseException;
import com.youanmi.scrm.api.account.dto.member.OrgMemberInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youanmi.commons.base.service.impl.BaseCrudServiceImpl;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.dto.member.MemberInfoDto;
import com.youanmi.scrm.api.account.service.member.IMemberInfoService;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.member.MemberInfoPo;
import com.youanmi.scrm.core.account.utils.PagingUtils;

public class MemberInfoService extends BaseCrudServiceImpl<MemberInfoDto> implements IMemberInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected String getNameSpace() {
		return MappersConstants.MemberInfo.NAMESPACE;
	}

	@Override
	protected Class<?> getPoClazz() {
		return MemberInfoPo.class;
	}

	@Override
	public PageBean<OrgMemberInfoDto> getPageByBirthday(Long birthday, Integer pageIndex, Integer pageSize) {
		logger.info(StringUtils.join("params birthday:", birthday, ",pageIndex:", pageIndex, ",pageSize:", pageSize));

		return PagingUtils.paging(baseDAO, MappersConstants.MemberInfo.GET_PAGE_BY_BIRTHDDAY, birthday, pageIndex, pageSize, OrgMemberInfoDto.class);
	}

	@Override
	public int insert(MemberInfoDto entity) throws BaseException {
		logger.info("params entity:"+ JSON.toJSONString(entity));

		MemberInfoPo memberInfoPo = BeanCopyUtils.map(entity,MemberInfoPo.class);
		int row = baseDAO.save(MappersConstants.MemberInfo.INSERT, memberInfoPo);
		entity.setId(memberInfoPo.getId());
		return row;
	}

	@Override
	public MemberInfoDto getByWxNo(String wxNo) {
		logger.info("params wxNo:", wxNo);

		return BeanCopyUtils.map(baseDAO.findForObject(MappersConstants.MemberInfo.GET_BY_WX_NO, wxNo), MemberInfoDto.class);
	}

	@Override
	public MemberInfoDto getByUserName(String userName) {
		logger.info("params userName:", userName);

		return BeanCopyUtils.map(baseDAO.findForObject(MappersConstants.MemberInfo.GET_BY_USERNAME, userName), MemberInfoDto.class);
	}

	@Override
	public List<MemberInfoDto> getMemberInfoByConditions(Map<String, Object> conditions) throws ParseException {
		LOG.debug("begin getMemberInfoByConditions.");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<MemberInfoPo> memberInfoList = new ArrayList<>();

		String startSendTimeStr = (String) conditions.get("startSendTimeStr");
		String endSendTimeStr = (String) conditions.get("endSendTimeStr");
		if (startSendTimeStr != null && !startSendTimeStr.isEmpty()) {
			conditions.put("startSendTime", sdf.parse(startSendTimeStr).getTime());
		}
		if (endSendTimeStr != null && !endSendTimeStr.isEmpty()) {
			conditions.put("endSendTime", sdf.parse(endSendTimeStr).getTime() + (1000 * 60 * 60 * 24));//加一天
		}
		//获取查询条件
		String queryType = (String) conditions.get("searchCondition");
		String content = (String) conditions.get("content");
		if (queryType.equals("1")) {
			conditions.put("wxNo", content);
		} else if (queryType.equals("2")) {
			conditions.put("mobilePhone", content);
		} else if (queryType.equals("3")) {
			conditions.put("userName", content);
		}

		LOG.info("获取到的参数为：" + conditions.toString());
		try {
			memberInfoList = baseDAO.findForList(MappersConstants.MemberInfo.GET_MEMBER_INFO_BY_CONDITIONS, conditions);
		} catch (Exception e) {
			LOG.error("user list acquired failed");
		}
		LOG.info("end getMemberInfoByConditions.");

		return BeanCopyUtils.mapList(memberInfoList, MemberInfoDto.class);
	}

	@Override
	public MemberInfoDto getMemberInfoById(Long memberId) {
		if (memberId == null || memberId <= 0L) {
			LOG.error(memberId + "不是合法的会员id！");
			return null;
		}

		MemberInfoPo memberInfoPo = null;

		try {
			memberInfoPo = baseDAO.findForObject(MappersConstants.MemberInfo.GET_MEMBER_INFO_BY_ID, memberId);
		} catch (Exception e) {
			LOG.error("获取memberInfo失败！原因：" + e);
			return null;
		}

		return BeanCopyUtils.map(memberInfoPo, MemberInfoDto.class);
	}
}

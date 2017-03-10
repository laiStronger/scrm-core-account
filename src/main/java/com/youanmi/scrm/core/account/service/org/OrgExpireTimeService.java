package com.youanmi.scrm.core.account.service.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.utils.StringUtil;
import com.youanmi.scrm.api.account.dto.org.OrgExpireTimeDto;
import com.youanmi.scrm.api.account.service.org.IOrgExpireTimeService;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.OrgExpireTimePo;

public class OrgExpireTimeService implements IOrgExpireTimeService {

	/**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrgExpireTimeService.class);

    @Autowired
    private BaseDAO baseDAO;
	
	@Override
	public OrgExpireTimeDto getOrgExpireTimePoByOrgId(Long topOrgId) throws ViewExternalDisplayException {
		if(!StringUtil.isNotNull(topOrgId)){
			LOG.info("OrgExpireTimeService-getOrgExpireTimePoByOrgId-topOrgId参数为空");
			return null;
		}
		OrgExpireTimePo expireTimePo = baseDAO.findForObject(MappersConstants.OrgExpireTime.GET_ORG_EXPIRE_TIME_BY_ORG_ID, topOrgId);
		return BeanCopyUtils.map(expireTimePo, OrgExpireTimeDto.class);
	}

}

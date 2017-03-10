/*
 * 文件名：OrgDetailInfoService.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月16日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.service.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.scrm.api.account.dto.org.OrgAddressDto;
import com.youanmi.scrm.api.account.dto.org.OrgDetailInfoDto;
import com.youanmi.scrm.api.account.service.org.IOrgDetailInfoService;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.OrgDetailInfoPo;


/**
 * 机构详细信息service
 *
 * @author tanguojun 2016年12月16日
 * @version 1.0.0
 */
public class OrgDetailInfoService implements IOrgDetailInfoService {
    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrgDetailInfoService.class);

    @Autowired
    private BaseDAO baseDAO;

    /**
     * 新增机构详情
     * 
     * @param dto 机构详情
     */
    @Override
    public void addOrgDetailInfo(OrgDetailInfoDto dto) {
        OrgDetailInfoPo infoPo = BeanCopyUtils.map(dto, OrgDetailInfoPo.class);
        if (null == infoPo) {
            throw new RuntimeException("机构详情异常");
        }
        Long currentTime = System.currentTimeMillis();
        infoPo.setCreateTime(currentTime);
        infoPo.setUpdateTime(currentTime);
        baseDAO.save(MappersConstants.OrgDetail.ADD_ORG_DETAIL, infoPo);
    }

    @Override
    public OrgDetailInfoDto getOrgDetailByOrgId(Long orgId) {

        LOG.info("orgId=" + orgId);

        // 查询机构详情
        OrgDetailInfoPo orgDetailInfoPo =
                baseDAO.findForObject(MappersConstants.OrgDetail.GET_ORG_DETAIL_BY_ORG_ID, orgId);

        if (orgDetailInfoPo == null) {
            return null;
        }

        // 返回对象
        return BeanCopyUtils.map(orgDetailInfoPo, OrgDetailInfoDto.class);
    }
    
    /**
     * 
     * 编辑门店信息
     * 
     * @param dto 门店信息
     */
    @Override
    public void updateByParam(OrgDetailInfoDto dto) {
        OrgDetailInfoPo po = BeanCopyUtils.map(dto, OrgDetailInfoPo.class);
        baseDAO.update(MappersConstants.OrgDetail.UPDATE_BY_PARAM, po);
    }

    @Override
    @Transactional
    public OrgAddressDto getOrgDetail(Long orgId, Long topOrgId) {
        if(orgId.equals(topOrgId)) {
            //单店
            return baseDAO.findForObject(MappersConstants.OrgDetail.GET_ORG_ADDRESS, orgId);
        } else {
            //查询当前门店信息
            OrgAddressDto dto = baseDAO.findForObject(MappersConstants.OrgDetail.GET_ORG_ADDRESS, orgId);
            //查询顶级机构信息
            dto.setParentAddress(baseDAO.findForObject(MappersConstants.OrgDetail.GET_ORG_ADDRESS, topOrgId));
            return dto;
        }
    }


}

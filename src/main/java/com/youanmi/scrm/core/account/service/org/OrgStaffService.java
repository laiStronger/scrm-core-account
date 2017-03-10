/*
 * 文件名：OrgStaffService.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月16日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.service.org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.helper.PageUtils;
import com.youanmi.commons.base.helper.ValidateViewExceptionUtils;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.constants.AccountTableConstants;
import com.youanmi.scrm.api.account.dto.org.OrgStaffDto;
import com.youanmi.scrm.api.account.dto.user.ShopEmployeeDto;
import com.youanmi.scrm.api.account.dto.user.UserInfoDto;
import com.youanmi.scrm.api.account.service.org.IOrgStaffService;
import com.youanmi.scrm.commons.constants.result.ResultCode;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.OrgInfoPo;
import com.youanmi.scrm.core.account.po.org.OrgPostPo;
import com.youanmi.scrm.core.account.po.org.OrgStaffPo;
import com.youanmi.scrm.core.account.po.user.UserInfoPo;


/**
 * 机构员工service
 * 
 * @author tanguojun 2016年12月16日
 * @version 1.0.0
 */
public class OrgStaffService implements IOrgStaffService {
    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrgStaffService.class);

    private static final Byte DELETE = 2;

    @Autowired
    private BaseDAO baseDAO;

    @Override
    public OrgStaffDto getStaffByUserId(Long userId) {

        LOG.info("userId=" + userId);

        // 查询账户信息
        OrgStaffPo staffPo = baseDAO.findForObject(MappersConstants.Staff.GET_STAFF_BY_USERID, userId);

        if (staffPo == null) {
            return null;
        }

        // 返回账号信息
        return BeanCopyUtils.map(staffPo, OrgStaffDto.class);
    }

    /**
     * 更新实体
     */
    @Override
    @Transactional
    public void updateByPrimaryKeySelective(OrgStaffDto orgStaffDto) {
        OrgStaffPo po = BeanCopyUtils.map(orgStaffDto, OrgStaffPo.class);
        baseDAO.update(MappersConstants.Staff.UPDATE_BY_PRIMARYKEY_SELECTIVE, po);
    }

    @Override
    public void updateLastLoginTimeById(Long id, Long lastLoginTime) {

        LOG.info("id=" + id + ",lastLoginTime=" + lastLoginTime);

        OrgStaffPo orgStaffPo = new OrgStaffPo();
        // 设置员工id
        orgStaffPo.setId(id);
        // 设置最后登录时间
        orgStaffPo.setLastLoginTime(lastLoginTime);

        // 修改员工最后登录时间
        baseDAO.update(MappersConstants.Staff.UPDATE_BY_PRIMARYKEY_SELECTIVE, orgStaffPo);
    }

    /**
     * 
     * 添加员工
     */
    @Override
    public Long insertSelective(OrgStaffDto orgStaffDto) {
        OrgStaffPo po = BeanCopyUtils.map(orgStaffDto, OrgStaffPo.class);

        baseDAO.save(MappersConstants.Staff.INSERT_SELECTIVE, po);

        Long id = po.getId();
        return id;
    }

    /**
     * 
     * 根据员工id获取员工实体
     */
    @Override
    public OrgStaffDto getOrgStaffById(Long id) {
        OrgStaffPo orgStaffPo = baseDAO.findForObject(MappersConstants.Staff.SELECT_BY_PRIMARYKEY, id);

        OrgStaffDto dto = BeanCopyUtils.map(orgStaffPo, OrgStaffDto.class);
        return dto;
    }

    /**
     * 设置店员岗位id为空
     * 
     * @param id 店员id
     */
    @Override
    public void setPostNull(Long id) {
        baseDAO.update(MappersConstants.Staff.SET_POST_NULL, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindNewStaffAndUserAndDelOldUser(Long newStaffId, Long oldUserId, Long newUserId)
            throws ViewExternalDisplayException {

        LOG.info("newStaffId=" + newStaffId + ",oldUserId=" + oldUserId + ",newUserId=" + newUserId);

        Long updateTime = System.currentTimeMillis();

        OrgStaffPo orgStaffPo = new OrgStaffPo();
        // 设置员工id
        orgStaffPo.setId(newStaffId);
        // 设置绑定的userId
        orgStaffPo.setUserId(oldUserId);
        // 设置修改时间
        orgStaffPo.setUpdateTime(updateTime);

        // 修改绑定关系
        if (0 < baseDAO.update(MappersConstants.Staff.UPDATE_BY_PRIMARYKEY_SELECTIVE, orgStaffPo)) {
            UserInfoPo deleteUser = new UserInfoPo();
            // 设置新账号id
            deleteUser.setId(newUserId);
            deleteUser.setUpdateTime(updateTime);
            // 删除旧账号
            if (1 > baseDAO.update(MappersConstants.User.UPDATE_DELETE_STATUS, deleteUser)) {
                // 删除失败,抛出异常
                throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
            }
        }
        else {
            // 绑定失败,抛出异常
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindNewStaffAndDelOldStaffAndNewUser(Long newStaffId, Long oldUserId, Long oldStaffId,
            Long newUserId) throws ViewExternalDisplayException {

        LOG.info("newStaffId=" + newStaffId + ",oldUserId=" + oldUserId + ",oldStaffId=" + oldStaffId
                + ",newUserId=" + newUserId);

        Long updateTime = System.currentTimeMillis();
        // 绑定新员工和旧用户的关系
        OrgStaffPo orgStaffPo = new OrgStaffPo();
        // 设置员工id
        orgStaffPo.setId(newStaffId);
        // 设置绑定的userId
        orgStaffPo.setUserId(oldUserId);
        // 设置修改时间
        orgStaffPo.setUpdateTime(updateTime);

        // 修改绑定关系
        if (0 < baseDAO.update(MappersConstants.Staff.UPDATE_BY_PRIMARYKEY_SELECTIVE, orgStaffPo)) {

            UserInfoPo deleteUser = new UserInfoPo();
            // 设置新账号id
            deleteUser.setId(newUserId);
            deleteUser.setUpdateTime(updateTime);

            // 删除旧账号
            if (0 < baseDAO.update(MappersConstants.User.UPDATE_DELETE_STATUS, deleteUser)) {

                OrgStaffPo deleteStaff = new OrgStaffPo();
                deleteStaff.setId(oldStaffId);
                deleteStaff.setUpdateTime(updateTime);
                // 删除旧员工
                if (1 > baseDAO.update(MappersConstants.Staff.UPDATE_DELETE_STATUS, deleteStaff)) {
                    // 删除失败,抛出异常
                    throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
                }
            }
            else {
                // 删除失败,抛出异常
                throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
            }
        }
        else {
            // 绑定失败,抛出异常
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindNewStaffAndDelOldStaffAndPostAndNewUser(Long newStaffId, Long oldUserId, Long oldStaffId,
            Long newUserId, Long oldPostId) throws ViewExternalDisplayException {

        LOG.info("newStaffId=" + newStaffId + ",oldUserId=" + oldUserId + ",oldStaffId=" + oldStaffId
                + ",newUserId=" + newUserId + ",oldPostId=" + oldPostId);

        // 设置删除岗位参数
        OrgPostPo deletePost = new OrgPostPo();
        deletePost.setId(oldPostId);
        deletePost.setUpdateTime(System.currentTimeMillis());

        // 删除旧岗位
        if (0 < baseDAO.update(MappersConstants.Post.UPDATE_DELETE_STATUS, deletePost)) {
            // 绑定新员工信息和旧用户的关系,删除新账号&旧员工
            bindNewStaffAndDelOldStaffAndNewUser(newStaffId, oldUserId, oldStaffId, newUserId);
        }
        else {
            // 删除失败,抛出异常
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageBean<OrgStaffDto> findOrgStaffList(Long topOrgId, String inputValue, Integer pageIndex,
            Integer pageSize, Long orgId) throws ViewExternalDisplayException {

        LOG.info("topOrgId=" + topOrgId + ",inputValue=" + inputValue + ",orgId=" + orgId + ",pageIndex="
                + pageIndex + ",pageSize=" + pageSize);

        // 模糊查询值,用户名&手机号&员工姓名
        String value = null;

        if (StringUtils.isNotBlank(inputValue)) {
            // 去除空格
            inputValue = inputValue.trim();
            ValidateViewExceptionUtils.validateLength(ResultCode.System.PARAMETER_NOT_NULL, inputValue, 30,
                "查询条件");

            // 防止百分号匹配查询所有
            inputValue = inputValue.replaceAll("%", "\\\\%");

            // 账号
            value = inputValue;
        }

        // 直属机构不为空
        if (orgId != null) {

            // 不能查询最顶级的部门员工,属于非法请求
            if (orgId.equals(topOrgId)) {
                throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
            }

            OrgInfoPo queryOrgInfo = new OrgInfoPo();
            queryOrgInfo.setId(orgId);
            queryOrgInfo.setTopOrgId(topOrgId);
            // 设置机构类型为连锁部门
            queryOrgInfo.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);
            // 查询直属机构id是否不是顶级机构下的部门
            int row = baseDAO.findForObject(MappersConstants.Org.IS_EXIST_ORG_BY_TOP_ID_AND_ID, queryOrgInfo);

            // 没有查询到该顶级机构下的子机构
            if (row < 1) {
                throw new ViewExternalDisplayException(ResultCode.Org.DEPARTMENT_NOT_EXIST);
            }
        }

        // 判断并获取pageIndex
        pageIndex = PageUtils.getPageIndex(pageIndex, 1);
        // 判断并获取pageSize
        pageSize = PageUtils.getPageSize(pageSize, 20);

        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);

        // 参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("value", value);
        params.put("topOrgId", topOrgId);
        params.put("orgId", orgId);
        params.put("orgType", AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);

        // 查询机构员工列表
        List<OrgStaffPo> datas = baseDAO.findForList(MappersConstants.Staff.SELECT_ORG_STAFF_LIST, params);

        // 转换列表
        Page<OrgStaffPo> lists = (Page<OrgStaffPo>) datas;

        // 转换返回数据
        PageBean<OrgStaffDto> page =
                PageUtils.getPageBean(pageIndex, pageSize, new Long(lists.getTotal()).intValue(),
                    lists.getPages(), BeanCopyUtils.mapList(lists, OrgStaffDto.class));

        return page;
    }

    /**
     * 查询岗位关联的员工列表
     * 
     * @param postId 岗位id
     * @return List<OrgStaffDto> 岗位关联的员工列表
     * @author 张秋平 2016-12-29
     */
    @Override
    public List<OrgStaffDto> getOrgStaffPoListByPostId(Long postId) {
        // 根据岗位id获取关联的店员
        List<OrgStaffPo> datas =
                baseDAO.findForList(MappersConstants.Staff.GET_ORG_STAFF_PO_LIST_BY_POST_ID, postId);
        if (null == datas || datas.isEmpty()) {
            return null;
        }
        return BeanCopyUtils.mapList(datas, OrgStaffDto.class);
    }

    @Override
    public UserInfoDto getBasicStaffInfo(Long staffId) {
        return baseDAO.findForObject(MappersConstants.Staff.GET_BASIC_STAFF_INFO, staffId);
    }

    @Override
    public Long getClerkUserIdByOrg(Long orgId) {
        return baseDAO.findForObject(MappersConstants.Staff.GET_CLERK_USER, orgId);
    }

    @Override
    public List<ShopEmployeeDto> loadEmployeesByOrgId(Long orgId) throws Exception {
        return baseDAO.findForList(MappersConstants.Staff.LOAD_EMPLOYEES_BY_ORG_ID, orgId);
    }

    @Override
    public void deleteStaff(Long staffId) {
        OrgStaffDto orgStaffById = getOrgStaffById(staffId);
        OrgStaffDto dto = new OrgStaffDto();
        dto.setId(staffId);
        dto.setUpdateTime(System.currentTimeMillis());
        dto.setIsDelete(DELETE);
        updateByPrimaryKeySelective(dto);
        // 删除user
        UserInfoPo userInfo = new UserInfoPo();
        userInfo.setId(orgStaffById.getUserId());
        userInfo.setIsDelete(DELETE);
        userInfo.setUpdateTime(System.currentTimeMillis());
        baseDAO.update(MappersConstants.User.UPDATE_BY_PRIMARYKEY_SELECTIVE, userInfo);
    }

    @Override
    public Long getCountStaffByTopOrg(Long topOrgId) {

        LOG.info("topOrgId=" + topOrgId);

        Long count = baseDAO.findForObject(MappersConstants.Staff.GET_COUNT_STAFF_BY_TOP_ORG, topOrgId);

        return count;
    }

}

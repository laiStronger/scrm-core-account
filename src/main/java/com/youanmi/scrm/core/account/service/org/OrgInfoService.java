/*
 * 文件名：OrgInfoService.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月16日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.service.org;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youanmi.commons.base.constants.GlobalConstants;
import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.helper.PageUtils;
import com.youanmi.commons.base.helper.ValidateViewExceptionUtils;
import com.youanmi.commons.base.utils.StringUtil;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.constants.AccountTableConstants;
import com.youanmi.scrm.api.account.dto.IdDto;
import com.youanmi.scrm.api.account.dto.OperatorDto;
import com.youanmi.scrm.api.account.dto.org.*;
import com.youanmi.scrm.api.account.service.org.IOrgInfoService;
import com.youanmi.scrm.api.account.service.user.IUserInfoService;
import com.youanmi.scrm.commons.constants.result.ResultCode;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.commons.util.string.AssertUtils;
import com.youanmi.scrm.core.account.AccountConstants;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.*;
import com.youanmi.scrm.core.account.po.user.UserInfoPo;
import com.youanmi.scrm.core.account.service.user.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * 机构信息service
 *
 * @author tanguojun 2016年12月16日
 * @version 1.0.0
 */
@Service
public class OrgInfoService implements IOrgInfoService {
    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrgInfoService.class);

    @Autowired
    private BaseDAO baseDAO;

    @Resource
    private IUserInfoService userInfoService;

    @Override
    public List<OrgInfoDto> getDirectOrg(Long topOrgId) {
        List<Long> parentOrg = baseDAO.findForList(MappersConstants.Org.GET_ALL_PARENT_ORG, topOrgId);
        Map<String, Object> param = new HashMap<>();
        param.put("topOrgId", topOrgId);
        param.put("parentOrg", parentOrg);
        return BeanCopyUtils.mapList(baseDAO.findForList(MappersConstants.Org.GET_DIRECT_ORG, param),
            OrgInfoDto.class);
    }

    @Override
    public PageBean<OrgInfoDto> getShopList(PageBean<OrgInfoDto> pageBean) {
        // 获取分页参数
        Integer pageIndex = PageUtils.getPageIndex(pageBean.getPageIndex(), 1);
        Integer pageSize = PageUtils.getPageSize(pageBean.getPageSize(), 10);
        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);
        List<OrgInfoPo> list = baseDAO.findForList(MappersConstants.Org.GET_SHOP_LIST, pageBean);
        Page<OrgInfoPo> page = (Page<OrgInfoPo>) list;

        // 如果查询结果为空，直接返回
        if (AssertUtils.isNull(list)) {
            return PageUtils.getPageBean(pageIndex, pageSize, Long.valueOf(page.getTotal()).intValue(),
                page.getPages(), new ArrayList<>());
        }

        // 查询上述门店中的管理员和员工数
        List<Long> ids = new ArrayList<>();
        for (OrgInfoPo po : list) {
            ids.add(po.getId());
        }
        List<Map<String, Object>> adminList = baseDAO.findForList(MappersConstants.Org.GET_ADMIN_BY_ORG, ids);
        List<Map<String, Object>> staffCountList =
                baseDAO.findForList(MappersConstants.Org.GET_STAFF_COUNT_BY_ORG, ids);
        // 查询上述门店的管理员是否激活
        List<Map<String, Object>> activeList =
                baseDAO.findForList(MappersConstants.Staff.GET_LAST_LOGIN_TIME, ids);
        List<OrgInfoDto> forList = BeanCopyUtils.mapList(list, OrgInfoDto.class);
        for (OrgInfoDto dto : forList) {
            // 设置管理员账号
            for (Map<String, Object> admin : adminList) {
                if (dto.getId().equals(Long.valueOf(admin.get("orgId").toString()))) {
                    dto.setAdminAccount(admin.get("userName").toString());
                }
            }
            // 设置员工数
            for (Map<String, Object> staff : staffCountList) {
                if (dto.getId().equals(Long.valueOf(staff.get("orgId").toString()))) {
                    // 除去管理员
                    dto.setUserCount(Integer.valueOf(staff.get("count").toString()) - 1);
                }
            }
            // 设置是否可删除标志
            for (Map<String, Object> active : activeList) {
                if (null != active && dto.getId().equals(Long.valueOf(active.get("orgId").toString()))
                        && null != active.get("lastLoginTime")) {
                    // 当前门店管理员已登录过，则不允许删除
                    dto.setDeleteAllowed(false);
                }
            }
        }
        return PageUtils.getPageBean(pageIndex, pageSize, Long.valueOf(page.getTotal()).intValue(),
            page.getPages(), forList);
    }

    /**
     * 添加部门
     */
    @Transactional
    @Override
    public Long addOrg(AddOrgDto dto) {
        OrgInfoPo parent = getOrg(dto.getParentId());
        if (parent == null) {
            ValidateViewExceptionUtils.throwDataNotExistException();
        }
        if (!parent.getTopOrgId().equals(dto.getOperatorTopOrgId())) {
            ValidateViewExceptionUtils.throwIllegalOperationException("不是本组织的层级,不允许操作");
        }
        if (parent.getOrgLevel().equals(AccountTableConstants.Org.TOP_LEVEL)) {
            ValidateViewExceptionUtils.throwIllegalOperationException("顶级层级下不允许添加层级");
        }

        if (parent.getOrgLevel() >= AccountConstants.Org.MAX_ORG_LEVEL) {
            ValidateViewExceptionUtils.throwIllegalOperationException("层级数已达上限,不允许新增");
        }

        Byte childOrgType = getOrgChildOrgType(parent.getId());
        // 如果下属层级不为空且不是部门,则不允许添加下属部门
        if (childOrgType != null && !childOrgType.equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
            ValidateViewExceptionUtils.throwIllegalOperationException("该层级下已存在门店, 不允许再添加下属层级");
        }

        OrgInfoPo po = new OrgInfoPo();
        po.setOrgName(dto.getName());
        po.setOrgFullName(dto.getName());
        po.setParentOrgId(dto.getParentId());
        po.setCreateTime(System.currentTimeMillis());
        po.setUpdateTime(po.getCreateTime());
        po.setOrgLevel((byte) (parent.getOrgLevel() + 1));
        po.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);
        po.setTopOrgId(parent.getTopOrgId());

        if (parent.getOrgPath().endsWith(",")) {
            po.setOrgPath(parent.getOrgPath() + parent.getId() + ",");
        }
        else {
            po.setOrgPath(parent.getOrgPath() + "," + parent.getId() + ",");
        }

        Long id = baseDAO.findForObject(MappersConstants.Org.SELECT_ID_BY_TOP_ORG_AND_NAME, po);
        if (id != null) {
            throw new ViewExternalDisplayException(ResultCode.System.NAME_EXIST);
        }
        baseDAO.save(MappersConstants.Org.ADD_ORG, po);
        return po.getId();

    }

    /**
     * 获取一个部门的下属机构的机构类型,可能为部门或者门店,也可能为null
     */
    @Override
    public Byte getOrgChildOrgType(Long id) {
        return baseDAO.findForObject(MappersConstants.Org.GET_ORG_CHILD_ORG_TYPE, id);
    }

    /**
     * 修改部门名称
     */
    @Transactional
    @Override
    public void updateOrgName(UpdateOrgNameDto dto) {
        OrgInfoPo org = getOrg(dto.getId());
        if (org == null) {
            ValidateViewExceptionUtils.throwDataNotExistException();
        }
        if (!org.getTopOrgId().equals(dto.getOperatorTopOrgId())) {
            ValidateViewExceptionUtils.throwIllegalOperationException("不是本组织的层级,不允许操作");
        }
        if (org.getOrgLevel().equals(AccountTableConstants.Org.TOP_LEVEL)) {
            ValidateViewExceptionUtils.throwIllegalOperationException("顶级层级不允许修改名称");
        }

        if (!org.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
            ValidateViewExceptionUtils.throwIllegalOperationException("不是层级,不能修改部门名称");
        }
        OrgInfoPo po = new OrgInfoPo();
        po.setTopOrgId(org.getTopOrgId());
        po.setOrgName(dto.getName());
        po.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);
        Long id = baseDAO.findForObject(MappersConstants.Org.SELECT_ID_BY_TOP_ORG_AND_NAME, po);
        if (id != null) {
            throw new ViewExternalDisplayException(ResultCode.System.NAME_EXIST);
        }

        po = new OrgInfoPo();
        po.setId(dto.getId());
        po.setOrgName(dto.getName());
        po.setUpdateTime(System.currentTimeMillis());
        baseDAO.update(MappersConstants.Org.UPDATE_ORG, po);
    }

    @Override
    @Transactional
    public void deleteOrg(IdDto dto) {
        OrgInfoPo org = getOrg(dto.getId());
        if (org == null) {
            ValidateViewExceptionUtils.throwDataNotExistException();
        }
        if (!org.getTopOrgId().equals(dto.getOperatorTopOrgId())) {
            ValidateViewExceptionUtils.throwIllegalOperationException("不是本组织的层级,不允许操作");
        }

        if (!org.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
            ValidateViewExceptionUtils.throwIllegalOperationException("不是层级,不能删除");
        }
        // 顶级层级和二级层级不允许删除
        if (org.getOrgLevel() <= AccountTableConstants.Org.TOP_LEVEL + 1) {
            ValidateViewExceptionUtils.throwIllegalOperationException("顶级层级不能删除");
        }

        // 删除org前需要判断该org是不是无下属部门和下属员工
        Byte type = baseDAO.findForObject(MappersConstants.Org.GET_ORG_CHILD_ORG_TYPE, dto.getId());
        if (type != null) {
            ValidateViewExceptionUtils.throwIllegalOperationException("下属层级或者门店不为空,不能删除");
        }
        OrgStaffPo staffPo =
                baseDAO.findForObject(MappersConstants.Staff.SELECT_ONE_STAFF_BY_ORG_ID, dto.getId());
        if (staffPo != null) {
            ValidateViewExceptionUtils.throwIllegalOperationException("下属员工不为空,不能删除");
        }

        OrgInfoPo po = new OrgInfoPo();
        po.setId(dto.getId());
        po.setUpdateTime(System.currentTimeMillis());
        baseDAO.delete(MappersConstants.Org.DELETE_ORG, po);
    }

    public OrgInfoPo getOrg(Long id) {
        return baseDAO.findForObject(MappersConstants.Org.GET_ORG, id);
    }

    @Override
    public OrgInfoDto getOrgAndTopOrgInfoById(Long orgId) {
        LOG.info("orgId=" + orgId);

        // 查询机构信息
        OrgInfoPo orgInfoPo = baseDAO.findForObject(MappersConstants.Org.GET_ORG_BY_ID, orgId);

        // 如果机构为空,则返回空
        if (orgInfoPo == null) {
            return null;
        }

        // 转换机构信息dto
        OrgInfoDto orgInfoDto = BeanCopyUtils.map(orgInfoPo, OrgInfoDto.class);

        // 如果不是单店,则查询顶级机构信息
        if (orgInfoPo.getOrgType() != AccountTableConstants.Org.ORG_TYPE_SINGLE_SHOP) {

            // 如果顶级机构不是自己
            if (!Objects.equals(orgInfoPo.getTopOrgId(), orgInfoPo.getId())) {

                // 查询顶级机构信息
                OrgInfoPo topOrgInfoPo =
                        baseDAO.findForObject(MappersConstants.Org.GET_ORG_BY_ID, orgInfoPo.getTopOrgId());

                // 转换机构信息dto
                OrgInfoDto topOrgInfoDto = BeanCopyUtils.map(topOrgInfoPo, OrgInfoDto.class);

                // 设置顶级机构信息
                orgInfoDto.setTopOrgInfo(topOrgInfoDto);
            }
        }

        return orgInfoDto;
    }

    /**
     * 新增门店
     * 
     * @param orgInfoDto 店铺信息
     * @author liubing
     * @date 2016年12月21日
     * @return 店铺id
     */
    @Override
    public Long addShopOrg(OrgInfoDto orgInfoDto) {
        OrgInfoPo orgInfoPo = BeanCopyUtils.map(orgInfoDto, OrgInfoPo.class);
        if (null == orgInfoPo) {
            throw new RuntimeException("门店机构为空");
        }
        // 注册时,全称与简称一致
        orgInfoPo.setOrgFullName(orgInfoPo.getOrgName());
        // 设置创建时间
        orgInfoPo.setCreateTime(System.currentTimeMillis());
        orgInfoPo.setUpdateTime(System.currentTimeMillis());
        // 连锁门店
        if (Objects.equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_SHOP, orgInfoPo.getOrgType())) {
            OrgInfoPo parentOrg = getOrg(orgInfoPo.getParentOrgId());
            // 层级在上层基础上+1
            orgInfoPo.setOrgLevel((byte) (parentOrg.getOrgLevel() + 1));
            if (parentOrg.getOrgPath().endsWith(",")) {
                orgInfoPo.setOrgPath(parentOrg.getOrgPath() + parentOrg.getId() + ",");
            }
            else {
                orgInfoPo.setOrgPath(parentOrg.getOrgPath() + "," + parentOrg.getId() + ",");
            }
            orgInfoPo.setTopOrgId(parentOrg.getTopOrgId());
        }
        else {
            // 单店自己就是最顶层
            orgInfoPo.setOrgLevel((byte) 1);
            // 默认父节点为0
            orgInfoPo.setParentOrgId(0L);

        }
        baseDAO.save(MappersConstants.Org.ADD_ORG, orgInfoPo);
        Long id = orgInfoPo.getId();
        // 如果是单店，要修改顶部机构id为自己本身
        if (Objects.equals(AccountTableConstants.Org.ORG_TYPE_SINGLE_SHOP, orgInfoPo.getOrgType())) {
            OrgInfoPo po = new OrgInfoPo();
            po.setId(id);
            po.setTopOrgId(id);
            po.setUpdateTime(System.currentTimeMillis());
            baseDAO.update(MappersConstants.Org.UPDATE_ORG, po);
        }
        return id;
    }

    /**
     * 获取部门树
     */
    @Override
    public DepartmentTreeDto departmentTree(OperatorDto dto) {
        List<OrgInfoPo> departments =
                baseDAO.findForList(MappersConstants.Org.SELECT_BY_TOP_ORG_ID, dto.getOperatorTopOrgId());
        List<OrgStaffPo> staffs =
                baseDAO.findForList(MappersConstants.Staff.SELECT_BY_TOP_ORG_ID, dto.getOperatorTopOrgId());

        Map<Long, DepartmentTreeDto> departmentDtoMap = new HashMap<>();

        Map<Long, OrgInfoPo> departmentPoMap = new HashMap<>();
        DepartmentTreeDto topDto = null;
        for (OrgInfoPo po : departments) {
            DepartmentTreeDto d = new DepartmentTreeDto();
            d.setId(po.getId());
            d.setName(po.getOrgName());
            d.setOrgLevel(po.getOrgLevel());
            departmentDtoMap.put(d.getId(), d);
            departmentPoMap.put(d.getId(), po);
            if (po.getTopOrgId().equals(po.getId())) {
                topDto = d;
            }
        }

        departmentDtoMap.values().forEach(d -> {
            OrgInfoPo po = departmentPoMap.get(d.getId());
            DepartmentTreeDto parentDto = departmentDtoMap.get(po.getParentOrgId());
            if (parentDto != null) {
                if (po.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_SHOP)) {
                    Integer shopCount = parentDto.getShopCount();
                    if (shopCount == null) {
                        shopCount = 0;
                    }
                    parentDto.setShopCount(shopCount + 1);
                }
                else {
                    parentDto.getDepartments().add(d);
                }
            }
        });

        // 店员的用户id -> 店员的map
        Map<Long, DepartmentStaffDto> staffDtoMap = new HashMap<>();

        Map<Long, OrgStaffPo> staffPoMap = new HashMap<>();

        List<Long> userIds = new ArrayList<>();
        for (OrgStaffPo po : staffs) {
            OrgInfoPo department = departmentPoMap.get(po.getOrgId());
            // 如果该员工是部门里的则处理,不是(即为门店员工)则忽略
            if (department != null
                    && department.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
                DepartmentStaffDto e = new DepartmentStaffDto();
                e.setId(po.getId());
                e.setName(po.getStaffName());
                staffDtoMap.put(po.getUserId(), e);
                staffPoMap.put(po.getId(), po);
                userIds.add(po.getUserId());
                DepartmentTreeDto d = departmentDtoMap.get(po.getOrgId());
                d.getStaffs().add(e);
            }
        }
        if (!userIds.isEmpty()) {
            List<UserInfoPo> users = baseDAO.findForList(MappersConstants.User.FIND_USER_BY_IDS, userIds);
            for (UserInfoPo user : users) {
                DepartmentStaffDto staffDto = staffDtoMap.get(user.getId());
                staffDto.setUserName(user.getUserName());
                staffDto.setMobilePhone(user.getMobilePhone());
            }
        }
        sortDepartmentTree(topDto, departmentPoMap, staffPoMap);
        return topDto;
    }

    /**
     * 根据创建时间对层级和员工进行倒序排序
     * 
     * @param dto 需要排序的层级dto
     * @param departmentPoMap 用来获取层级的创建时间
     * @param staffPoMap 用来获取员工的创建时间
     */
    private void sortDepartmentTree(DepartmentTreeDto dto, Map<Long, OrgInfoPo> departmentPoMap,
            Map<Long, OrgStaffPo> staffPoMap) {
        try {
            if (dto != null) {
                if (dto.getDepartments() != null) {
                    dto.getDepartments().sort(
                        (o1, o2) -> {
                            long result =
                                    departmentPoMap.get(o1.getId()).getCreateTime()
                                            - departmentPoMap.get(o2.getId()).getCreateTime();
                            return result < 0 ? -1 : (result == 0 ? 0 : 1);
                        });
                    dto.getDepartments().forEach(d -> sortDepartmentTree(d, departmentPoMap, staffPoMap));
                }
                if (dto.getStaffs() != null) {
                    dto.getStaffs().sort(
                        (o1, o2) -> {
                            long result =
                                    staffPoMap.get(o1.getId()).getCreateTime()
                                            - staffPoMap.get(o2.getId()).getCreateTime();
                            return result < 0 ? -1 : (result == 0 ? 0 : 1);
                        });
                }
            }
        }
        catch (Exception e) {
            LOG.error("排序失败", e);
        }
    }

    /**
     * 获取当前连锁体系下其他门店
     *
     * @param topOrgId 顶级机构id
     * @param orgId 当前门店id
     * @return list
     */
    @Override
    public List<OrgInfoDto> getOtherOrg(Long topOrgId, Long orgId) {
        Map<String, Long> param = new HashMap<>();
        param.put("topOrgId", topOrgId);
        param.put("orgId", orgId);
        List<OrgInfoPo> list = baseDAO.findForList(MappersConstants.Org.GET_OTHER_ORG, param);
        return BeanCopyUtils.mapList(list, OrgInfoDto.class);
    }

    /**
     * 员工id获取机构实体
     */
    @Override
    public OrgInfoDto getOrgByStaffId(Long staffId) {
        OrgInfoPo po = baseDAO.findForObject(MappersConstants.Org.GET_ORG_BY_STAFFID, staffId);

        OrgInfoDto dto = BeanCopyUtils.map(po, OrgInfoDto.class);
        return dto;
    }

    /**
     * 根据id获取组织机构
     */
    @Override
    public OrgInfoDto getOrgById(Long id) {
        OrgInfoPo po = this.getOrg(id);

        OrgInfoDto dto = BeanCopyUtils.map(po, OrgInfoDto.class);
        return dto;
    }

    /**
     * 根据顶级机构获取整个组织机构,只要部门的
     */
    @Override
    public List<OrgInfoDto> selectAllChainOrg(Long topOrgId) {
        List<OrgInfoPo> list = baseDAO.findForList(MappersConstants.Org.SELECT_BY_TOP_ORG_ID, topOrgId);

        List<OrgInfoDto> resultList = new ArrayList<OrgInfoDto>();
        for (OrgInfoPo po : list) {
            OrgInfoDto dto = BeanCopyUtils.map(po, OrgInfoDto.class);

            if (dto.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
                resultList.add(dto);
            }

        }
        return resultList;
    }

    @Override
    public List<OrgInfoDto> getAllChainShopByTopOrg(Long topOrgId) {
        return BeanCopyUtils.mapList(baseDAO.findForList(MappersConstants.Org.SELECT_ALL_SHOP, topOrgId),
            OrgInfoDto.class);
    }

    /**
     * 
     * 更新实体
     */
    @Transactional
    @Override
    public void updateByPrimaryKeySelective(OrgInfoDto dto) {
        OrgInfoPo po = BeanCopyUtils.map(dto, OrgInfoPo.class);

        baseDAO.update(MappersConstants.Org.UPDATE_BY_PRIMARYKEY_SELECTIVE, po);
    }

    @Override
    public List<OrgInfoDto> getDepartmentOrgByTopOrgId(Long topOrgId) {

        LOG.info("topOrgId=" + topOrgId);

        // 实例化查询参数
        OrgInfoPo queryOrgInfo = new OrgInfoPo();

        queryOrgInfo.setTopOrgId(topOrgId);
        // 设置类型为连锁部门
        queryOrgInfo.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);

        List<OrgInfoPo> list =
                baseDAO.findForList(MappersConstants.Org.SELECT_LIST_BY_TOP_ID_AND_ORG_TYPE, queryOrgInfo);

        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // 返回dto列表
        return BeanCopyUtils.mapList(list, OrgInfoDto.class);
    }

    @Override
    public List<Long> getShopByTopOrgAndName(Long topOrgId, String orgName) {
        Map<String, Object> param = new HashMap<>();
        param.put("topOrgId", topOrgId);
        param.put("orgName", orgName);
        return baseDAO.findForList(MappersConstants.Org.GET_SHOP_BY_TOP_ORG_AND_NAME, param);
    }

    /**
     * 加上rollbackFor，默认给checked异常也添加事物处理
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editOrgInfo(OrgInfoDto orgDto, Object... extras) throws ViewExternalDisplayException {
        // 修改org信息。
        OrgInfoPo po = BeanCopyUtils.map(orgDto, OrgInfoPo.class);
        baseDAO.update(MappersConstants.Org.UPDATE_BY_PRIMARYKEY_SELECTIVE, po);
        // 判断orgDetailFileds是否不为null如果存在，则利用反射创建orgDetail信息，然后更新
        if (extras != null && extras.length > 0) {
            for (Object object : extras) {
                if (object instanceof OrgDetailInfoDto) {// 如果有额外信息并且是明细，则执行
                    OrgDetailInfoPo orgDetailPo = BeanCopyUtils.map(object, OrgDetailInfoPo.class);
                    baseDAO.update(MappersConstants.OrgDetail.UPDATE_BY_PRIMARYKEY_SELECTIVE, orgDetailPo);
                }
                // 扩展预留...
            }
        }
        // else 表示只有orgInfo更新
    }

    @Override
    public boolean uniqueneOrgParams(Map<String, Object> params) {
        Integer count = baseDAO.findForObject(MappersConstants.Org.UNIQUENE_BY_ORG_PARAMS, params);
        if (count != null && count.intValue() > 0) {// 表示已经存在
            return false;
        }
        return true;
    }

    @Override
    public List<OrgInfoDto> getDepts(OperatorDto dto) throws Exception {
        // 获取currentOrgInfo
        OrgInfoPo orgPo = this.getOrg(dto.getOperatorOrgId());
        Map<String, Object> params = new HashMap<>();
        params.put("topOrgId", orgPo.getTopOrgId());// 顶级机构id
        params.put(
            "orgPath",
            StringUtil.isNotNull(orgPo.getOrgPath()) ? orgPo.getOrgPath() + "," + orgPo.getId() : orgPo
                .getId() + "");// 这个不会出现同层级数据加载的问题。和数据库一起配合使用
        params.put("orgType", 1);// 部门级别的
        // 按照时间排序
        params.put(GlobalConstants.ORDER_BY_FIELD, "org_level,create_time");
        params.put(GlobalConstants.ORDER_BY, "desc");
        // 根据机构路径，查询当前登录人下级的部门层级。
        // TODO 需要加上当前层级
        List<OrgInfoPo> departments = baseDAO.findForList(MappersConstants.Org.SELECT_BY_PARAMS, params);
        return BeanCopyUtils.mapList(departments, OrgInfoDto.class);
    }

    @Override
    public void resetShopManagerPwd(Long orgId, Long topOrgId) throws ViewExternalDisplayException {

        LOG.info("orgId=" + orgId + ",topOrgId=" + topOrgId);

        OrgInfoPo queryOrgInfo = new OrgInfoPo();
        queryOrgInfo.setId(orgId);
        queryOrgInfo.setTopOrgId(topOrgId);
        // 设置机构类型为连锁分店
        queryOrgInfo.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_SHOP);
        // 查询分店id是否不是顶级机构下的分店
        OrgInfoPo orgInfo =
                baseDAO.findForObject(MappersConstants.Org.SELECT_ORG_BY_TOP_ORG_AND_ORG_ID_AND_ORG_TYPE,
                    queryOrgInfo);
        // 没有查询到该顶级机构下的分店
        if (orgInfo == null) {
            throw new ViewExternalDisplayException(ResultCode.Org.ORG_NOT_EXIST);
        }

        // 获取分店账号
        String orgAccount = orgInfo.getOrgAccount();

        // 查询管理员账号信息
        UserInfoPo userInfoPo =
                baseDAO.findForObject(MappersConstants.User.FIND_USER_BY_USER_NAME, orgAccount);

        // 账号不存在,系统错误
        if (userInfoPo == null) {
            throw new ViewExternalDisplayException(ResultCode.System.SYSTEM_ERROR);
        }

        // 加密密码
        String encryptionPwd = UserInfoService.staticEncryptionPwd("123456");

        UserInfoPo updateUser = new UserInfoPo();

        updateUser.setId(userInfoPo.getId());
        // 设置密码
        updateUser.setPassword(encryptionPwd);
        // 设置修改时间
        updateUser.setUpdateTime(System.currentTimeMillis());

        // 修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_BY_PRIMARYKEY_SELECTIVE, updateUser)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    @Transactional
    public void deleteShop(Long orgId) {
        // 查询分店信息
        OrgInfoPo org = this.getOrg(orgId);
        Long currentTime = System.currentTimeMillis();
        Map<String, Object> param = new HashMap<>();
        param.put("updateTime", currentTime);
        if (null != org && AssertUtils.notNull(org.getOrgAccount())) {
            // 删除管理员账号
            param.put("userName", org.getOrgAccount());
            this.baseDAO.update(MappersConstants.User.DELETE_BY_ACCOUNT, param);
        }
        param.put("id", orgId);
        // 删除该门店下所有的员工
        baseDAO.update(MappersConstants.Staff.DELETE_STAFF_BY_ORG, param);
        // 删除门店
        baseDAO.delete(MappersConstants.Org.DELETE_ORG, param);
    }

    /**
     * 获取org所有下属门店的id
     * 
     * @param orgId
     * @return
     */
    @Override
    public List<Long> getChildShopIds(Long orgId) {
        return baseDAO.findForList(MappersConstants.Org.GET_CHILD_SHOP_IDS, orgId);
    }

    @Override
    public List<DeptShopIdsAndCountDto> getShopIdsAndCountByDeptId(Long topOrgId, String orgPath)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("topOrgId", topOrgId);// 如果为null将会抛出异常给调用方
        params.put("orgPath", orgPath);// 如果是顶级id则这里可以为null
        return baseDAO.findForList(MappersConstants.Org.SELECT_SHOP_IDS_AND_COUNT_BY_DEPT_ID, params);
    }

    /**
     * 连锁门店查询
     */
    @Override
    public List<BranchOrgDto> branchOrgList(SelectBranchOrgDto dto) {
        List<BranchOrgDto> list = baseDAO.findForList(MappersConstants.Org.BRANCH_ORG_LIST, dto);
        return list;
    }

    /**
     * 连锁门店数量
     */
    @Override
    public Long branchOrgCount(SelectBranchOrgDto dto) {
        Long count = baseDAO.findForObject(MappersConstants.Org.BRANCH_ORG_COUNT, dto);
        return count;
    }

    /**
     * 连锁分店详情
     * 
     * @return
     */
    @Override
    public BranchOrgDetailDto getBranchOrgDetail(Long orgId) {
        BranchOrgDetailDto dto = baseDAO.findForObject(MappersConstants.Org.GET_BRANCH_ORG_DETAIL, orgId);

        if (dto != null) {
            dto.setTotalAddress(dto.getProvinceName() + dto.getCityName() + dto.getAreaName()
                    + dto.getAddress());
        }

        return dto;
    }

    @Override
    public PageBean<OrgInfoDto> selectOrgListByCondition(int pageIndex, int pageSize, Byte orgType,
            String orgFullName, String orgAccount, Long firstIndustryId, Long secondIndustryId,
            Long startExpireTime, Long endExpireTime, Long startCreateTime, Long endCreateTime,
            Long provinceId, Long cityId, Long areaId) throws ViewExternalDisplayException {

        LOG.info(pageIndex + ",pageSize=" + pageSize + ",orgType=" + orgType + ",orgFullName=" + orgFullName
                + ",orgAccount=" + orgAccount + ",firstIndustryId=" + firstIndustryId + ",twoIndustryId="
                + secondIndustryId + ",startExpireTime=" + startExpireTime + ",endExpireTime="
                + endExpireTime + ",startCreateTime=" + startCreateTime + ",endCreateTime=" + endCreateTime
                + ",provinceId=" + provinceId + ",cityId=" + cityId + ",areaId=" + areaId);

        // 模糊查询,防止正则
        if (StringUtils.isNotBlank(orgFullName)) {
            orgFullName = orgFullName.replaceAll("%", "\\\\%");
            orgFullName = orgFullName.replaceAll("_", "\\\\_");
        }
        // 模糊查询,防止正则
        if (StringUtils.isNotBlank(orgAccount)) {
            orgAccount = orgAccount.replaceAll("%", "\\\\%");
            orgAccount = orgAccount.replaceAll("_", "\\\\_");
        }

        // 判断并获取pageIndex
        pageIndex = PageUtils.getPageIndex(pageIndex, 1);
        // 判断并获取pageSize
        pageSize = PageUtils.getPageSize(pageSize, 20);

        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);

        Map<String, Object> params = new HashMap<String, Object>();

        // 机构全称
        params.put("orgType", orgType);
        // 机构全称
        params.put("orgFullName", orgFullName);
        // 机构账号
        params.put("orgAccount", orgAccount);
        // 一级行业id
        params.put("firstIndustryId", firstIndustryId);
        // 二级行业id
        params.put("secondIndustryId", secondIndustryId);
        // 开始过期时间
        params.put("startExpireTime", startExpireTime);
        // 结束过期时间
        params.put("endExpireTime", endExpireTime);
        // 开始创建时间
        params.put("startCreateTime", endCreateTime);
        // 结束创建时间
        params.put("endCreateTime", endCreateTime);
        // 省份id
        params.put("provinceId", provinceId);
        // 城市id
        params.put("cityId", cityId);
        // 地区id
        params.put("areaId", areaId);

        // 查询机构员工列表
        List<OrgInfoPo> datas =
                baseDAO.findForList(MappersConstants.Org.SELECT_ORG_LIST_BY_CONDITION, params);

        // 转换列表
        Page<OrgInfoPo> lists = (Page<OrgInfoPo>) datas;

        // 转换返回数据
        PageBean<OrgInfoDto> page =
                PageUtils.getPageBean(pageIndex, pageSize, new Long(lists.getTotal()).intValue(),
                    lists.getPages(), BeanCopyUtils.mapList(lists, OrgInfoDto.class));

        return page;
    }

    @Override
    public Long getCountShopByTopOrg(Long topOrgId) {

        LOG.info("topOrgId=" + topOrgId);

        Long count = baseDAO.findForObject(MappersConstants.Org.GET_COUNT_SHOP_BY_TOP_ORG, topOrgId);

        return count;
    }

    @Override
    public boolean deleteOrgsByTopOrgId(Long topOrgId) throws ViewExternalDisplayException {

        LOG.info("topOrgId=" + topOrgId);

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("topOrgId", topOrgId);
        params.put("updateTime", System.currentTimeMillis());

        // 删除机构
        if (0 < baseDAO.update(MappersConstants.Org.DELETE_ORGS_BY_TOP_ORG_ID, params)) {
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public void resetOrgManagerPwd(Long orgId, Byte orgType) throws ViewExternalDisplayException {

        LOG.info("orgId=" + orgId);

        OrgInfoPo queryOrgInfo = new OrgInfoPo();
        queryOrgInfo.setId(orgId);
        queryOrgInfo.setTopOrgId(orgId);
        // 设置机构类型
        queryOrgInfo.setOrgType(orgType);
        // 查询是不是连锁机构超管机构
        OrgInfoPo orgInfo =
                baseDAO.findForObject(MappersConstants.Org.SELECT_ORG_BY_TOP_ORG_AND_ORG_ID_AND_ORG_TYPE,
                    queryOrgInfo);
        // 没有查询到该顶级机构
        if (orgInfo == null) {
            throw new ViewExternalDisplayException(ResultCode.Org.ORG_NOT_EXIST);
        }

        // 获取分店账号
        String orgAccount = orgInfo.getOrgAccount();

        // 查询管理员账号信息
        UserInfoPo userInfoPo =
                baseDAO.findForObject(MappersConstants.User.FIND_USER_BY_USER_NAME, orgAccount);

        // 账号不存在,系统错误
        if (userInfoPo == null) {
            throw new ViewExternalDisplayException(ResultCode.System.SYSTEM_ERROR);
        }

        // 加密密码
        String encryptionPwd = UserInfoService.staticEncryptionPwd("123456");

        UserInfoPo updateUser = new UserInfoPo();

        updateUser.setId(userInfoPo.getId());
        // 设置密码
        updateUser.setPassword(encryptionPwd);
        // 设置修改时间
        updateUser.setUpdateTime(System.currentTimeMillis());

        // 修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_BY_PRIMARYKEY_SELECTIVE, updateUser)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }

    }

    /**
     * 新增顶级机构
     * 
     * @param dto
     */
    @Override
    @Transactional
    public AddTopOrgResultDto addTopOrg(AddTopOrgDto dto) {
        // 新增基本信息
        OrgInfoPo topOrgPo = new OrgInfoPo();
        topOrgPo.setOrgFullName(dto.getOrgFullName());
        topOrgPo.setOrgName(dto.getOrgName());
        topOrgPo.setParentOrgId(AccountTableConstants.Org.TOP_PARENT_ORG_ID);
        topOrgPo.setOrgType(dto.getOrgType());
        topOrgPo.setOrgLevel(AccountTableConstants.Org.TOP_LEVEL);
        topOrgPo.setOrgPath(AccountTableConstants.Org.TOP_ORG_PATH);
        topOrgPo.setCreateTime(System.currentTimeMillis());
        topOrgPo.setUpdateTime(topOrgPo.getCreateTime());
        topOrgPo.setIsForbid(AccountTableConstants.Org.NOT_FORBID);
        topOrgPo.setIsDelete(AccountTableConstants.NOT_DELETE);
        topOrgPo.setFirstIndustryId(dto.getFirstIndustryId());
        topOrgPo.setSecondIndustryId(dto.getSecondIndustryId());
        baseDAO.save(MappersConstants.Org.ADD_ORG, topOrgPo);
        topOrgPo.setTopOrgId(topOrgPo.getId());

        // 新增扩展信息
        OrgDetailInfoPo detailInfoPo = new OrgDetailInfoPo();
        detailInfoPo.setOrgId(topOrgPo.getTopOrgId());
        detailInfoPo.setProvinceId(dto.getProvinceId());
        detailInfoPo.setProvinceName(dto.getProvinceName());
        detailInfoPo.setCityId(dto.getCityId());
        detailInfoPo.setCityName(dto.getCityName());
        detailInfoPo.setAreaId(dto.getAreaId());
        detailInfoPo.setAreaName(dto.getAreaName());
        detailInfoPo.setAddress(dto.getAddress());
        detailInfoPo.setBusinessLicense(dto.getBusinessLicense());
        detailInfoPo.setCreateTime(topOrgPo.getCreateTime());
        detailInfoPo.setUpdateTime(topOrgPo.getCreateTime());
        detailInfoPo.setIsDelete(AccountTableConstants.NOT_DELETE);
        baseDAO.save(MappersConstants.OrgDetail.ADD_ORG_DETAIL, detailInfoPo);

        // 新增过期时间信息
        OrgExpireTimePo expireTimePo = new OrgExpireTimePo();
        expireTimePo.setExpireTime(dto.getExpireTime());
        expireTimePo.setCreateTime(topOrgPo.getCreateTime());
        expireTimePo.setUpdateTime(topOrgPo.getCreateTime());
        expireTimePo.setTopOrgId(topOrgPo.getTopOrgId());
        baseDAO.save(MappersConstants.OrgExpireTime.ADD, expireTimePo);

        // 如果是连锁机构, 默认新增三层层级
        if (dto.getOrgType().equals(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART)) {
            OrgInfoPo parent = topOrgPo;
            parent = addOrgByParentAndName(parent, "第一层级");
            parent = addOrgByParentAndName(parent, "第二层级");
            addOrgByParentAndName(parent, "第三层级");
        }
        // 新增超管账号

        String orgAccount;
        try {
            orgAccount = userInfoService.getCommercialTenantCode(topOrgPo.getId());
        }
        catch (Exception e) {
            throw new RuntimeException("好蛋疼的Exception");
        }
        Long postId = addSuperAdmin(topOrgPo, "超级管理员", orgAccount);

        // 设置顶级机构的顶级机构id
        OrgInfoPo setTopOrgPo = new OrgInfoPo();
        setTopOrgPo.setOrgAccount(orgAccount);
        setTopOrgPo.setId(topOrgPo.getId());
        setTopOrgPo.setTopOrgId(topOrgPo.getId());
        baseDAO.update(MappersConstants.Org.UPDATE_ORG, setTopOrgPo);

        AddTopOrgResultDto resultDto = new AddTopOrgResultDto();
        resultDto.setSuperAdminPostId(postId);
        return resultDto;
    }

    /**
     * 添加超级管理员
     * 
     * @return
     */
    private Long addSuperAdmin(OrgInfoPo topOrg, String name, String orgAccount) {
        // 新增超管用户
        UserInfoPo po = new UserInfoPo();
        po.setCreateTime(System.currentTimeMillis());
        po.setUpdateTime(po.getCreateTime());
        po.setUserType(AccountTableConstants.User.USER_TYPE_SPECIAL);
        po.setPassword(userInfoService.encryptionPwd(AccountTableConstants.User.DEFAULT_PASSWORD));
        po.setUserName(orgAccount);
        po.setName(name);
        baseDAO.save(MappersConstants.User.INSERT_SELECTIVE, po);

        // 新增超管的岗位
        OrgPostPo postPo = new OrgPostPo();
        postPo.setPostType(AccountTableConstants.Post.POST_TYPE_ADMIN);
        postPo.setPostName(AccountTableConstants.Post.SUPER_ADMIN);
        postPo.setOrgId(topOrg.getId());
        postPo.setTopOrgId(topOrg.getId());
        postPo.setCreateTime(System.currentTimeMillis());
        postPo.setUpdateTime(System.currentTimeMillis());
        baseDAO.save(MappersConstants.Post.INSERT_SELECTIVE, postPo);

        // 新增超管的商户账号
        OrgStaffPo staffPo = new OrgStaffPo();
        staffPo.setStaffName(name);
        staffPo.setOrgId(topOrg.getId());
        staffPo.setPostId(postPo.getId());
        staffPo.setUserId(po.getId());
        staffPo.setTopOrgId(topOrg.getId());
        staffPo.setCreateTime(System.currentTimeMillis());
        staffPo.setUpdateTime(System.currentTimeMillis());
        staffPo.setCreaterId(null);
        baseDAO.save(MappersConstants.Staff.INSERT_SELECTIVE, staffPo);
        return postPo.getId();
    }

    private OrgInfoPo addOrgByParentAndName(OrgInfoPo parent, String name) {
        OrgInfoPo po = new OrgInfoPo();
        po.setOrgName(name);
        po.setOrgFullName(name);
        po.setParentOrgId(parent.getId());
        po.setCreateTime(System.currentTimeMillis());
        po.setUpdateTime(po.getCreateTime());
        po.setOrgLevel((byte) (parent.getOrgLevel() + 1));
        po.setOrgType(AccountTableConstants.Org.ORG_TYPE_CHAIN_DEPART);
        po.setTopOrgId(parent.getTopOrgId());
        po.setOrgPath(parent.getOrgPath() + parent.getId() + ",");

        baseDAO.save(MappersConstants.Org.ADD_ORG, po);
        return po;
    }
}

/*
 * 文件名：MappersConstants.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年4月27日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account;

/**
 * mappers常量类
 * <p>
 * 定义所有mappers方法的常量
 * 
 * 
 * @author tanguojun
 * @since 2.2.4
 */
public interface MappersConstants {

    /**
     * 用户
     */
    interface User {
        /**
         * 根据id获取用户基本信息
         */
        String GET_USER_BY_ID = "UserInfoPoMapper.selectByPrimaryKey";

        /**
         * 根据手机号获取绑定用户
         */
        String GET_USER_BY_PHONE = "UserInfoPoMapper.getUserByPhone";

        /**
         * 根据用户名查询用户
         * 
         * @author tanguojun 2016年12月19日 11:31:07
         */
        String FIND_USER_BY_USER_NAME = "UserInfoPoMapper.findUserByUserName";

        /**
         * 插入实体对象
         *
         * @author laishaoqiang 2016年12月21日 11:31:07
         */
        String INSERT_SELECTIVE = "UserInfoPoMapper.insertSelective";

        /**
         * 根据userId查找user
         */
        String FIND_USER_BY_IDS = "UserInfoPoMapper.findUserByIds";

        /**
         * 根据id修改user
         */
        String UPDATE_BY_ID = "UserInfoPoMapper.updateByPrimaryKeySelective";

        /**
         * 修改用户的手机号码、激活状态
         *
         * @author tanguojun 2016年12月22日 15:19:40
         */
        String UPDATE_PHONE_AND_ACTIVE_BY_USER = "UserInfoPoMapper.updatePhoneAndActiveByUser";

        /**
         * 修改为删除状态(逻辑删除)
         * 
         * @author tanguojun 2016-12-22
         */
        String UPDATE_DELETE_STATUS = "UserInfoPoMapper.updateDeleteStatus";

        String GET_SHOP_MANAGER_USERINFO_BY_ORGID = "UserInfoPoMapper.getShopManagerUserInfoByOrgId";
        /**
         * 更新
         */
        String UPDATE_BY_PRIMARYKEY_SELECTIVE = "UserInfoPoMapper.updateByPrimaryKeySelective";

        /**
         * 根据账号删除用户
         */
        String DELETE_BY_ACCOUNT = "UserInfoPoMapper.deleteUserByAccount";

        /**
         * 按条件获取当前的全部企业用户
         */
        String GET_ORG_USERS_BY_CONDITIONS = "UserInfoPoMapper.getOrgUsersByConditions";

        /**
         * 获取机构用户数量
         */
        String GET_ORG_USRE_COUNT = "UserInfoPoMapper.getOrgUserCount";

        /**
         * 按条件获取当前企业用户的账户状态
         */
        String GET_FORBID_STATUS_BY_ID = "UserInfoPoMapper.getForbidStatusById";

        /**
         * 启用账户
         */
        String UPDATE_ENABLE_USER = "UserInfoPoMapper.updateEnableUser";

        /**
         * 禁用账户
         */
        String UPDATE_DISABLE_USER = "UserInfoPoMapper.updateDisAbleUser";
        /**
         * 解绑手机
         */
        String UNBOUND_MOBILE = "UserInfoPoMapper.unboundMobile";

        /**
         * 重新绑定手机
         */
        String REBOUND_MOBILE = "UserInfoPoMapper.reboundMobile";
        
        String SELECT_ALL_EMPLOYEES_COUNT = "UserInfoPoMapper.selectAllEmployeesCount";
    }

    /**
     * 机构
     */
    interface Org {
        /**
         * 查询所有非直属部门
         */
        String GET_ALL_PARENT_ORG = "OrgInfoPoMapper.getAllParentOrg";

        /**
         * 查询直属部门
         */
        String GET_DIRECT_ORG = "OrgInfoPoMapper.getDirectOrg";

        /**
         * 获取门店列表
         */
        String GET_SHOP_LIST = "OrgInfoPoMapper.getShopList";

        /**
         * 获取org
         */
        String GET_ORG = "OrgInfoPoMapper.selectByPrimaryKey";

        /**
         * 添加org
         */
        String ADD_ORG = "OrgInfoPoMapper.insertSelective";

        /**
         * 根据topOrg和名称查询部门,用来做名称唯一性校验
         */
        String SELECT_ID_BY_TOP_ORG_AND_NAME = "OrgInfoPoMapper.selectIdByTopOrgAndNameAndOrgType";

        /**
         * 修改org
         */
        String UPDATE_ORG = "OrgInfoPoMapper.updateByPrimaryKeySelective";

        /**
         * 删除org
         */
        String DELETE_ORG = "OrgInfoPoMapper.deleteByPrimaryKey";

        /**
         * 根据topOrgId查找org
         */
        String SELECT_BY_TOP_ORG_ID = "OrgInfoPoMapper.selectByTopOrgId";

        /**
         * 获取一个部门的下属部门类型,用来决定该部门能否新增部门或者门店
         */

        String GET_ORG_CHILD_ORG_TYPE = "OrgInfoPoMapper.getOrgChildOrgType";

        /**
         * 根据id查询机构
         */
        String GET_ORG_BY_ID = "OrgInfoPoMapper.getOrgById";

        /**
         * 员工id获取机构实体
         */
        String GET_ORG_BY_STAFFID = "OrgInfoPoMapper.getOrgByStaffId";

        /**
         * 获取机构的管理员
         */
        String GET_ADMIN_BY_ORG = "OrgInfoPoMapper.getAdminByOrg";

        /**
         * 获取门店的员工数量
         */
        String GET_STAFF_COUNT_BY_ORG = "OrgInfoPoMapper.getStaffCountByOrg";

        /**
         * 获取当前连锁体系下其他门店
         */
        String GET_OTHER_ORG = "OrgInfoPoMapper.getOtherOrg";

        /**
         * 查找连锁机构下所有门店
         */
        String SELECT_ALL_SHOP = "OrgInfoPoMapper.selectShopByTopOrg";

        /**
         * 更新实体
         */
        String UPDATE_BY_PRIMARYKEY_SELECTIVE = "OrgInfoPoMapper.updateByPrimaryKeySelective";

        /**
         * 根据topId和id、机构类型查询机构是否存在
         *
         * @author tanguojun 2016-12-26
         */
        String IS_EXIST_ORG_BY_TOP_ID_AND_ID = "OrgInfoPoMapper.isExistOrgByTopIdAndId";

        /**
         * 根据顶级id&机构类型查询机构列表
         * 
         * @author tanguojun 2016-12-26
         */
        String SELECT_LIST_BY_TOP_ID_AND_ORG_TYPE = "OrgInfoPoMapper.selectListByTopIdAndOrgType";

        /**
         * 根据顶级机构和门店的名称查找连锁店
         */
        String GET_SHOP_BY_TOP_ORG_AND_NAME = "OrgInfoPoMapper.getShopByTopOrgAndName";

        /*
         * 根据orgName查询是否系统唯一
         */
        String UNIQUENE_BY_ORG_PARAMS = "OrgInfoPoMapper.uniqueneByOrgParams";

        /**
         * 根据map查询组织
         */
        String SELECT_BY_PARAMS = "OrgInfoPoMapper.selectByParams";

        /**
         * 根据顶级机构id、机构id和机构类型查询机构信息
         * 
         * @author tanguojun 2017-2-20
         */
        String SELECT_ORG_BY_TOP_ORG_AND_ORG_ID_AND_ORG_TYPE =
                "OrgInfoPoMapper.selectOrgByTopOrgAndOrgIdAndOrgType";

        /**
         * 根据机构id查询所有下属门店的id
         */
        String GET_CHILD_SHOP_IDS = "OrgInfoPoMapper.getChildShopIds";

        String SELECT_SHOP_IDS_AND_COUNT_BY_DEPT_ID = "OrgInfoPoMapper.selectShopIdsAndCountByDeptId";

        String BRANCH_ORG_LIST = "OrgInfoPoMapper.branchOrgList";

        String BRANCH_ORG_COUNT = "OrgInfoPoMapper.branchOrgCount";

        String GET_BRANCH_ORG_DETAIL = "OrgInfoPoMapper.getBranchOrgDetail";

        /**
         * 分页条件查询机构列表
         * 
         * @author tanguojun 2017-3-2
         */
        String SELECT_ORG_LIST_BY_CONDITION = "OrgInfoPoMapper.selectOrgListByCondition";

        /**
         * 获取机构分店总数
         * 
         * @author tanguojun 2017-3-2
         */
        String GET_COUNT_SHOP_BY_TOP_ORG = "OrgInfoPoMapper.getCountShopByTopOrg";

        /**
         * 删除机构
         * 
         * @author tanguojun 2017-3-6
         */
        String DELETE_ORGS_BY_TOP_ORG_ID = "OrgInfoPoMapper.deleteOrgsByTopOrgId";
    }

    /**
     * 岗位
     *
     * @author 张秋平 2016-12-19
     *
     */
    interface Post {
        /** 机构岗位分页查询 */
        String GET_ORG_POST_PAGE = "OrgPostPoMapper.getOrgPostPage";

        /** 机构岗位总数 */
        String GET_ORG_POST_PAGE_COUNT = "OrgPostPoMapper.getOrgPostPageCount";

        /** 查询机构岗位信息 */
        String GET_ORG_POST_LIST_BY_POST = "OrgPostPoMapper.getOrgPostListByPost";

        /** 增加机构岗位信息 */
        String ADD_ORG_POST = "OrgPostPoMapper.addOrgPost";

        /** 更新机构岗位信息 */
        String UPDATE_ORG_POST = "OrgPostPoMapper.updateOrgPost";

        /** 删除机构岗位信息 */
        String DELETE_ORG_POST = "OrgPostPoMapper.deleteOrgPost";

        /** 员工id获取岗位实体 */
        String GET_STAFF_POST_BY_STAFFID = "OrgPostPoMapper.getStaffPostByStaffId";

        /** 更新岗位实体 */
        String UPDATE_BY_PRIMARYKEY_SELECTIVE = "OrgPostPoMapper.updateByPrimaryKeySelective";

        /** 插入实体对象 */
        String INSERT_SELECTIVE = "OrgPostPoMapper.insertSelective";

        String GET_BY_ID = "OrgPostPoMapper.selectByPrimaryKey";

        /** 查询门店下所有岗位 张秋平 2016-12-22 */
        String GET_ORG_ALL_POST = "OrgPostPoMapper.getOrgAllPost";

        /**
         * 修改为删除状态(逻辑删除)
         * 
         * @author tanguojun 2016-12-22
         */
        String UPDATE_DELETE_STATUS = "OrgPostPoMapper.updateDeleteStatus";

        /**
         * 根据门店id和岗位类型查岗位
         */
        String SELECT_BY_ORG_AND_POST_TYPE = "OrgPostPoMapper.selectByOrgAndPostType";

        /** 查询岗位列表--为调岗实现(如果店长已关联店员，则调岗时不显示) 张秋平 2016-12-30 11:15 */
        String getAllOrgPostForAdjustPost = "OrgPostPoMapper.getAllOrgPostForAdjustPost";
    }

    /**
     * 员工
     *
     * @author tanguojun on 2016年12月19日
     *
     */
    interface Staff {

        /**
         * 根据用户id查询商户账号
         *
         * @author tanguojun 2016-12-19
         */
        String GET_STAFF_BY_USERID = "OrgStaffPoMapper.getStaffByUserId";

        /** 更新员工实体 */
        String UPDATE_BY_PRIMARYKEY_SELECTIVE = "OrgStaffPoMapper.updateByPrimaryKeySelective";

        String SELECT_BY_TOP_ORG_ID = "OrgStaffPoMapper.selectByTopOrgId";

        /** 插入实体对象 */
        String INSERT_SELECTIVE = "OrgStaffPoMapper.insertSelective";
        /** 根据主键查询 */
        String SELECT_BY_PRIMARYKEY = "OrgStaffPoMapper.selectByPrimaryKey";

        /***
         * 获取店员列表
         */
        String GET_STAFF_LIST = "OrgStaffPoMapper.getShopStaffList";

        /**
         * 设置员工岗位为空
         */
        String SET_POST_NULL = "OrgStaffPoMapper.setPostNull";

        /**
         * 修改为删除状态(逻辑删除)
         *
         * @author tanguojun 2016-12-22
         */
        String UPDATE_DELETE_STATUS = "OrgStaffPoMapper.updateDeleteStatus";

        /** 门店店员分页查询 张秋平 2016-12-23 */
        String SELECT_ORG_CLERK_LIST_FOR_PAGE = "OrgStaffPoMapper.selectOrgClerkListForPage";

        /** 门店店员分页查询 张秋平 2016-12-23 */
        String SELECT_ORG_CLERK_COUNT__FOR_PAGE = "OrgStaffPoMapper.selectOrgClerkCountForPage";
        /**
         * 查找层级下是否存在员工
         */
        String SELECT_ONE_STAFF_BY_ORG_ID = "OrgStaffPoMapper.selectOneStaffByOrgId";

        /**
         * 分页查询机构员工列表
         *
         * @author tanguojun 2016-12-26
         */
        String SELECT_ORG_STAFF_LIST = "OrgStaffPoMapper.selectOrgStaffList";

        /**
         * 根据orgId和postId查找店员
         */
        String SELECT_BY_ORG_AND_POST = "OrgStaffPoMapper.selectByOrgAndPost";

        /** 根据机构id查询关联的员工列表 */
        String GET_ORG_STAFF_PO_LIST_BY_POST_ID = "OrgStaffPoMapper.getOrgStaffPoListByPostId";

        /**
         * 获取用户的最近登录时间
         */
        String GET_LAST_LOGIN_TIME = "OrgStaffPoMapper.getLastLoginTime";

        /**
         * 获取员工的基本信息
         */
        String GET_BASIC_STAFF_INFO = "OrgStaffPoMapper.getBasicStaffInfo";

        /**
         * 删除某机构下的店员
         */
        String DELETE_STAFF_BY_ORG = "OrgStaffPoMapper.deleteStaffByOrg";

        /**
         * 获取门店店长id
         */
        String GET_CLERK_USER = "OrgStaffPoMapper.getClerkUserId";

        /**
         * 获取分店或者单店的员工[店长+员工]
         */
        String LOAD_EMPLOYEES_BY_ORG_ID = "OrgStaffPoMapper.loadEmployeesByOrgId";

        /**
         * 获取机构员工总数,不包括管理员
         * 
         * @author tanguojun 2017-3-2
         */
        String GET_COUNT_STAFF_BY_TOP_ORG = "OrgStaffPoMapper.getCountStaffByTopOrg";
    }

    /**
     * 机构详细信息
     */
    interface OrgDetail {

        /**
         * 新增机构详情信息
         */
        String ADD_ORG_DETAIL = "OrgDetailInfoPoMapper.insertSelective";
        /**
         * 查询门店详细信息
         */
        String GET_ORG_DETAIL_INFO = "OrgDetailInfoPoMapper.selectByPrimaryKey";
        /**
         * 根据机构id查询机构详情
         */
        String GET_ORG_DETAIL_BY_ORG_ID = "OrgDetailInfoPoMapper.getOrgDetailByOrgId";

        /**
         * 更新实体对象
         */
        String UPDATE_BY_PARAM = "OrgDetailInfoPoMapper.updateByParam";

        /**
         * 更新对象
         */
        String UPDATE_BY_PRIMARYKEY_SELECTIVE = "OrgDetailInfoPoMapper.updateByPrimaryKeySelective";

        /**
         * 获取机构地址信息
         */
        String GET_ORG_ADDRESS = "OrgDetailInfoPoMapper.getOrgAddress";

    }

    /**
     * 
     * @ClassName: OrgExpireTime
     * @Description: 机构超时时间mapper
     * @author li.jinwen
     * @email lijinwen@youanmi.com
     * @date 2017年2月10日 上午11:36:53
     *
     */
    interface OrgExpireTime {
        /**
         * 根据组织ID获取机构超时信息
         */
        String GET_ORG_EXPIRE_TIME_BY_ORG_ID = "OrgExpireTimePoMapper.selectByPrimaryKey";
        String ADD = "OrgExpireTimePoMapper.insertSelective";
    }

    interface MemberInfo {
        /**
         * 命名空间
         */
        String NAMESPACE = "MemberInfoPoMapper";
        /**
         * 按照要求获取memberInfo
         */
        String GET_MEMBER_INFO_BY_CONDITIONS = NAMESPACE + ".selectByParams";
        /**
         * 按照主键获取会员用户
         */
        String GET_MEMBER_INFO_BY_ID = NAMESPACE + ".selectByPrimaryKey";
        /**
         * 获取生日会员
         */
        String GET_PAGE_BY_BIRTHDDAY = NAMESPACE + ".getPageByBirthday";
        /**
         * 查询微信会员
         */
        String GET_BY_WX_NO = NAMESPACE + ".getByWxNo";
        /**
         * 插入
         */
        String INSERT = NAMESPACE + ".insert";
        /**
         * 根具用户名查找
         */
        String GET_BY_USERNAME = NAMESPACE + ".getByUserName";
    }
}

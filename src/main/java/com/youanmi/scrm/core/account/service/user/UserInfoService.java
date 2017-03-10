/*
 * 文件名：UserInfoService.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月16日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.service.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.helper.ValidateViewExceptionUtils;
import com.youanmi.commons.base.utils.StringUtil;
import com.youanmi.scrm.api.account.constants.AccountTableConstants;
import com.youanmi.scrm.api.account.dto.org.ChangePasswordDto;
import com.youanmi.scrm.api.account.dto.user.UserInfoDto;
import com.youanmi.scrm.api.account.service.user.IUserInfoService;
import com.youanmi.scrm.commons.constants.result.ResultCode;
import com.youanmi.scrm.commons.util.encryption.MD5Utils;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.commons.util.string.AssertUtils;
import com.youanmi.scrm.commons.util.string.MatcherUtils;
import com.youanmi.scrm.core.account.AccountConstants;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.user.UserInfoPo;


/**
 * 用户信息service
 * 
 * @author tanguojun 2016年12月16日
 * @version 1.0.0
 */
public class UserInfoService implements IUserInfoService {
    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private BaseDAO baseDAO;

    /**
     * 根据用户id获取用户基本信息
     * 
     * @param id 用户id
     */
    @Override
    public UserInfoDto getUserById(Long id) {
        UserInfoPo userInfo = baseDAO.findForObject(MappersConstants.User.GET_USER_BY_ID, id);
        // 用户为空
        if (userInfo == null) {
            return null;
        }
        return BeanCopyUtils.map(userInfo, UserInfoDto.class);
    }

    /**
     * 根据手机号获取绑定用户
     * 
     * @param phone 手机号
     */
    @Override
    public UserInfoDto getUserByPhone(String phone) {
        UserInfoPo userInfo = baseDAO.findForObject(MappersConstants.User.GET_USER_BY_PHONE, phone);
        // 用户为空
        if (userInfo == null) {
            return null;
        }
        return BeanCopyUtils.map(userInfo, UserInfoDto.class);
    }

    @Override
    public UserInfoDto findUserByUserNameOrPhone(String mobilePhone, String userName) {

        LOG.info("mobilePhone=" + mobilePhone + ",userName=" + userName);

        UserInfoPo userInfoPo = null;

        // 如果用户名不为空,则使用用户名查询用户
        if (StringUtils.isNoneBlank(userName)) {
            userInfoPo = baseDAO.findForObject(MappersConstants.User.FIND_USER_BY_USER_NAME, userName);
        }
        // 否则使用手机号查询
        else if (StringUtils.isNoneBlank(mobilePhone)) {
            userInfoPo = baseDAO.findForObject(MappersConstants.User.GET_USER_BY_PHONE, mobilePhone);
        }

        // 用户没找到,返回用户不存在
        if (userInfoPo == null) {
            return null;
        }

        // 返回dto实体
        return BeanCopyUtils.map(userInfoPo, UserInfoDto.class);
    }

    @Override
    public String encryptionPwd(String password) {

        LOG.info("password=" + password);

        return staticEncryptionPwd(password);
    }

    /**
     * 静态加密字符串
     *
     * @param password
     * @return
     * @author tanguojun on 2017年2月20日
     */
    public static String staticEncryptionPwd(String password) {
        // 密码加盐
        String saltPwd = AccountConstants.User.PWD_SALT + password;

        // 密码md5两次,转换为大写
        String md5Pwd = MD5Utils.getMD5Code(MD5Utils.getMD5Code(saltPwd)).toUpperCase();

        return md5Pwd;
    }

    /**
     * 增加用户,返回对象id
     */
    @Override
    public Long insertSelective(UserInfoDto userInfoDto) {
        UserInfoPo po = BeanCopyUtils.map(userInfoDto, UserInfoPo.class);
        baseDAO.save(MappersConstants.User.INSERT_SELECTIVE, po);
        Long id = po.getId();
        return id;
    }

    /**
     * 校验密码是否符合规范 校验不通过直接抛异常
     */
    private void validatePassword(String password) {
        if (!MatcherUtils.matcherString(password, "^[0-9a-zA-Z]+$")) {
            ValidateViewExceptionUtils.throwIllegalOperationException("密码只能为数字和字母");
        }
        if (password.length() < AccountTableConstants.User.PASSWORD_MIN_LENGTH
                || password.length() > AccountTableConstants.User.PASSWORD_MAX_LENGTH) {
            ValidateViewExceptionUtils.throwIllegalOperationException("密码长度太短或超出范围");
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDto param) {
        String oldPasswordEncrypt = encryptionPwd(param.getOldPassword());
        UserInfoDto userInfoDto = getUserById(param.getUserId());
        if (!userInfoDto.getPassword().equals(oldPasswordEncrypt)) {
            throw new ViewExternalDisplayException(ResultCode.User.PASSWORD_ERROR);
        }
        validatePassword(param.getNewPassword());
        UserInfoPo po = new UserInfoPo();
        po.setId(param.getUserId());
        po.setPassword(encryptionPwd(param.getNewPassword()));
        baseDAO.update(MappersConstants.User.UPDATE_BY_ID, po);
        // 如果旧密码校验通过,则允许

    }

    @Override
    public void updatePhoneAndActiveByUser(Long userId, String mobilePhone) {

        LOG.info("userId=" + userId + ",mobilePhone=" + mobilePhone);

        UserInfoPo userInfoPo = new UserInfoPo();
        // 设置用户id
        userInfoPo.setId(userId);
        // 设置修改时间
        userInfoPo.setUpdateTime(System.currentTimeMillis());
        // 设置手机号
        userInfoPo.setMobilePhone(mobilePhone);
        // 设置为激活状态
        userInfoPo.setIsActive(AccountTableConstants.User.ACTIVITE);

        // 修改
        int rows = baseDAO.update(MappersConstants.User.UPDATE_PHONE_AND_ACTIVE_BY_USER, userInfoPo);

        if (rows < 1) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    public void resetUserPassword(String mobilePhone, String newPassword) throws ViewExternalDisplayException {

        LOG.info("mobilePhone=" + mobilePhone + ",newPassword=" + newPassword);

        // 校验密码
        validatePassword(newPassword);

        // 根据手机号查询用户
        UserInfoDto userInfoDto = getUserByPhone(mobilePhone);

        // 用户不存在
        if (userInfoDto == null) {
            throw new ViewExternalDisplayException(ResultCode.User.ACCOUNT_NOT_EXIST);
        }

        // 加密密码
        String encryptionPwd = encryptionPwd(newPassword);

        UserInfoPo userInfoPo = new UserInfoPo();
        // 设置用户id
        userInfoPo.setId(userInfoDto.getId());
        // 设置加密后的密码
        userInfoPo.setPassword(encryptionPwd);
        // 设置修改时间
        userInfoPo.setUpdateTime(System.currentTimeMillis());

        // 小于1,说明修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_BY_ID, userInfoPo)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    public UserInfoDto getShopManagerUserInfoByOrgId(Long OrgId) throws ViewExternalDisplayException {
        if (AssertUtils.isNull(OrgId)) {
            LOG.info("UserInfoService-getShopManagerUserInfoByOrgId-入参错误");
            throw new ViewExternalDisplayException(ResultCode.System.PARAMETER_NOT_NULL, "OrgId");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", OrgId);
        UserInfoPo po =
                baseDAO.findForObject(MappersConstants.User.GET_SHOP_MANAGER_USERINFO_BY_ORGID, params);
        return BeanCopyUtils.map(po, UserInfoDto.class);
    }

    /**
     * 更新实体
     */
    @Override
    public void updateByPrimaryKeySelective(UserInfoDto userInfoDto) {
        UserInfoPo po = BeanCopyUtils.map(userInfoDto, UserInfoPo.class);
        baseDAO.update(MappersConstants.User.UPDATE_BY_PRIMARYKEY_SELECTIVE, po);
    }

    @Override
    public void updateUserActiveStatus(Long userId) throws ViewExternalDisplayException {

        UserInfoPo updateUser = new UserInfoPo();
        // 设置用户id
        updateUser.setId(userId);
        // 设置修改时间
        updateUser.setUpdateTime(System.currentTimeMillis());
        // 设置为已激活状态
        updateUser.setIsActive(AccountTableConstants.User.ACTIVITE);

        // 小于1,说明修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_BY_ID, updateUser)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserPhoneToOtherUser(Long oldUserId, Long newUserId, String mobilePhone)
            throws ViewExternalDisplayException {

        Long currentTime = System.currentTimeMillis();

        UserInfoPo updateOldUser = new UserInfoPo();
        // 设置用户id
        updateOldUser.setId(oldUserId);
        // 设置手机号为null
        updateOldUser.setMobilePhone(null);
        // 设置修改时间
        updateOldUser.setUpdateTime(currentTime);
        // 设置为激活状态
        updateOldUser.setIsActive(AccountTableConstants.User.NO_ACTIVITE);

        // 小于1,说明修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_PHONE_AND_ACTIVE_BY_USER, updateOldUser)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }

        UserInfoPo updateNewUser = new UserInfoPo();
        // 设置用户id
        updateNewUser.setId(newUserId);
        // 设置手机号
        updateNewUser.setMobilePhone(mobilePhone);
        // 设置修改时间
        updateNewUser.setUpdateTime(currentTime);
        // 设置为激活状态
        updateNewUser.setIsActive(AccountTableConstants.User.ACTIVITE);

        // 小于1,说明修改失败
        if (1 > baseDAO.update(MappersConstants.User.UPDATE_PHONE_AND_ACTIVE_BY_USER, updateNewUser)) {
            throw new ViewExternalDisplayException(ResultCode.System.OPERATION_FAIL);
        }
    }

    @Override
    @Transactional
    public List<UserInfoDto> getOrgUserListByConditions(Map<String, Object> conditions) throws ParseException {
        List<UserInfoPo> userInfoList  = baseDAO.findForList(MappersConstants.User.GET_ORG_USERS_BY_CONDITIONS,
                conditions);
        return BeanCopyUtils.mapList(userInfoList, UserInfoDto.class);
    }

    @Override
    @Transactional
    public void unboundMobile(Map<String, Object> conditions) {
        Integer unbound = (Integer)conditions.get("unbound");
        Long id = (Long)conditions.get("id");
        if (id == null || id <= 0L) {
            LOG.error("用户id获取失败！");
            return;
        }

        changeForbidStatus(conditions);

        //unbound为1时表示要解绑手机号，
        if (unbound.equals(1)) {
            try {
                baseDAO.update(MappersConstants.User.UNBOUND_MOBILE, conditions);
            } catch (Exception e) {
                LOG.error("解绑手机失败。原因：" + e);
                return;
            }
        } else {
            LOG.error(unbound + "不是合法的解绑手机参数！");
        }

    }

    @Override
    @Transactional
    public void reboundMobile(Map<String, Object> conditions) {
        Long id = (Long)conditions.get("id");
        if (id != null || id <= 0L) {
            LOG.error("用户id获取失败！");
            return;
        }

        changeForbidStatus(conditions);

        try {
            baseDAO.update(MappersConstants.User.REBOUND_MOBILE, conditions);
        } catch (Exception e) {
            LOG.error("重新绑定或者更改用户状态时失败！原因：" + e);
        }
    }

    private void changeForbidStatus(Map<String, Object> conditions) {
        Long id = (Long) conditions.get("id");
        Integer isForbid = (Integer)conditions.get("isForbid");
        if (isForbid.equals(1)) {
            baseDAO.update(MappersConstants.User.UPDATE_ENABLE_USER, id);
        } else if (isForbid.equals(2)) {
            baseDAO.update(MappersConstants.User.UPDATE_DISABLE_USER, id);
        } else {
            LOG.error("不是合法的禁用/启用参数！");
        }
    }

	@Override
	public String getCommercialTenantCode(Long topOrgId) throws Exception{
		if(AssertUtils.isNull(topOrgId)){
			//抛出检查性异常
			throw new Exception("topOrgId不能为null或者为0");
		}
		//查找顶级机构内所有的员工
		Integer dbCount = baseDAO.findForObject(MappersConstants.User.SELECT_ALL_EMPLOYEES_COUNT, topOrgId);
		//+1
		if(dbCount == null){//容错处理
			LOG.error("UserInfoService-getCommercialTenantCode-查询用户数量为null");
			dbCount = 0;
		}
		int count = dbCount.intValue()+1;
		return topOrgId + StringUtil.format(count, 5);
	}

    @Override
    public Integer getOrgUserCount(Map<String, Object> conditions) {
        return baseDAO.findForObject(MappersConstants.User.GET_ORG_USRE_COUNT, conditions);
    }
}

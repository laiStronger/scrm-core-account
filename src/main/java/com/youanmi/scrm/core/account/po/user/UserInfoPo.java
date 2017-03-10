package com.youanmi.scrm.core.account.po.user;

import java.io.Serializable;


public class UserInfoPo implements Serializable {
    private Long id;

    private String userName;

    private String mobilePhone;

    private String password;

    private Byte userType;

    private Long createTime;

    private Long updateTime;

    private Byte isDelete;

    /**
     * 性别 (1.男 2.女)
     */
    private Byte gender;

    /**
     *
     */
    private Long birthday;

    /**
     * 是否禁用(1:未禁用,2:已禁用)
     */
    private Byte isForbid;

    /**
     * 激活状态(1:未激活,2:已激活)(绑定了手机号就是激活状态,解绑了就是未激活)
     */
    private Byte isActive;

    private String name;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 头像缩略图
     */
    private String thumHeadUrl;

    // 是否使用(1:未使用,2:已使用)(登录过系统,则为使用过)
    private Byte isUse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone == null ? null : mobilePhone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Byte getUserType() {
        return userType;
    }

    public void setUserType(Byte userType) {
        this.userType = userType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Byte getIsForbid() {
        return isForbid;
    }

    public void setIsForbid(Byte isForbid) {
        this.isForbid = isForbid;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getThumHeadUrl() {
        return thumHeadUrl;
    }

    public void setThumHeadUrl(String thumHeadUrl) {
        this.thumHeadUrl = thumHeadUrl;
    }

    public Byte getIsUse() {
        return isUse;
    }

    public void setIsUse(Byte isUse) {
        this.isUse = isUse;
    }
    
    
}
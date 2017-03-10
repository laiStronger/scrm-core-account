package com.youanmi.scrm.core.account.po.org;

import java.io.Serializable;


public class OrgInfoPo implements Serializable {
    private Long id;

    private Long parentOrgId;

    private Long topOrgId;

    private String orgFullName;

    private String orgName;

    private Byte orgType;

    private Byte orgLevel;

    private String orgPath;

    private Long createTime;

    private Long updateTime;

    private Byte isForbid;

    private Byte isDelete;

    // 顶级机构信息
    private OrgInfoPo topOrgInfo;

    // 父机构名称
    private String parentOrgName;

    // 地址
    private String address;

    // logo机构logo
    private String logo;

    // thum_logo机构缩略logo
    private String thumLogo;

    // first_industry_id一级行业id
    private Long firstIndustryId;

    // second_industry_id二级行业id
    private Long secondIndustryId;

    // org_account机构账号
    private String orgAccount;

    // 机构到期时间
    private OrgExpireTimePo orgExpireTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public Long getTopOrgId() {
        return topOrgId;
    }

    public void setTopOrgId(Long topOrgId) {
        this.topOrgId = topOrgId;
    }

    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName == null ? null : orgFullName.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    public Byte getOrgType() {
        return orgType;
    }

    public void setOrgType(Byte orgType) {
        this.orgType = orgType;
    }

    public Byte getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Byte orgLevel) {
        this.orgLevel = orgLevel;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath == null ? null : orgPath.trim();
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

    public Byte getIsForbid() {
        return isForbid;
    }

    public void setIsForbid(Byte isForbid) {
        this.isForbid = isForbid;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public OrgInfoPo getTopOrgInfo() {
        return topOrgInfo;
    }

    public void setTopOrgInfo(OrgInfoPo topOrgInfo) {
        this.topOrgInfo = topOrgInfo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getThumLogo() {
        return thumLogo;
    }

    public void setThumLogo(String thumLogo) {
        this.thumLogo = thumLogo;
    }

    public Long getFirstIndustryId() {
        return firstIndustryId;
    }

    public void setFirstIndustryId(Long firstIndustryId) {
        this.firstIndustryId = firstIndustryId;
    }

    public Long getSecondIndustryId() {
        return secondIndustryId;
    }

    public void setSecondIndustryId(Long secondIndustryId) {
        this.secondIndustryId = secondIndustryId;
    }

    public String getOrgAccount() {
        return orgAccount;
    }

    public void setOrgAccount(String orgAccount) {
        this.orgAccount = orgAccount;
    }

    public OrgExpireTimePo getOrgExpireTime() {
        return orgExpireTime;
    }

    public void setOrgExpireTime(OrgExpireTimePo orgExpireTime) {
        this.orgExpireTime = orgExpireTime;
    }

}
package com.youanmi.scrm.core.account.po.org;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/1.
 */
public class OrgCustomeRelationPo implements Serializable {
    private Long id;

    private Long userId;

    private Long memberId;

    private Long orgId;

    private Long topOrgId;

    private String remarkName;

    private String mobile;

    private Long birthday;

    private Byte isSpecialAttention;

    private Long createTime;

    private Long updateTime;

    private Byte isDelete;

    private Long attentionTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getTopOrgId() {
        return topOrgId;
    }

    public void setTopOrgId(Long topOrgId) {
        this.topOrgId = topOrgId;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName == null ? null : remarkName.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Byte getIsSpecialAttention() {
        return isSpecialAttention;
    }

    public void setIsSpecialAttention(Byte isSpecialAttention) {
        this.isSpecialAttention = isSpecialAttention;
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

    public Long getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(Long attentionTime) {
        this.attentionTime = attentionTime;
    }
}

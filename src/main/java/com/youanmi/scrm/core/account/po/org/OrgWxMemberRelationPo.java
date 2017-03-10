package com.youanmi.scrm.core.account.po.org;

/**
 * Created by Administrator on 2017/2/24.
 */
public class OrgWxMemberRelationPo {
    private Long id;

    private Long orgWxId;

    private Long memberId;

    private Long orgId;

    private Long topOrgId;

    private String remark;

    private Long createTime;

    private Long updateTime;

    private Byte isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgWxId() {
        return orgWxId;
    }

    public void setOrgWxId(Long orgWxId) {
        this.orgWxId = orgWxId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    @Override
    public String toString() {
        return "OrgWxMemberRelationDto{" +
                "id=" + id +
                ", orgWxId=" + orgWxId +
                ", memberId=" + memberId +
                ", orgId=" + orgId +
                ", topOrgId=" + topOrgId +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                '}';
    }
}

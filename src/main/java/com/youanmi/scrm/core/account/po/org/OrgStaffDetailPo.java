package com.youanmi.scrm.core.account.po.org;

/**
 * 添加类的一句话简单描述。
 * <p>
 * 详细描述
 *
 * @author Administrator on 2016/12/22
 * @since ${version}
 */
public class OrgStaffDetailPo {

    private Long id; //店员id

    private Long orgId; //门店id

    private String mobilePhone; //手机号

    private String orgName; //机构名称

    private String postName; //岗位名称

    private String userName; //账号

    private String staffName; //店员名称

    private Long userId; //用户id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

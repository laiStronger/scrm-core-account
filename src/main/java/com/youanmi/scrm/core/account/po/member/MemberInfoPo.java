package com.youanmi.scrm.core.account.po.member;

import java.io.Serializable;


/**
 * 会员表
 * 
 * @author tanguojun on 2017年2月18日
 *
 */
public class MemberInfoPo implements Serializable {

    // id
    private Long id;

    // 用户名(系统生成的)
    private String userName;

    // 性别(1.男 2.女)(微信上的)
    private Byte gender;

    // 出生日期(微信上的)
    private Long birthday;

    // 地区(微信上的)
    private String area;

    // 头像(微信上的)
    private String headUrl;

    // 缩略头像(微信上的)
    private String thumHeadUrl;

    // 微信号
    private String wxNo;

    // 微信名
    private String wxName;

    // 创建时间
    private Long createTime;

    // 修改时间
    private Long updateTime;

    // 是否删除 (1.否 2.是)
    private Byte isDelete;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl == null ? null : headUrl.trim();
    }

    public String getThumHeadUrl() {
        return thumHeadUrl;
    }

    public void setThumHeadUrl(String thumHeadUrl) {
        this.thumHeadUrl = thumHeadUrl;
    }

    public String getWxNo() {
        return wxNo;
    }

    public void setWxNo(String wxNo) {
        this.wxNo = wxNo == null ? null : wxNo.trim();
    }

    public String getWxName() {
        return wxName;
    }

    public void setWxName(String wxName) {
        this.wxName = wxName == null ? null : wxName.trim();
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
}
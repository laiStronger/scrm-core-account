/*
 * 文件名：OrgPostService.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：tanguojun
 * 修改时间：2016年12月16日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.service.org;

import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.dto.org.OrgPostDto;
import com.youanmi.scrm.api.account.service.org.IOrgPostService;
import com.youanmi.scrm.commons.constants.result.ResultCode;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.OrgPostPo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 机构岗位service
 * 
 * @author tanguojun 2016年12月16日
 * @version 1.0.0
 */
public class OrgPostService implements IOrgPostService {
    /**
     * 调测日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrgPostService.class);

    @Autowired
    private BaseDAO baseDAO;

    /**
	 * 机构岗位分页查询
	 * @param page PageBean<UserInfoDto>分页对象信息
	 * @return PageBean<OrgPostDto> 返回分页对象信息
	 * @author 张秋平 2016-12-19
	 */
	@Override
	public PageBean<OrgPostDto> getOrgPostPage(PageBean<OrgPostDto> page) {
		LOG.info("进入机构岗位分页列表服务.....开始");
		page.setData(baseDAO.findForList(MappersConstants.Post.GET_ORG_POST_PAGE,page));
		page.setAllRecord(baseDAO.findForObject(MappersConstants.Post.GET_ORG_POST_PAGE_COUNT,page));
		LOG.info("查询机构岗位分页列表服务.....结束");
		return page;
	}

	/**
	 * 增加机构岗位信息 门店内的岗位名称不可重复
	 * @param orgPostDto
	 * @return OrgPostDto 增加的数量
	 * @author 张秋平 2016-12-19
	 */
	@Override
	public OrgPostDto addOrgPost(OrgPostDto orgPostDto){
//		if(null == orgPostDto){
//			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
//		}
		
		//验证必填字段，包括：岗位名称，权限id数组或者集合
//		if(	null == orgPostDto.getOrgId() 
//				|| null == orgPostDto.getPermissionIds()
//				|| orgPostDto.getPermissionIds().size() < 1){
//			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
//		}
		//验证岗位名称是否重复
//		if(postIsExist(orgPostDto)){
//			throw new ViewExternalDisplayException(ResultCode.OrgPost.POST_NAME_IS_EXIST);//岗位名称已存在
//		}
		//增加岗位信息
		baseDAO.save(MappersConstants.Post.ADD_ORG_POST, orgPostDto);
		return orgPostDto;
	}
	
	/**
	 * 验证岗位名称是否存在
	 * @param orgPostDto 机构岗位信息(需要岗位id和机构id)
	 * @return Boolean true：存在，false：不存在
	 */
	public Boolean postIsExist(OrgPostDto orgPostDto){
		//根据岗位名称和门店id查询
		List<OrgPostPo> list = baseDAO.findForList(MappersConstants.Post.GET_ORG_POST_LIST_BY_POST, orgPostDto);
		if(null == list || list.isEmpty()){
			return false;
		}
		return true;
	}

	/**
	 * 编辑机构岗位信息
	 * @param orgPostDto 机构岗位信息
	 * @author 张秋平 2016-12-19
	 */
	@Override
	public void editOrgPost(OrgPostDto orgPostDto) {
//		if(null == orgPostDto){
//			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
//		}
		//验证必填字段，包括：岗位名称，权限id数组或者集合
//		if(null == orgPostDto.getId() || null == orgPostDto.getOrgId() 
//				|| null == orgPostDto.getPermissionIds()
//				|| orgPostDto.getPermissionIds().size() < 1){
//			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
//		}
		//验证岗位名称是否重复
//		if(postIsExist(orgPostDto)){
//			throw new ViewExternalDisplayException(ResultCode.OrgPost.POST_NAME_IS_EXIST);//岗位名称已存在
//		}
		//更新机构岗位信息
		baseDAO.update(MappersConstants.Post.UPDATE_ORG_POST, orgPostDto);
	}

	/**
	 * 更新删除机构岗位信息
	 * @param id 机构岗位id
	 * @return int 删除的数量
	 * @author 张秋平 2016-12-19
	 */
	@Override
	public int delOrgPost(Long id) {
		if(null == id){
			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
		}
//		int count = baseDAO.delete(MappersConstants.Post.DELETE_ORG_POST, id);
		OrgPostPo post = new OrgPostPo();
		post.setUpdateTime(System.currentTimeMillis());
		post.setId(id);
		int count = baseDAO.update(MappersConstants.Post.DELETE_ORG_POST, post);
		return count;
	}
	
	/**
     * 根据员工获取岗位实体
     */
    @Override
    public OrgPostDto getStaffPostByStaffId(Long staffId) {
        OrgPostPo po = baseDAO.findForObject(MappersConstants.Post.GET_STAFF_POST_BY_STAFFID,staffId);
        
        OrgPostDto dto = BeanCopyUtils.map(po, OrgPostDto.class);
        return dto;
    }

    /**
     * 更新实体
     */
    @Override
    public void updateByPrimaryKeySelective(OrgPostDto updateOrgPostDto) {
        OrgPostPo po = BeanCopyUtils.map(updateOrgPostDto, OrgPostPo.class);
        baseDAO.update(MappersConstants.Post.UPDATE_BY_PRIMARYKEY_SELECTIVE, po);
        
    }

    /**
     * 
     * 添加岗位
     */
    @Override
    public Long insertSelective(OrgPostDto orgPostDto) {
        OrgPostPo po = BeanCopyUtils.map(orgPostDto, OrgPostPo.class);
        baseDAO.save(MappersConstants.Post.INSERT_SELECTIVE, po);
        Long id = po.getId();
        return id;
    }


	/**
	 * 查询机构(门店)下的所有岗位，返回集合； 张秋平 2016-12-22
	 * @param post 机构岗位信息
	 * @return List<OrgPostDto> 门店所有岗位集合
	 */
	public List<OrgPostDto> getAllOrgPost(OrgPostDto post){
		List<OrgPostPo> data = baseDAO.findForList(MappersConstants.Post.GET_ORG_ALL_POST,post);
//		if(null == data || data.isEmpty()){
//			return null;
//		}
//		List<OrgPostDto> list = BeanCopyUtils.mapList(data, OrgPostDto.class);
    	return data == null ? null : BeanCopyUtils.mapList(data, OrgPostDto.class);
	}

	/**
	 * 根据岗位id查询岗位信息
	 * @param postId 机构id
	 * @return OrgPostDto 岗位对象信息
	 */
	@Override
	public OrgPostDto getPost(Long postId){
		OrgPostPo po =  baseDAO.findForObject(MappersConstants.Post.GET_BY_ID, postId);
		return po == null ? null : BeanCopyUtils.map(po, OrgPostDto.class);
	}

	/**
	 * 根据岗位id，获取岗位信息
	 * @param id 岗位id
	 * @return OrgPostDto 岗位对象信息
	 * 张秋平 2016-12-22
	 */
	public OrgPostDto getOrgPost(Long id){
		OrgPostPo post = baseDAO.findForObject(MappersConstants.Post.GET_BY_ID,id);
		if(null == post){
			return null;
		}
		return BeanCopyUtils.map(post,OrgPostDto.class);
	}

	/**
	 * 查询调岗时的岗位列表(如果店长岗位已关联店员，则不显示)
	 * @param orgId 机构id
	 * @return List<OrgPostDto> 岗位列表
	 * @author 张秋平
	 */
	@Override
	public List<OrgPostDto> getAllOrgPostForAdjustPost(Long orgId) {
		//查询岗位列表，需排序已关联店员的店长岗位
		List<OrgPostPo> data = baseDAO.findForList(MappersConstants.Post.getAllOrgPostForAdjustPost,orgId);
    	return data == null ? null : BeanCopyUtils.mapList(data, OrgPostDto.class);
	}

	/**
	 * 根据机构id和岗位名称查询岗位信息（主要用户判断岗位名称是否存在）
	 * @param postName 岗位名称
	 * @param orgId 机构id
	 * @param postId 岗位id
	 * @return OrgPostDto返回岗位对象
	 * 张秋平 2016-12-30
	 */
	@Override
	public List<OrgPostDto> getOrgPostByPost(OrgPostDto post) {
		List<OrgPostPo> list = baseDAO.findForList(MappersConstants.Post.GET_ORG_POST_LIST_BY_POST, post);
		return list == null ? null : BeanCopyUtils.mapList(list, OrgPostDto.class);
	}


}

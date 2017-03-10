package com.youanmi.scrm.core.account.service.shop;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.exception.ViewExternalDisplayException;
import com.youanmi.commons.base.helper.PageUtils;
import com.youanmi.commons.base.helper.ValidateViewExceptionUtils;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.api.account.constants.AccountTableConstants;
import com.youanmi.scrm.api.account.dto.org.OrgInfoDto;
import com.youanmi.scrm.api.account.dto.org.OrgStaffDetailDto;
import com.youanmi.scrm.api.account.dto.org.OrgStaffDto;
import com.youanmi.scrm.api.account.dto.shop.ChangePostDto;
import com.youanmi.scrm.api.account.service.org.IOrgInfoService;
import com.youanmi.scrm.api.account.service.org.IOrgStaffService;
import com.youanmi.scrm.api.account.service.shop.IShopStaffService;
import com.youanmi.scrm.commons.constants.result.ResultCode;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;
import com.youanmi.scrm.core.account.MappersConstants;
import com.youanmi.scrm.core.account.po.org.OrgPostPo;
import com.youanmi.scrm.core.account.po.org.OrgStaffDetailPo;
import com.youanmi.scrm.core.account.po.org.OrgStaffPo;
import com.youanmi.scrm.core.account.service.org.OrgStaffService;

/**
 * @author sunxiaolong on 2016/12/22
 */
@Service
public class ShopStaffService implements IShopStaffService {

	/**
	 * 调测日志记录器。
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(OrgStaffService.class);

	@Resource
	private BaseDAO baseDAO;

	@Resource
	private IOrgStaffService orgStaffService;

	@Resource
	private IOrgInfoService orgInfoService;

	@Transactional
	@Override
	public void changePost(ChangePostDto param) {
		OrgStaffDto staff = orgStaffService
				.getOrgStaffById(param.getStaffId());
		if (staff == null) {
			ValidateViewExceptionUtils.throwDataNotExistException();
		}
		OrgInfoDto org = orgInfoService.getOrgById(staff.getOrgId());

		if (org == null) {
			ValidateViewExceptionUtils.throwDataNotExistException();
		}


		// 如果操作人与被修改人为同一门店或者操作人在被修改人的门店层级之上 才允许修改
		if (! (staff.getOrgId().equals(param.getOperatorOrgId())|| org.getOrgPath().contains(param.getOperatorOrgId().toString()))) {
			ValidateViewExceptionUtils.throwIllegalOperationException("非法的操作人");
		}


		if (staff.getPostId() != null) {
			OrgPostPo oldPostPo = baseDAO.findForObject(
					MappersConstants.Post.GET_BY_ID, staff.getPostId());
			if(oldPostPo != null && oldPostPo.getPostType().equals(AccountTableConstants.Post.POST_TYPE_ADMIN)){
				ValidateViewExceptionUtils.throwIllegalOperationException("不能给管理员调岗");
			}
		}

		OrgPostPo newPostPo = baseDAO.findForObject(
				MappersConstants.Post.GET_BY_ID, param.getPostId());
		if (newPostPo == null) {
			ValidateViewExceptionUtils.throwDataNotExistException();
		}
		if (newPostPo.getPostType().equals(AccountTableConstants.Post.POST_TYPE_ADMIN)) {
			ValidateViewExceptionUtils.throwIllegalOperationException("新岗位不能为管理员");
		}
		if(staff.getPostId()!=null && staff.getPostId().equals(param.getPostId())){
			ValidateViewExceptionUtils.throwIllegalOperationException("不能调至原岗位");
		}


		// 如果新岗位为店长,则需判断店长岗是否空缺,空缺则可调至店长岗
		if (newPostPo.getPostType().equals(AccountTableConstants.Post.POST_TYPE_SHOP_MANAGER)) {
			//找到店长岗位
			OrgPostPo findPostPo = new OrgPostPo();
			findPostPo.setOrgId(param.getOperatorOrgId());
			findPostPo.setPostType(AccountTableConstants.Post.POST_TYPE_SHOP_MANAGER);
			OrgPostPo shopManagerPost = baseDAO.findForObject(MappersConstants.Post.SELECT_BY_ORG_AND_POST_TYPE, findPostPo);
			// 根据店长岗位的id找到该店的店长,如果能找到说明店长非空缺,不能调至店长岗位
			OrgStaffPo findPo = new OrgStaffPo();
			findPo.setOrgId(staff.getOrgId());
			findPo.setPostId(shopManagerPost.getId());
			List<OrgStaffPo> shopManagers =  baseDAO.findForList(MappersConstants.Staff.SELECT_BY_ORG_AND_POST, findPo);
			if (!CollectionUtils.isEmpty(shopManagers)) {
				ValidateViewExceptionUtils.throwIllegalOperationException("店长非空缺,无法调岗");
			}
		}

		OrgStaffPo staffPo = new OrgStaffPo();
		staffPo.setId(param.getStaffId());
		staffPo.setPostId(param.getPostId());
		staffPo.setUpdateTime(System.currentTimeMillis());
		baseDAO.update(MappersConstants.Staff.UPDATE_BY_PRIMARYKEY_SELECTIVE, staffPo);

	}

	/**
	 * 获取店员列表
	 * 
	 * @param pageBean
	 *            分页对象
	 * @return 分页结果
	 */
	@Override
	public PageBean<OrgStaffDetailDto> getStaffList(
			PageBean<OrgStaffDetailDto> pageBean) {
		// 获取分页参数
		Integer pageIndex = PageUtils.getPageIndex(pageBean.getPageIndex(), 1);
		Integer pageSize = PageUtils.getPageSize(pageBean.getPageSize(), 10);
		// 设置分页参数
		PageHelper.startPage(pageIndex, pageSize);
		List<OrgStaffDetailPo> list = baseDAO.findForList(
				MappersConstants.Staff.GET_STAFF_LIST, pageBean);
		Page<OrgStaffDetailPo> page = (Page<OrgStaffDetailPo>) list;
		List<OrgStaffDetailDto> dtoList = BeanCopyUtils.mapList(list,
				OrgStaffDetailDto.class);
		return PageUtils.getPageBean(pageIndex, pageSize, Long.valueOf(page.getTotal()).intValue(),
				page.getPages(), dtoList);
	}

	@Override
	public PageBean<OrgStaffDetailDto> getStaffList222(
			PageBean<OrgStaffDetailDto> pageBean) {
		List<OrgStaffDetailPo> list = baseDAO.findForList(MappersConstants.Staff.GET_STAFF_LIST, pageBean);
		List<OrgStaffDetailDto> dtoList = BeanCopyUtils.mapList(list,OrgStaffDetailDto.class);
		pageBean.setData(dtoList);
		return pageBean;
	}
	
	 /**
     * 分页：查询店员列表
     * @param pageBean  PageBean<OrgStaffDetailDto>分页对象
     * @return PageBean<OrgStaffDetailDto>分页对象
     * @author 张秋平 2016-12-23 14:00
     */
	@Override
	public PageBean<OrgStaffDetailDto> getShopClerkList(
			PageBean<OrgStaffDetailDto> pageBean, OrgStaffDetailDto staff) {
		if(null == pageBean){
			throw new ViewExternalDisplayException(ResultCode.System.ILLEGALITY_REQUEST);
		}
		pageBean.setParamObject(staff);
		//查询总数量
		pageBean.setAllRecord(baseDAO.findForObject(MappersConstants.Staff.SELECT_ORG_CLERK_COUNT__FOR_PAGE, pageBean));
		//查询分页数据
		pageBean.setData(baseDAO.findForList(MappersConstants.Staff.SELECT_ORG_CLERK_LIST_FOR_PAGE, pageBean));
		return pageBean;
	}
}

/*
 * 文件名：PagingUtils.java
 * 版权：深圳柚安米科技有限公司版权所有
 * 修改人：Administrator
 * 修改时间：2016年5月6日
 * 修改内容：新增
 */
package com.youanmi.scrm.core.account.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youanmi.commons.base.dao.BaseDAO;
import com.youanmi.commons.base.helper.PageUtils;
import com.youanmi.commons.base.vo.PageBean;
import com.youanmi.scrm.commons.util.object.BeanCopyUtils;

/**
 *分页工具类
 * <p>
 * 分页工具类
 *
 *
 * @author     liulj
 * @since      2.2.4
 */
public class PagingUtils {
    private PagingUtils(){}

    /**
     *
     *  分页
     *
     * @param baseDAO  执行的dao对象
     * @param mapperId  mapper方法的id
     * @param param     mapper方法的参数
     * @param pageIndex 页码
     * @param pageSize 页数
     * @param mapDesClass 要转换的类型，如果不需要，则写为null
     * @return 分页对象
     */
    @SuppressWarnings("unchecked")
    public static <E> PageBean<E> paging(BaseDAO baseDAO, String mapperId, Object param,
                                         Integer pageIndex, Integer pageSize, Class<E> mapDesClass) {

        // 判断并获取pageIndex
        pageIndex = PageUtils.getPageIndex(pageIndex, 1);
        // 判断并获取pageSize
        pageSize = PageUtils.getPageSize(pageSize, 10);

        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);

        // 查询延保产品列表
        List<?> datas =  baseDAO.findForList(mapperId,param);
        List<E> desList;
        if (mapDesClass != null) {
           desList = BeanCopyUtils.mapList(datas, mapDesClass);
        }
        else{
            desList = (List<E>) datas;
        }

        // 转换列表
        Page<?> lists =  (Page<?>) datas;

        return PageUtils.getPageBean(pageIndex, pageSize, new Long(lists.getTotal()).intValue(),
                lists.getPages(), desList);
    }
    /**
     *
     *  原生分页
     *
     * @param baseDAO  执行的dao对象
     * @param mapperId  mapper方法的id
     * @param param     mapper方法的参数
     * @param pageIndex 页码
     * @param pageSize 页数
     * @return 分页对象
     */
    @SuppressWarnings("unchecked")
    public static <E> Page<E> nativePaging(BaseDAO baseDAO,String mapperId,Object param,
        Integer pageIndex,Integer pageSize) {

        // 判断并获取pageIndex
        pageIndex = PageUtils.getPageIndex(pageIndex, 1);
        // 判断并获取pageSize
        pageSize = PageUtils.getPageSize(pageSize, 10);

        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);

        return (Page<E>) baseDAO.findForList(mapperId,param);
    }
    /**
     *
     * 分页
     *
     * @param baseDAO  dao对象
     * @param mapperId  mapper的id
     * @param param  参数
     * @param pageIndex  页码
     * @param pageSize  页大小
     * @return  分页对象
     */
    public static <E> PageBean<E> paging(BaseDAO baseDAO,String mapperId,Object param,Integer pageIndex,Integer pageSize) {
        return paging(baseDAO, mapperId, param, pageIndex, pageSize,null);
    }
    /**
     *
     * 分页
     *
     * @param baseDAO  dao对象
     * @param mapperId  mapper的id
     * @param param  参数
     * @param pageIndex  页码
     * @param pageSize  页大小
     * @param transition 回调的方法，进行转换list用
     * @return  分页对象
     */
    public static <E,T> PageBean<E> pagingTranstion(BaseDAO baseDAO,String mapperId,Object param,Integer pageIndex,
            Integer pageSize,Function<T, E> transition) {
        // 判断并获取pageIndex
        pageIndex = PageUtils.getPageIndex(pageIndex, 1);
        // 判断并获取pageSize
        pageSize = PageUtils.getPageSize(pageSize, 10);

        // 设置分页参数
        PageHelper.startPage(pageIndex, pageSize);

        // 查询延保产品列表
        List<T> datas =  baseDAO.findForList(mapperId,param);
        List<E> desList = new ArrayList<>();
        if (transition != null) {
            datas.forEach(p-> desList.add(transition.apply(p)));
        }
    
        // 转换列表
        Page<T> lists =  (Page<T>) datas;
    
        return PageUtils.getPageBean(pageIndex, pageSize, new Long(lists.getTotal()).intValue(),
                lists.getPages(), desList);
    }
        
}

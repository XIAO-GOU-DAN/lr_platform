package com.lr.platform.service;

import com.lr.platform.entity.domainsAdmins.DomainsAdmins;

import java.util.List;

/**
 * (DomainsAdmins)表服务接口
 *
 * @author makejava
 * @since 2022-06-29 15:37:21
 */
public interface DomainsAdminsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DomainsAdmins queryById(Integer id);
    Integer updateByDomainAdmin(DomainsAdmins set, DomainsAdmins where);
    Boolean exists(Integer id);
    Boolean existsByDomainAdmin(DomainsAdmins domainsAdmins);
    List<DomainsAdmins> queryAll(DomainsAdmins domainsAdmins);
    int register(DomainsAdmins domainsAdmins);
    /**
     * 新增数据
     *
     * @param domainsAdmins 实例对象
     * @return 实例对象
     */
    DomainsAdmins insert(DomainsAdmins domainsAdmins);

    /**
     * 修改数据
     *
     * @param domainsAdmins 实例对象
     * @return 实例对象
     */
    DomainsAdmins update(DomainsAdmins domainsAdmins);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

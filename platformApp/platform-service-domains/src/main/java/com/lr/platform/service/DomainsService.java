package com.lr.platform.service;


import com.lr.platform.entity.domains.Domains;

import java.util.List;

/**
 * (Domains)表服务接口
 *
 * @author makejava
 * @since 2022-07-09 18:44:13
 */
public interface DomainsService {

    Boolean existsByDomains(Domains domains);
    Domains queryById(Integer id);
    List<Domains> queryAll(Domains domains);
    Boolean exists(Integer id);
    int register(Domains domains);

    /**
     * 新增数据
     *
     * @param domains 实例对象
     * @return 实例对象
     */
    Domains insert(Domains domains);

    /**
     * 修改数据
     *
     * @param domains 实例对象
     * @return 实例对象
     */
    Domains update(Domains domains);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

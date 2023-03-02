package com.lr.platform.service;


import com.lr.platform.entity.admins.Admins;

import java.util.List;

/**
 * (Admins)表服务接口
 *
 * @author makejava
 * @since 2022-06-29 15:30:20
 */
public interface AdminsService {
    Boolean existsByAdmin(Admins admins);
    List<Admins> getAll(Admins admins);
    Admins queryByAdmins(Admins admins);
    int register(Admins admins);
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Admins queryById(Integer id);

    Boolean exists(Integer id);
    /**
     * 新增数据
     *
     * @param admins 实例对象
     * @return 实例对象
     */
    Admins insert(Admins admins);

    /**
     * 修改数据
     *
     * @param admins 实例对象
     * @return 实例对象
     */
    int update(Admins admins);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

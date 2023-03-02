package com.lr.platform.service;

import com.lr.platform.entity.managerLog.ManagerLog;

import java.util.List;

/**
 * (Log)表服务接口
 *
 * @author makejava
 * @since 2022-06-28 17:12:05
 */
public interface ManagerLogService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ManagerLog queryById(Integer id);
    List<ManagerLog> getAll(ManagerLog managerLog);
    List<ManagerLog> queryByOffset(ManagerLog guestLog);
    /**
     * 新增数据
     *
     * @param managerLog 实例对象
     * @return 实例对象
     */
    ManagerLog insert(ManagerLog managerLog);

    /**
     * 修改数据
     *
     * @param managerLog 实例对象
     * @return 实例对象
     */
    ManagerLog update(ManagerLog managerLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

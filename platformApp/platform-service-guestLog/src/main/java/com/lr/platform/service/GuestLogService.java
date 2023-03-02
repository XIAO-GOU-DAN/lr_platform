package com.lr.platform.service;

import com.lr.platform.entity.guestLog.GuestLog;

import java.util.List;

/**
 * (GuestLog)表服务接口
 *
 * @author makejava
 * @since 2022-07-15 23:57:38
 */
public interface GuestLogService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    GuestLog queryById(Integer id);
    List<GuestLog> queryAll(GuestLog guestLog);
    List<GuestLog> queryByOffset(GuestLog guestLog);
    /**
     * 新增数据
     *
     * @param guestLog 实例对象
     * @return 实例对象
     */
    GuestLog insert(GuestLog guestLog);

    /**
     * 修改数据
     *
     * @param guestLog 实例对象
     * @return 实例对象
     */
    GuestLog update(GuestLog guestLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

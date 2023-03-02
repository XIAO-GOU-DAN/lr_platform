package com.lr.platform.service.impl;

import com.lr.platform.dao.GuestLogDao;
import com.lr.platform.entity.guestLog.GuestLog;
import com.lr.platform.service.GuestLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (GuestLog)表服务实现类
 *
 * @author makejava
 * @since 2022-07-15 23:57:38
 */
@Service("guestLogService")
public class GuestLogServiceImpl implements GuestLogService {
    @Resource
    private GuestLogDao guestLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public GuestLog queryById(Integer id) {

        return guestLogDao.queryById(id);
    }

    @Override
    public List<GuestLog> queryAll(GuestLog guestLog) {
        return guestLogDao.queryAll(guestLog);
    }

    @Override
    public List<GuestLog> queryByOffset(GuestLog guestLog) {
        return guestLogDao.queryByOffset(guestLog);
    }


    /**
     * 新增数据
     *
     * @param guestLog 实例对象
     * @return 实例对象
     */
    @Override
    public GuestLog insert(GuestLog guestLog) {
        this.guestLogDao.insert(guestLog);
        return guestLog;
    }

    /**
     * 修改数据
     *
     * @param guestLog 实例对象
     * @return 实例对象
     */
    @Override
    public GuestLog update(GuestLog guestLog) {
        this.guestLogDao.update(guestLog);
        return this.queryById(guestLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.guestLogDao.deleteById(id) > 0;
    }
}

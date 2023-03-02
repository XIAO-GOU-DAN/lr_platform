package com.lr.platform.service.impl;

import com.lr.platform.dao.ManagerLogDao;
import com.lr.platform.entity.guestLog.GuestLog;
import com.lr.platform.entity.managerLog.ManagerLog;
import com.lr.platform.service.ManagerLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Log)表服务实现类
 *
 * @author makejava
 * @since 2022-06-28 17:12:05
 */
@Service("managerLogService")
public class ManagerManagerLogServiceImpl implements ManagerLogService {
    @Resource
    private ManagerLogDao managerLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ManagerLog queryById(Integer id) {
        return this.managerLogDao.queryById(id);
    }

    @Override
    public List<ManagerLog> getAll(ManagerLog managerLog) {
        return managerLogDao.getAll(managerLog);
    }

    @Override
    public List<ManagerLog> queryByOffset(ManagerLog guestLog) {
        return managerLogDao.queryByOffset(guestLog);
    }


    /**
     * 新增数据
     *
     * @param managerLog 实例对象
     * @return 实例对象
     */
    @Override
    public ManagerLog insert(ManagerLog managerLog) {
        this.managerLogDao.insert(managerLog);
        return managerLog;
    }

    /**
     * 修改数据
     *
     * @param managerLog 实例对象
     * @return 实例对象
     */
    @Override
    public ManagerLog update(ManagerLog managerLog) {
        this.managerLogDao.update(managerLog);
        return this.queryById(managerLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.managerLogDao.deleteById(id) > 0;
    }
}

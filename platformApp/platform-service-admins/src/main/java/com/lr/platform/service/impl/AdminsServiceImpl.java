package com.lr.platform.service.impl;

import com.lr.platform.dao.AdminsDao;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.service.AdminsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Admins)表服务实现类
 *
 * @author makejava
 * @since 2022-06-29 15:30:20
 */
@Service("adminsService")
public class AdminsServiceImpl implements AdminsService {
    @Resource
    private AdminsDao adminsDao;

    @Override
    public Boolean existsByAdmin(Admins admins) {
        return adminsDao.existsByAdmin(admins);
    }

    @Override
    public List<Admins> getAll(Admins admins) {
        return adminsDao.getAll(admins);
    }

    @Override
    public Admins queryByAdmins(Admins admins) {
        return adminsDao.queryByAdmins(admins);
    }

    @Override
    public int register(Admins admins) {
        return adminsDao.register(admins);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Admins queryById(Integer id) {
        return this.adminsDao.queryById(id);
    }

    @Override
    public Boolean exists(Integer id) {
        return adminsDao.exists(id);
    }


    /**
     * 新增数据
     *
     * @param admins 实例对象
     * @return 实例对象
     */
    @Override
    public Admins insert(Admins admins) {
        this.adminsDao.insert(admins);
        return admins;
    }

    /**
     * 修改数据
     *
     * @param admins 实例对象
     * @return 实例对象
     */
    @Override
    public int update(Admins admins) {
        return this.adminsDao.update(admins);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.adminsDao.deleteById(id) > 0;
    }
}

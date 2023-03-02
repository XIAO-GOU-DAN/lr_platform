package com.lr.platform.service.impl;


import com.lr.platform.dao.UsersDao;
import com.lr.platform.entity.users.Users;
import com.lr.platform.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Users)表服务实现类
 *
 * @author makejava
 * @since 2022-07-02 17:44:59
 */
@Service("usersService")
public class UsersServiceImpl implements UsersService {
    @Resource
    private UsersDao usersDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Users queryById(Integer id) {
        return this.usersDao.queryById(id);
    }

    @Override
    public List<Users> queryAll(Users users) {
        return this.usersDao.queryAll(users);
    }

    @Override
    public Users queryByUsers(Users users) {
        return usersDao.queryByUsers(users);
    }

    @Override
    public Boolean exists(Integer id) {
        return this.usersDao.exists(id);
    }

    @Override
    public int register(Users users) {
        return usersDao.register(users);
    }

    @Override
    public List<Users> queryAllExcludeUsers(Users users, List<Integer> ids) {
        return usersDao.queryAllExcludeUsers(users,ids);
    }

    @Override
    public long count(Users users) {
        return usersDao.count(users);
    }

    @Override
    public List<Users> queryAllGrade(Users users) {
        return usersDao.queryAllGrade(users);
    }

    @Override
    public List<Users> queryByOffset(Users users) {
        return usersDao.queryByOffset(users);
    }

    @Override
    public List<Users> queryByQueryUser(Users users) {
        return usersDao.queryByQueryUser(users);
    }

    /**
     * 新增数据
     *
     * @param users 实例对象
     * @return 实例对象
     */
    @Override
    public Users insert(Users users) {
        this.usersDao.insert(users);
        return users;
    }

    /**
     * 修改数据
     *
     * @param users 实例对象
     * @return 实例对象
     */
    @Override
    public int update(Users users) {
        return this.usersDao.update(users);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.usersDao.deleteById(id) > 0;
    }
}

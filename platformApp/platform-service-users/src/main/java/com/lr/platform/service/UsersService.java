package com.lr.platform.service;

import com.lr.platform.entity.users.Users;

import java.util.List;

/**
 * (Users)表服务接口
 *
 * @author makejava
 * @since 2022-07-02 17:44:59
 */
public interface UsersService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Users queryById(Integer id);
    List<Users> queryAll(Users users);
    Users queryByUsers(Users users);
    Boolean exists(Integer id);
    int register(Users users);
    List<Users> queryAllExcludeUsers(Users users,List<Integer> ids);
    long count(Users users);
    List<Users> queryAllGrade(Users users);
    List<Users> queryByOffset(Users users);
    List<Users> queryByQueryUser(Users users);
    /**
     * 新增数据
     *
     * @param users 实例对象
     * @return 实例对象
     */
    Users insert(Users users);

    /**
     * 修改数据
     *
     * @param users 实例对象
     * @return 实例对象
     */
    int update(Users users);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

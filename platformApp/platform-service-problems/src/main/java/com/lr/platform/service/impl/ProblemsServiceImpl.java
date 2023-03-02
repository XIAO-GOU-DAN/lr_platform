package com.lr.platform.service.impl;

import com.lr.platform.dao.ProblemsDao;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.service.ProblemsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Problems)表服务实现类
 *
 * @author makejava
 * @since 2022-07-07 13:13:36
 */
@Service("problemsService")
public class ProblemsServiceImpl implements ProblemsService {
    @Resource
    private ProblemsDao problemsDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Problems queryById(Integer id) {
        return this.problemsDao.queryById(id);
    }

    @Override
    public Integer updateByProblems(Problems setProblems,Problems whereProblems) {
        return problemsDao.updateByProblems(setProblems,whereProblems);
    }

    @Override
    public Boolean exists(Integer id) {
        return problemsDao.exists(id);
    }

    @Override
    public List<Problems> queryAll(Problems problems) {
        return problemsDao.queryAll(problems);
    }


    /**
     * 新增数据
     *
     * @param problems 实例对象
     * @return 实例对象
     */
    @Override
    public Problems insert(Problems problems) {
        this.problemsDao.insert(problems);
        return problems;
    }

    /**
     * 修改数据
     *
     * @param problems 实例对象
     * @return 实例对象
     */
    @Override
    public Problems update(Problems problems) {
        this.problemsDao.update(problems);
        return this.queryById(problems.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.problemsDao.deleteById(id) > 0;
    }
}

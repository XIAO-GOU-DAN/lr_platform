package com.lr.platform.service;

import com.lr.platform.entity.problems.Problems;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Problems)表服务接口
 *
 * @author makejava
 * @since 2022-07-07 13:13:36
 */
public interface ProblemsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Problems queryById(Integer id);
    Integer updateByProblems(Problems setProblems,Problems whereProblems);
    Boolean exists(Integer id);
    List<Problems> queryAll(Problems problems);
    /**
     * 新增数据
     *
     * @param problems 实例对象
     * @return 实例对象
     */
    Problems insert(Problems problems);

    /**
     * 修改数据
     *
     * @param problems 实例对象
     * @return 实例对象
     */
    Problems update(Problems problems);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

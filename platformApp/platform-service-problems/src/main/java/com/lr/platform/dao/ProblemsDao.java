package com.lr.platform.dao;

import com.lr.platform.entity.problems.Problems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Problems)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-07 13:13:36
 */
@Repository
@Mapper
public interface ProblemsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Problems queryById(Integer id);
    List<Problems> queryAll(Problems problems);
    Boolean exists(Integer id);
    Integer updateByProblems(@Param("set")Problems setProblems,@Param("where")Problems whereProblems);
    /**
     * 统计总行数
     *
     * @param problems 查询条件
     * @return 总行数
     */
    long count(Problems problems);

    /**
     * 新增数据
     *
     * @param problems 实例对象
     * @return 影响行数
     */
    int insert(Problems problems);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Problems> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Problems> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Problems> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Problems> entities);

    /**
     * 修改数据
     *
     * @param problems 实例对象
     * @return 影响行数
     */
    int update(Problems problems);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


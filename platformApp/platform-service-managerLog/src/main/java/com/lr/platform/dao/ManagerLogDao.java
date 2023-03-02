package com.lr.platform.dao;

import com.lr.platform.entity.managerLog.ManagerLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Log)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-28 17:12:05
 */
@Mapper
@Repository
public interface ManagerLogDao {

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
     * 统计总行数
     *
     * @param managerLog 查询条件
     * @return 总行数
     */
    long count(ManagerLog managerLog);

    /**
     * 新增数据
     *
     * @param managerLog 实例对象
     * @return 影响行数
     */
    int insert(ManagerLog managerLog);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Log> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ManagerLog> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Log> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<ManagerLog> entities);

    /**
     * 修改数据
     *
     * @param managerLog 实例对象
     * @return 影响行数
     */
    int update(ManagerLog managerLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


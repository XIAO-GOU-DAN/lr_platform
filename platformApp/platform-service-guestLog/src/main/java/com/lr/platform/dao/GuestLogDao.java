package com.lr.platform.dao;

import com.lr.platform.entity.guestLog.GuestLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (GuestLog)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-15 23:57:38
 */
@Mapper
@Repository
public interface GuestLogDao {

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
     * 统计总行数
     *
     * @param guestLog 查询条件
     * @return 总行数
     */
    long count(GuestLog guestLog);

    /**
     * 新增数据
     *
     * @param guestLog 实例对象
     * @return 影响行数
     */
    int insert(GuestLog guestLog);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<GuestLog> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<GuestLog> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<GuestLog> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<GuestLog> entities);

    /**
     * 修改数据
     *
     * @param guestLog 实例对象
     * @return 影响行数
     */
    int update(GuestLog guestLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


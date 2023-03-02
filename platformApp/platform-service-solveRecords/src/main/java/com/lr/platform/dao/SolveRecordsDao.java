package com.lr.platform.dao;

import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (SolveRecords)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-07 13:16:11
 */
@Repository
@Mapper
public interface SolveRecordsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SolveRecords queryById(Integer id);
    Integer updateBySolveRecords(@Param("set")SolveRecords set,@Param("where") WhereRecords where);
    List<SolveRecords> queryAll(WhereRecords whereRecords);
    SolveRecords queryBySolveRecords(WhereRecords whereRecords);
    int updateSolveRecordsAddOneToSubmitTimes(SolveRecords solveRecords);
    int register(SolveRecords solveRecords);
    int handleUpload(SolveRecords solveRecords);
    Long countByWhereRecord(WhereRecords whereRecords);
    /**
     * 统计总行数
     *
     * @param solveRecords 查询条件
     * @return 总行数
     */
    Long count(SolveRecords solveRecords);

    /**
     * 新增数据
     *
     * @param solveRecords 实例对象
     * @return 影响行数
     */
    int insert(SolveRecords solveRecords);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SolveRecords> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SolveRecords> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SolveRecords> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SolveRecords> entities);

    /**
     * 修改数据
     *
     * @param solveRecords 实例对象
     * @return 影响行数
     */
    int update(SolveRecords solveRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


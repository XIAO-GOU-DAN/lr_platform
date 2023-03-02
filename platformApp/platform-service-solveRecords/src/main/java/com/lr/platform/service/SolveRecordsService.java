package com.lr.platform.service;

import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;

import java.util.List;

/**
 * (SolveRecords)表服务接口
 *
 * @author makejava
 * @since 2022-07-07 13:16:12
 */
public interface SolveRecordsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SolveRecords queryById(Integer id);
    Integer updateBySolveRecords(SolveRecords set, WhereRecords where);
    List<SolveRecords> queryAll(WhereRecords whereRecords);
    Long count(SolveRecords solveRecords);
    SolveRecords queryBySolveRecords(WhereRecords whereRecords);
    int updateSolveRecordsAddOneToSubmitTimes(SolveRecords solveRecords);
    int register(SolveRecords solveRecords);
    int handleUpload(SolveRecords solveRecords);
    Long countByWhereRecord(WhereRecords whereRecords);
    /**
     * 新增数据
     *
     * @param solveRecords 实例对象
     * @return 实例对象
     */
    SolveRecords insert(SolveRecords solveRecords);

    /**
     * 修改数据
     *
     * @param solveRecords 实例对象
     * @return 实例对象
     */
    SolveRecords update(SolveRecords solveRecords);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

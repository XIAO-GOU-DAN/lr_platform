package com.lr.platform.service.impl;

import com.lr.platform.dao.SolveRecordsDao;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.service.SolveRecordsService;
import org.springframework.stereotype.Service;
import com.lr.platform.entity.solveRecords.SolveRecords;

import javax.annotation.Resource;
import java.util.List;

/**
 * (SolveRecords)表服务实现类
 *
 * @author makejava
 * @since 2022-07-07 13:16:12
 */
@Service("solveRecordsService")
public class SolveRecordsServiceImpl implements SolveRecordsService {
    @Resource
    private SolveRecordsDao solveRecordsDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SolveRecords queryById(Integer id) {
        return this.solveRecordsDao.queryById(id);
    }

    @Override
    public Integer updateBySolveRecords(SolveRecords set, WhereRecords where) {
        return solveRecordsDao.updateBySolveRecords(set, where);
    }

    @Override
    public List<SolveRecords> queryAll(WhereRecords whereRecords) {
        return solveRecordsDao.queryAll(whereRecords);
    }

    @Override
    public Long count(SolveRecords solveRecords) {
        return solveRecordsDao.count(solveRecords);
    }

    @Override
    public SolveRecords queryBySolveRecords(WhereRecords whereRecords) {
        return solveRecordsDao.queryBySolveRecords(whereRecords);
    }

    @Override
    public int updateSolveRecordsAddOneToSubmitTimes(SolveRecords solveRecords) {
        return solveRecordsDao.updateSolveRecordsAddOneToSubmitTimes(solveRecords);
    }

    @Override
    public int register(SolveRecords solveRecords) {
        return solveRecordsDao.register(solveRecords);
    }

    @Override
    public int handleUpload(SolveRecords solveRecords) {
        return solveRecordsDao.handleUpload(solveRecords);
    }

    @Override
    public Long countByWhereRecord(WhereRecords whereRecords) {
        return solveRecordsDao.countByWhereRecord(whereRecords);
    }


    /**
     * 新增数据
     *
     * @param solveRecords 实例对象
     * @return 实例对象
     */
    @Override
    public SolveRecords insert(SolveRecords solveRecords) {
        this.solveRecordsDao.insert(solveRecords);
        return solveRecords;
    }

    /**
     * 修改数据
     *
     * @param solveRecords 实例对象
     * @return 实例对象
     */
    @Override
    public SolveRecords update(SolveRecords solveRecords) {
        this.solveRecordsDao.update(solveRecords);
        return this.queryById(solveRecords.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.solveRecordsDao.deleteById(id) > 0;
    }
}

package com.lr.platform.dao;

import com.lr.platform.entity.admins.Admins;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Admins)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-29 15:30:20
 */
@Mapper
@Repository
public interface AdminsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Admins queryById(Integer id);
    Boolean existsByAdmin(Admins admins);
    Boolean exists(Integer id);
    Admins queryByAdmins(Admins admins);
    int register(Admins admins);
    /**
     * 统计总行数
     *
     * @param admins 查询条件
     * @return 总行数
     */
    long count(Admins admins);
    List<Admins> getAll(Admins admins);
    /**
     * 新增数据
     *
     * @param admins 实例对象
     * @return 影响行数
     */
    int insert(Admins admins);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Admins> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Admins> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Admins> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Admins> entities);

    /**
     * 修改数据
     *
     * @param admins 实例对象
     * @return 影响行数
     */
    int update(Admins admins);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


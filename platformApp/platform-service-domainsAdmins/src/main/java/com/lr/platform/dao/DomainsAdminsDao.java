package com.lr.platform.dao;

import com.lr.platform.entity.domainsAdmins.DomainsAdmins;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (DomainsAdmins)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-29 15:37:21
 */
@Mapper
@Repository
public interface DomainsAdminsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DomainsAdmins queryById(Integer id);

    Boolean exists(Integer id);
    Integer updateByDomainAdmin(@Param("set")DomainsAdmins set,@Param("where") DomainsAdmins where);
    Boolean existsByDomainAdmin(DomainsAdmins domainsAdmins);
    List<DomainsAdmins> queryAll(DomainsAdmins domainsAdmins);
    int register(DomainsAdmins domainsAdmins);
    /**
     * 统计总行数
     *
     * @param domainsAdmins 查询条件
     * @return 总行数
     */
    long count(DomainsAdmins domainsAdmins);

    /**
     * 新增数据
     *
     * @param domainsAdmins 实例对象
     * @return 影响行数
     */
    int insert(DomainsAdmins domainsAdmins);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<DomainsAdmins> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<DomainsAdmins> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<DomainsAdmins> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<DomainsAdmins> entities);

    /**
     * 修改数据
     *
     * @param domainsAdmins 实例对象
     * @return 影响行数
     */
    int update(DomainsAdmins domainsAdmins);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


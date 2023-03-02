package com.lr.platform.dao;

import com.lr.platform.entity.domains.Domains;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Domains)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-09 18:44:13
 */
@Repository
@Mapper
public interface DomainsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Domains queryById(Integer id);
    List<Domains> queryAll(Domains domains);
    Boolean exists(Integer id);
    Boolean existsByDomains(Domains domains);
    int register(Domains domains);
    /**
     * 统计总行数
     *
     * @param domains 查询条件
     * @return 总行数
     */
    long count(Domains domains);

    /**
     * 新增数据
     *
     * @param domains 实例对象
     * @return 影响行数
     */
    int insert(Domains domains);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Domains> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Domains> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Domains> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Domains> entities);

    /**
     * 修改数据
     *
     * @param domains 实例对象
     * @return 影响行数
     */
    int update(Domains domains);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


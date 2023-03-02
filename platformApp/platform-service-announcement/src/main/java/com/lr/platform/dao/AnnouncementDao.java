package com.lr.platform.dao;

import com.lr.platform.entity.announcement.Announcement;
import com.lr.platform.entity.announcement.AnnouncementOffsetVo;
import com.lr.platform.entity.announcement.AnnouncementUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Announcement)表数据库访问层
 *
 * @author makejava
 * @since 2022-08-24 19:32:49
 */
@Mapper
@Repository
public interface AnnouncementDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Announcement queryById(Long id);

    List<Announcement> queryAll(Announcement announcement);
    List<Announcement> queryByOffsetId(AnnouncementOffsetVo announcement);
    List<Announcement> queryByUpdateOffset(AnnouncementOffsetVo announcement);
    /**
     * 统计总行数
     *
     * @param announcement 查询条件
     * @return 总行数
     */
    long count(Announcement announcement);

    /**
     * 新增数据
     *
     * @param announcement 实例对象
     * @return 影响行数
     */
    int insert(Announcement announcement);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Announcement> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Announcement> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Announcement> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Announcement> entities);

    /**
     * 修改数据
     *
     * @param announcement 实例对象
     * @return 影响行数
     */
    int update(AnnouncementUpdate announcement);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}


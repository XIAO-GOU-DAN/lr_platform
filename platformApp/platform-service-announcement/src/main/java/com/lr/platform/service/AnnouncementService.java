package com.lr.platform.service;

import com.lr.platform.entity.announcement.Announcement;
import com.lr.platform.entity.announcement.AnnouncementOffsetVo;
import com.lr.platform.entity.announcement.AnnouncementUpdate;

import java.util.List;

/**
 * (Announcement)表服务接口
 *
 * @author makejava
 * @since 2022-08-24 19:32:49
 */
public interface AnnouncementService {

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
     * 新增数据
     *
     * @param announcement 实例对象
     * @return 实例对象
     */
    Announcement insert(Announcement announcement);

    /**
     * 修改数据
     *
     * @param announcement 实例对象
     * @return 实例对象
     */
    int update(AnnouncementUpdate announcement);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}

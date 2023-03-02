package com.lr.platform.service.impl;

import com.lr.platform.entity.announcement.Announcement;
import com.lr.platform.dao.AnnouncementDao;
import com.lr.platform.entity.announcement.AnnouncementOffsetVo;
import com.lr.platform.entity.announcement.AnnouncementUpdate;
import com.lr.platform.service.AnnouncementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Announcement)表服务实现类
 *
 * @author makejava
 * @since 2022-08-24 19:32:49
 */
@Service("announcementService")
public class AnnouncementServiceImpl implements AnnouncementService {
    @Resource
    private AnnouncementDao announcementDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Announcement queryById(Long id) {
        return this.announcementDao.queryById(id);
    }

    @Override
    public List<Announcement> queryAll(Announcement announcement) {
        return announcementDao.queryAll(announcement);
    }

    @Override
    public List<Announcement> queryByOffsetId(AnnouncementOffsetVo announcement) {
        return announcementDao.queryByOffsetId(announcement);
    }

    @Override
    public List<Announcement> queryByUpdateOffset(AnnouncementOffsetVo announcement) {
        return announcementDao.queryByUpdateOffset(announcement);
    }


    /**
     * 新增数据
     *
     * @param announcement 实例对象
     * @return 实例对象
     */
    @Override
    public Announcement insert(Announcement announcement) {
        this.announcementDao.insert(announcement);
        return announcement;
    }

    /**
     * 修改数据
     *
     * @param announcement 实例对象
     * @return 实例对象
     */
    @Override
    public int update(AnnouncementUpdate announcement) {
        return this.announcementDao.update(announcement);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.announcementDao.deleteById(id) > 0;
    }
}

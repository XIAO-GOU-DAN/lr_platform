package com.lr.platform.service.impl;

import com.lr.platform.dao.DomainsDao;
import com.lr.platform.service.DomainsService;
import org.springframework.stereotype.Service;
import com.lr.platform.entity.domains.Domains;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Domains)表服务实现类
 *
 * @author makejava
 * @since 2022-07-09 18:44:13
 */
@Service("domainsService")
public class DomainsServiceImpl implements DomainsService {
    @Resource
    private DomainsDao domainsDao;


    @Override
    public Boolean existsByDomains(Domains domains) {
        return domainsDao.existsByDomains(domains);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Domains queryById(Integer id) {
        return this.domainsDao.queryById(id);
    }

    @Override
    public Boolean exists(Integer id) {
        return this.domainsDao.exists(id);
    }

    @Override
    public int register(Domains domains) {
        return domainsDao.register(domains);
    }

    @Override
    public List<Domains> queryAll(Domains domains) {
        return this.domainsDao.queryAll(domains);
    }

    /**
     * 新增数据
     *
     * @param domains 实例对象
     * @return 实例对象
     */
    @Override
    public Domains insert(Domains domains) {
        this.domainsDao.insert(domains);
        return domains;
    }

    /**
     * 修改数据
     *
     * @param domains 实例对象
     * @return 实例对象
     */
    @Override
    public Domains update(Domains domains) {
        this.domainsDao.update(domains);
        return this.queryById(domains.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.domainsDao.deleteById(id) > 0;
    }
}

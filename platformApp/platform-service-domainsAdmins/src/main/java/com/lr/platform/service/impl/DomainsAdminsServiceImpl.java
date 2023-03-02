package com.lr.platform.service.impl;

import com.lr.platform.dao.DomainsAdminsDao;
import com.lr.platform.entity.domainsAdmins.DomainsAdmins;
import com.lr.platform.service.DomainsAdminsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (DomainsAdmins)表服务实现类
 *
 * @author makejava
 * @since 2022-06-29 15:37:21
 */
@Service("domainsAdminsService")
public class DomainsAdminsServiceImpl implements DomainsAdminsService {
    @Resource
    private DomainsAdminsDao domainsAdminsDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public DomainsAdmins queryById(Integer id) {
        return this.domainsAdminsDao.queryById(id);
    }

    @Override
    public Integer updateByDomainAdmin(DomainsAdmins set, DomainsAdmins where) {
        return domainsAdminsDao.updateByDomainAdmin(set, where);
    }

    @Override
    public Boolean exists(Integer id) {
        return domainsAdminsDao.exists(id);
    }

    @Override
    public Boolean existsByDomainAdmin(DomainsAdmins domainsAdmins) {
        return domainsAdminsDao.existsByDomainAdmin(domainsAdmins);
    }

    @Override
    public List<DomainsAdmins> queryAll(DomainsAdmins domainsAdmins) {
        return domainsAdminsDao.queryAll(domainsAdmins);
    }

    @Override
    public int register(DomainsAdmins domainsAdmins) {
        return domainsAdminsDao.register(domainsAdmins);
    }


    /**
     * 新增数据
     *
     * @param domainsAdmins 实例对象
     * @return 实例对象
     */
    @Override
    public DomainsAdmins insert(DomainsAdmins domainsAdmins) {
        this.domainsAdminsDao.insert(domainsAdmins);
        return domainsAdmins;
    }

    /**
     * 修改数据
     *
     * @param domainsAdmins 实例对象
     * @return 实例对象
     */
    @Override
    public DomainsAdmins update(DomainsAdmins domainsAdmins) {
        this.domainsAdminsDao.update(domainsAdmins);
        return this.queryById(domainsAdmins.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.domainsAdminsDao.deleteById(id) > 0;
    }

}

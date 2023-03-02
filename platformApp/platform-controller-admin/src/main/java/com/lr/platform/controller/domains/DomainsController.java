package com.lr.platform.controller.domains;

import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsAdminOpVo;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.domains.DomainsVo;
import com.lr.platform.entity.domainsAdmins.DomainsAdmins;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.response.Response;
import com.lr.platform.service.DomainsAdminsService;
import com.lr.platform.service.DomainsService;
import com.lr.platform.service.ProblemsService;
import com.lr.platform.service.SolveRecordsService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * (Direction)表控制层
 *
 * @author makejava
 * @since 2022-06-26 19:36:07
 */
@Api(tags = "方向模块")
@RestController
@RequestMapping("domain")
public class DomainsController {
    /**
     * 服务对象
     */
    @Resource
    private DomainsService domainsService;

    @Resource
    private DomainsAdminsService domainsAdminsService;

    @Resource
    private SolveRecordsService solveRecordsService;

    @Resource
    private ProblemsService problemsService;

    @ApiOperation(value = "获取方向",notes = "option=1 用于获取简洁的结果")
    @GetMapping
    public Response selectDomains(@RequestParam(value = "option",required = false) Integer option){
        Domains domain=new Domains();
        List<Domains> domains = domainsService.queryAll(domain);
        if (Objects.equals(option,1)){
            List<DomainsAdminOpVo> domainsOpVos = Dozer.convert(domains, DomainsAdminOpVo.class);
            WhereRecords whereRecords=new WhereRecords();
            whereRecords.setStatus(1);
            for (DomainsAdminOpVo d:domainsOpVos){
                whereRecords.setDomainId(d.getId());
                d.setNewCommit(solveRecordsService.countByWhereRecord(whereRecords));
            }
            return Response
                    .builder()
                    .len(domainsOpVos.size())
                    .data(domainsOpVos)
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
        }
        List<DomainsVo> domainsVos =Dozer.convert(domains, DomainsVo.class);
        return Response
                .builder()
                .len(domainsVos.size())
                .data(domainsVos)
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "新增修改方向",notes = "")
    @ManagerLog(tag = "新增方向",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/create")
    public Response createDomains(@RequestBody DomainsOpVo domainsOpVo){
        if (!Validator.isValidDomainsName(domainsOpVo.getName())){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_ILLEGAL_NAME)
                    .build();
        }
        Domains domains =Dozer.convert(domainsOpVo, Domains.class);
        domains.setId(null);
        if (domainsService.existsByDomains(domains)){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_NAME_ALREADY_REGISTERED)
                    .build();
        }
        int res=domainsService.register(domains);
        if (res!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_DATA_ALREADY_REGISTER)
                    .build();
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "修改方向")
    @ManagerLog(tag = "修改方向",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateDomains(@RequestBody DomainsOpVo domainsOpVo){
        if (!Validator.isValidDomainsName(domainsOpVo.getName())){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_ILLEGAL_NAME)
                    .build();
        }
        if (!domainsService.exists(domainsOpVo.getId()) ){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_DATA_NOT_FOUND)
                    .build();
        }
        Domains domains =Dozer.convert(domainsOpVo, Domains.class);
        if (domainsService.existsByDomains(domains)){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_NAME_ALREADY_REGISTERED)
                    .build();
        }
        domainsService.update(domains);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "删除方向",notes = "在url中添加id字段")
    @ManagerLog(level = WarningLevel.COMMON_DELETE,tag = "删除方向")
    @GetMapping("/delete")
    public Response deleteDomains(@RequestParam(value = "id") Integer id){
        Domains domains = domainsService.queryById(id);
        if (Objects.equals(domains,null) ){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_DATA_NOT_FOUND)
                    .build();
        }
        domains.setDeletedAt(new Date());
        domainsService.update(domains);
        DomainsAdmins setDomainsAdmins=new DomainsAdmins();
        setDomainsAdmins.setDeletedAt(new Date());
        DomainsAdmins whereDomainsAdmins=new DomainsAdmins();
        whereDomainsAdmins.setDomainId(id);
        domainsAdminsService.updateByDomainAdmin(setDomainsAdmins,whereDomainsAdmins);
        Problems setProblems=new Problems();
        setProblems.setDeletedAt(new Date());
        Problems whereProblems=new Problems();
        whereProblems.setDomainId(id);
        problemsService.updateByProblems(setProblems,whereProblems);
        SolveRecords setSolveRecords=new SolveRecords();
        setSolveRecords.setDeletedAt(new Date());
        WhereRecords whereRecords =new WhereRecords();
        whereRecords.setDomainId(id);
        solveRecordsService.updateBySolveRecords(setSolveRecords,whereRecords);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }
}


package com.lr.platform.controller.solveRecords;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.problems.ProblemsAdminHandlerVo;
import com.lr.platform.entity.solveRecords.*;
import com.lr.platform.entity.users.Users;
import com.lr.platform.entity.users.UsersAdminVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.response.Response;
import com.lr.platform.service.*;
import com.lr.platform.utils.Dozer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * (SolveRecords)表控制层
 *
 * @author makejava
 * @since 2022-07-02 17:41:08
 */
@Api(tags = "解题记录模块")
@RestController
@RequestMapping("solveRecords")
@Slf4j
public class SolveRecordsController {
    /**
     * 服务对象
     */
    @Resource
    private SolveRecordsService solveRecordsService;

    @Resource
    private DomainsService domainsService;

    @Resource
    private ProblemsService problemsService;

    @Resource
    private UsersService usersService;

    @Resource
    private AdminsService adminsService;

    @Value("${data.problems.local.path}")
    private String localPath;

    @ApiOperation(value = "根据id获取解题记录")
    @GetMapping("/queryById")
    public Response queryById(@RequestParam("id") Integer id){
        SolveRecords solveRecords=solveRecordsService.queryById(id);
        if (solveRecords == null){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_NOT_FOUND)
                    .build();
        }
        SolveRecordsAdminHandleVo solveRecordsAdminHandleVo= Dozer.convert(solveRecords,SolveRecordsAdminHandleVo.class);
        Problems problems=problemsService.queryById(solveRecords.getProblemId());
        ProblemsAdminHandlerVo problemsAdminHandlerVo=Dozer.convert(problems,ProblemsAdminHandlerVo.class);
        if (problemsAdminHandlerVo!=null){
            Domains domains= domainsService.queryById(problems.getDomainId());
            problemsAdminHandlerVo.setDomainsOpVo(Dozer.convert(domains, DomainsOpVo.class));
            if (problems.getFirstSolverId()!=null){
                Users first= usersService.queryById(problems.getFirstSolverId());
                problemsAdminHandlerVo.setFirstUser(Dozer.convert(first,UsersAdminVo.class));
            }
        }
        solveRecordsAdminHandleVo.setProblem(problemsAdminHandlerVo);
        Users users=usersService.queryById(solveRecords.getUserId());
        solveRecordsAdminHandleVo.setUser(Dozer.convert(users,UsersAdminVo.class));
        if (problems.getCurrentScore() != null && solveRecords.getDegree() != null){
            solveRecordsAdminHandleVo.setScore(Math.round(problems.getCurrentScore() * solveRecords.getDegree()));
        }else {
            solveRecordsAdminHandleVo.setScore(0);
        }
        if (solveRecords.getHandlerId() != null){
            Admins admins = adminsService.queryById(solveRecords.getHandlerId());
            solveRecordsAdminHandleVo.setHandler(Dozer.convert(admins,AdminsAdminVo.class));
        }
        if (Objects.equals(problems.getSubmitType(),"flag")){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .data(solveRecordsAdminHandleVo)
                    .build();
        }
        String pdfPath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",localPath,solveRecords.getUserId(),users.getUsername()
                ,problems.getId(),solveRecords.getUserId(),users.getUsername(),problems.getId());
        String basePath=String.format("%s/%s_%s/%s",localPath,users.getId(),users.getUsername(),problems.getId());
        String pre=String.format("%s_%s_%s_appendix",users.getId(),users.getUsername(),problems.getId());
        File baseFile=new File(basePath);
        File[] fileList = baseFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir,name).isFile() && name.startsWith(pre);
            }
        });
        if (fileList.length > 1){ //多附件
            log.error("ERROR:记录{},存在多附件",id);
        }
        File file=new File(pdfPath);
        if (file.exists() && file.isFile())solveRecordsAdminHandleVo.setHasPdf(1);
        if (fileList.length>0){
            String  filePath=String.format("%s/%s_%s/%s/%s",localPath,users.getId(),
                    users.getUsername(),problems.getId(),fileList[0].getName());
            file=new File(filePath);
            if (file.exists() && file.isFile())solveRecordsAdminHandleVo.setHasOtherFile(1);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(solveRecordsAdminHandleVo)
                .build();
    }

    @ApiOperation(value = "根据uid获取解题记录")
    @GetMapping("/queryByUserId")
    public Response queryByUserId(@RequestParam("uid") Integer uid){
        Users user = usersService.queryById(uid);
        if (user == null) {
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        List<SolveRecordsAdminUidVo> solveRecordsAdminUidVoList = new LinkedList<>();
        Domains domains = new Domains();
        List<Domains> domainsList = domainsService.queryAll(domains);
        for (Domains d : domainsList){
            SolveRecordsAdminUidVo solveRecordsAdminUidVo = new SolveRecordsAdminUidVo();
            solveRecordsAdminUidVo.setDomain(Dozer.convert(d, DomainsOpVo.class));
            WhereRecords whereRecords = new WhereRecords();
            whereRecords.setDomainId(d.getId());
            whereRecords.setUserId(uid);
            List<SolveRecords> solveRecordsList=solveRecordsService.queryAll(whereRecords);
            List<SolveRecordsAdminHandleVo> solveRecordsResult = new LinkedList<>();
            for (SolveRecords s : solveRecordsList) {
                SolveRecordsAdminHandleVo solveRecord = Dozer.convert(s, SolveRecordsAdminHandleVo.class);
                Problems problems = problemsService.queryById(s.getProblemId());
                ProblemsAdminHandlerVo problemsAdminHandlerVo = Dozer.convert(problems,ProblemsAdminHandlerVo.class);
                if (problemsAdminHandlerVo != null && problems.getFirstSolverId() != null) {
                        Users first= usersService.queryById(problems.getFirstSolverId());
                        problemsAdminHandlerVo.setFirstUser(Dozer.convert(first,UsersAdminVo.class));
                }
                solveRecord.setProblem(problemsAdminHandlerVo);
                if (problems.getCurrentScore() != null && s.getDegree() != null) {
                    solveRecord.setScore(Math.round(problems.getCurrentScore() * s.getDegree()));
                } else {
                    solveRecord.setScore(0);
                }
                if (s.getHandlerId() != null){
                    Admins admins = adminsService.queryById(s.getHandlerId());
                    solveRecord.setHandler(Dozer.convert(admins,AdminsAdminVo.class));
                }

                if (Objects.equals(problems.getSubmitType(),"flag")) {
                    solveRecordsResult.add(solveRecord);
                    continue;
                }
                String pdfPath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",localPath,s.getUserId(),user.getUsername()
                ,problems.getId(),s.getUserId(),user.getUsername(),problems.getId());
                String basePath=String.format("%s/%s_%s/%s",localPath,user.getId(),user.getUsername(),problems.getId());
                String pre=String.format("%s_%s_%s_appendix",user.getId(),user.getUsername(),problems.getId());
                File baseFile=new File(basePath);
                File[] fileList = baseFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return new File(dir,name).isFile() && name.startsWith(pre);
                    }
                });
                if (fileList.length > 1){ //多附件
                    log.error("ERROR:记录{},存在多附件", s.getId());
                }
                File file=new File(pdfPath);
                if (file.exists() && file.isFile()) {
                    solveRecord.setHasPdf(1);
                }
                if (fileList.length>0){
                    String  filePath=String.format("%s/%s_%s/%s/%s",localPath,user.getId(),
                            user.getUsername(),problems.getId(),fileList[0].getName());
                    file=new File(filePath);
                    if (file.exists() && file.isFile()) solveRecord.setHasOtherFile(1);
                }
                solveRecordsResult.add(solveRecord);
            }
            solveRecordsAdminUidVo.setRecords(solveRecordsResult);
            solveRecordsAdminUidVoList.add(solveRecordsAdminUidVo);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(solveRecordsAdminUidVoList)
                .build();
    }

    @ApiOperation(value = "分页查询解题记录",notes = "pageSize 页面大小不能超过50, current 选择页从1开始,不填方向id则返回所有方向的解题记录")
    @GetMapping("/page")
    public Response getSolveRecordsPage(@RequestParam("pageSize") Integer pageSize,
                                        @RequestParam("current") Integer current,
                                        @RequestParam(value = "domainId",required = false) Integer domainId){
        if (pageSize > 50 || pageSize <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_PAGESIZE)
                    .build();
        }
        if (current <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_CURRENT)
                    .build();
        }
        if (domainId!=null && !domainsService.exists(domainId)){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAINS_DATA_NOT_FOUND)
                    .build();
        }
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setDomainId(domainId);
        PageHelper.startPage(current,pageSize);
        List<SolveRecords> solveRecordsList=solveRecordsService.queryAll(whereRecords);
        PageInfo<SolveRecords> pageInfo=new PageInfo<>(solveRecordsList);
        List<SolveRecordsAdminDetailVo> solveRecordsAdminDetailVoList= Dozer.convert(pageInfo.getList(),SolveRecordsAdminDetailVo.class);
        for (SolveRecordsAdminDetailVo e:solveRecordsAdminDetailVoList){
            Users users=usersService.queryById(e.getUserId());
            Admins admins=adminsService.queryById(e.getHandlerId());
            Problems problem=problemsService.queryById(e.getProblemId());
            e.setUser(Dozer.convert(users, UsersAdminVo.class));
            e.setHandler(Dozer.convert(admins, AdminsAdminVo.class));
            if (problem != null && problem.getCurrentScore()!=null){
                e.setCurrentScore(Math.round(e.getDegree()*problem.getCurrentScore()));
            }else {
                e.setCurrentScore(0);
            }
            if (problem!= null){
                e.setProblem(Dozer.convert(problem,ProblemsAdminHandlerVo.class));
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(solveRecordsAdminDetailVoList)
                .len((int)pageInfo.getTotal())
                .build();
    }

    @ApiOperation(value = "分页查询单道题解题记录",notes = "pageSize 页面大小不能超过50, current 选择页从1开始")
    @GetMapping("/singleProblemPage")
    public Response getSingleProblemSolveRecordsPage(@RequestParam("pageSize") Integer pageSize,
                                                    @RequestParam("current") Integer current,
                                                    @RequestParam(value = "problemId") Integer problemId){
        if (pageSize > 50 || pageSize <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_PAGESIZE)
                    .build();
        }
        if (current <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_CURRENT)
                    .build();
        }
        Problems problem=problemsService.queryById(problemId);
        if (problem == null){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setProblemId(problemId);
        PageHelper.startPage(current,pageSize);
        List<SolveRecords> solveRecordsList=solveRecordsService.queryAll(whereRecords);
        PageInfo<SolveRecords> pageInfo=new PageInfo<>(solveRecordsList);
        List<SolveRecordsAdminSingleProblemCardsVo> solveRecordsAdminSingleProblemCardsVoList=Dozer.convert(pageInfo.getList(),SolveRecordsAdminSingleProblemCardsVo.class);
        for(SolveRecordsAdminSingleProblemCardsVo e:solveRecordsAdminSingleProblemCardsVoList){
            Users users=usersService.queryById(e.getUserId());
            Admins admins=adminsService.queryById(e.getHandlerId());
            e.setUser(Dozer.convert(users, UsersAdminVo.class));
            e.setHandler(Dozer.convert(admins, AdminsAdminVo.class));
            if (problem.getCurrentScore()!=null){
                e.setCurrentScore(Math.round(e.getDegree()*problem.getCurrentScore()));
            }else {
                e.setCurrentScore(0);
            }
            e.setProblem(Dozer.convert(problem,ProblemsAdminHandlerVo.class));
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(solveRecordsAdminSingleProblemCardsVoList)
                .len((int)pageInfo.getTotal())
                .build();
    }
}


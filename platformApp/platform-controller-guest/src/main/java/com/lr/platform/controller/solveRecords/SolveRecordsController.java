package com.lr.platform.controller.solveRecords;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsGuestVo;
import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.problems.ProblemsGuestCardsVo;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.SolveRecordsGuestDetailVo;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.DomainsService;
import com.lr.platform.service.ProblemsService;
import com.lr.platform.service.SolveRecordsService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    private HttpServletRequest httpServletRequest;

    @Resource
    private AdminsService adminsService;

    @Resource
    private DomainsService domainsService;

    @Resource
    private ProblemsService problemsService;

    @Value("${data.problems.local.path}")
    private String localPath;

    @ApiOperation(value = "获取个人解题记录")
    @GetMapping("/mySolveRecords")
    public Response selectMySolveRecords(@RequestParam("pageSize") Integer pageSize,
                                         @RequestParam("current") Integer current,
                                         @RequestParam("domainId") Integer domainId) {
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
        String token = httpServletRequest.getHeader("token");
        JWTClaims jwtClaims = JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        WhereRecords whereRecords = new WhereRecords();
        whereRecords.setUserId(jwtClaims.getUid());
        whereRecords.setDomainId(domainId);
        PageHelper.startPage(current,pageSize);
        List<SolveRecords> solveRecordsList = solveRecordsService.queryAll(whereRecords);
        PageInfo<SolveRecords> pageInfo=new PageInfo<>(solveRecordsList);
        List<SolveRecordsGuestDetailVo> solveRecordsGuestDetailVos = Dozer.convert(pageInfo.getList(), SolveRecordsGuestDetailVo.class);
        HashMap<Integer,DomainsOpVo> domainMap= new HashMap<>();
        for (SolveRecordsGuestDetailVo i:solveRecordsGuestDetailVos) {
            //Domains domains = domainsService.queryById(i.getDomainId());
            //i.setDomain(Dozer.convert(domains, DomainsOpVo.class));
            Integer status = i.getStatus();
            Problems problem=problemsService.queryById(i.getProblemId());
            ProblemsGuestCardsVo problemsGuestCardsVo=Dozer.convert(problem, ProblemsGuestCardsVo.class);
            i.setProblem(problemsGuestCardsVo);
            if (problem!=null && problem.getCurrentScore()!=null){
                i.setCurrentScore(Math.round(i.getDegree()*problem.getCurrentScore()));
            }else {
                i.setCurrentScore(0);
            }
            if (problem!=null && problem.getDomainId()!=null){
                DomainsOpVo domainsOpVo=domainMap.get(problem.getDomainId());
                if (domainsOpVo==null){
                    Domains domains=domainsService.queryById(problem.getDomainId());
                    domainsOpVo=Dozer.convert(domains,DomainsOpVo.class);
                    domainMap.put(problem.getDomainId(),domainsOpVo);
                }
                i.setDomainId(problem.getDomainId());
                i.setDomain(domainsOpVo);
            }
            if (status == 2 || status == 3) {
                Admins admins=adminsService.queryById(i.getHandlerId());
                i.setHandler(Dozer.convert(admins, AdminsGuestVo.class));
            }
            if (Objects.equals(problem.getSubmitType(), "flag"))continue;
            String pdfPath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",localPath,i.getUserId(),jwtClaims.getUsername()
                    ,i.getProblemId(),i.getUserId(),jwtClaims.getUsername(),i.getProblemId());
            String basePath=String.format("%s/%s_%s/%s",localPath,i.getUserId(),jwtClaims.getUsername(),i.getProblemId());
            String pre=String.format("%s_%s_%s_appendix",i.getUserId(),jwtClaims.getUsername(),i.getProblemId());
            File baseFile=new File(basePath);
            File[] fileList = baseFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir,name).isFile() && name.startsWith(pre);
                }
            });
            if (fileList.length > 1){ //多附件
                log.error("ERROR:记录{},存在多附件",i.getId());
            }
            File file=new File(pdfPath);
            if (file.exists() && file.isFile())i.setHasPdf(1);
            if (fileList.length>0){
                String  filePath=String.format("%s/%s_%s/%s/%s",localPath,i.getUserId(),
                        jwtClaims.getUsername(),i.getProblemId(),fileList[0].getName());
                file=new File(filePath);
                if (file.exists() && file.isFile())i.setHasOtherFile(1);
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(solveRecordsGuestDetailVos)
                .len((int) pageInfo.getTotal())
                .build();
    }
}

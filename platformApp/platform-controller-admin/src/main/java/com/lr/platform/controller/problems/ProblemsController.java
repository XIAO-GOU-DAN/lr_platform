package com.lr.platform.controller.problems;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.problems.ProblemsAdminCardsVo;
import com.lr.platform.entity.problems.ProblemsAdminVo;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.LoginException;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import com.lr.platform.service.DomainsService;
import com.lr.platform.service.ProblemsService;
import com.lr.platform.service.SolveRecordsService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * (Problems)表控制层
 *
 * @author makejava
 * @since 2022-07-02 00:09:19
 */
@Api(tags = "题目模块")
@RestController
@RequestMapping("problems")
public class ProblemsController {
    /**
     * 服务对象
     */
    @Resource
    private ProblemsService problemsService;

    @Resource
    private DomainsService domainsService;

    @Resource
    private SolveRecordsService solveRecordsService;

    @Value("${data.problems.web.path}")
    private String webPath;

    @Value("${data.problems.local.path}")
    private String localPath;

    @Resource
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "新增题目")
    @ManagerLog(tag = "新增题目",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/create")
    public Response createProblems(@RequestBody ProblemsAdminVo ProblemsAdminVo){
        String title = ProblemsAdminVo.getTitle();
        if (Objects.equals(title, null) || Objects.equals(title.length(), 0)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_TITLE)
                    .build();
        }
        String content = ProblemsAdminVo.getContent();
        if (Objects.equals(content, null) || Objects.equals(content.length(), 0)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_CONTENT)
                    .build();
        }
        Integer isShow=ProblemsAdminVo.getIsShow();
        if (Objects.equals(isShow,1) && Objects.equals(isShow,0)){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SHOW_OPTION)
                    .build();
        }
        String submitType = ProblemsAdminVo.getSubmitType();
        if (!Objects.equals("flag",submitType) && !Objects.equals("upload",submitType)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SUBMIT_TYPE)
                    .build();
        }
        String flag = ProblemsAdminVo.getFlag();
        if (submitType.equals("flag") && !Validator.isValidFlag(flag)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_FLAG)
                    .build();
        }
        if (submitType.equals("upload")){
            ProblemsAdminVo.setFlag(null);
        }
        Integer originalScore = ProblemsAdminVo.getOriginalScore();
        if (Objects.equals(originalScore, null) || originalScore>5000 || originalScore<0) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SCORE)
                    .build();
        }
        if (!domainsService.exists(ProblemsAdminVo.getDomainId())) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        ProblemsAdminVo.setCurrentScore(originalScore);
        Problems problems = Dozer.convert(ProblemsAdminVo, Problems.class);
        problems.setFirstSolveTime(null);
        problems.setFirstSolverId(null);
        problems.setSolverNum(0);
        problems.setRevisedAt(new Date());
        problemsService.insert(problems);
        return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
    }

    @ApiOperation(value = "修改题目")
    @ManagerLog(tag = "修改题目",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateProblems(@RequestBody ProblemsAdminVo problemsAdminVo){
        String title = problemsAdminVo.getTitle();
        if (Objects.equals(title, null) || Objects.equals(title.length(), 0)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_TITLE)
                    .build();
        }
        String content = problemsAdminVo.getContent();
        if (Objects.equals(content, null) || Objects.equals(content.length(), 0)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_CONTENT)
                    .build();
        }
        Integer isShow= problemsAdminVo.getIsShow();
        if (Objects.equals(isShow,1) && Objects.equals(isShow,0)){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SHOW_OPTION)
                    .build();
        }
        String submitType = problemsAdminVo.getSubmitType();
        if (!Objects.equals("flag",submitType) && !Objects.equals("upload",submitType)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SUBMIT_TYPE)
                    .build();
        }
        String flag = problemsAdminVo.getFlag();
        if (submitType.equals("flag") && !Validator.isValidFlag(flag)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_FLAG)
                    .build();
        }
        if (submitType.equals("upload")){
            problemsAdminVo.setFlag(null);
        }
        Integer originalScore = problemsAdminVo.getOriginalScore();
        if (Objects.equals(originalScore, null) || originalScore>5000 || originalScore<0) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SCORE)
                    .build();
        }
        if (!problemsService.exists(problemsAdminVo.getId())) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (!domainsService.exists(problemsAdminVo.getDomainId())) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        Problems problems = Dozer.convert(problemsAdminVo, Problems.class);
        problems.setDomainId(null);
        problems.setFirstSolverId(null);
        problems.setFirstSolveTime(null);
        problems.setSolverNum(null);
        problems.setDeletedAt(null);
        problems.setCurrentScore(originalScore);
        problems.setRevisedAt(new Date());
        problemsService.update(problems);
        return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
    }

    @ApiOperation(value = "删除题目")
    @ManagerLog(tag = "删除题目",level = WarningLevel.COMMON_DELETE)
    @GetMapping("/delete")
    public Response deleteProblems(@RequestParam("id") Integer id){
        Problems problems = problemsService.queryById(id);
        if (problems==null) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        problems.setDeletedAt(new Date());
        problemsService.update(problems);
        SolveRecords setSolveRecords=new SolveRecords();
        setSolveRecords.setDeletedAt(new Date());
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setProblemId(id);
        solveRecordsService.updateBySolveRecords(setSolveRecords,whereRecords);
        return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
    }

    @ApiOperation(value = "分页获取题目卡片",notes = "暂时只返回卡片所需的字段")
    @GetMapping("/page")
    public Response selectProblemsPage(@RequestParam("pageSize") Integer pageSize,
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
        if (domainId!=null && !domainsService.exists(domainId)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        Problems problems=new Problems();
        problems.setDomainId(domainId);
        PageHelper.startPage(current,pageSize);
        List<Problems> problemsList=problemsService.queryAll(problems);
        PageInfo<Problems> pageInfo = new PageInfo<>(problemsList);
        List<ProblemsAdminCardsVo> problemsAdminCardsVoList=Dozer.convert(pageInfo.getList(),ProblemsAdminCardsVo.class);
        SolveRecords solveRecords=new SolveRecords();
        for(ProblemsAdminCardsVo e:problemsAdminCardsVoList){
            solveRecords.setProblemId(e.getId());
            //e.setTotalCommit(solveRecordsService.count(solveRecords));
            //System.out.println(e.getTotalCommit());
            solveRecords.setStatus(1);
            e.setNewCommitCount(solveRecordsService.count(solveRecords));
            solveRecords.setStatus(2);
            e.setIsReadCount(solveRecordsService.count(solveRecords));
            solveRecords.setStatus(null);
            e.setTotalCommit(solveRecordsService.count(solveRecords));
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len((int) pageInfo.getTotal())
                .data(problemsAdminCardsVoList)
                .build();
    }

    @ApiOperation(value = "查询单道题目")
    @GetMapping("/query")
    public Response queryProblems(@RequestParam("id") Integer id) {
        Problems problems = problemsService.queryById(id);
        if (Objects.equals(problems, null)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        ProblemsAdminVo problemsVo = Dozer.convert(problems, ProblemsAdminVo.class);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(problemsVo)
                .build();
    }

    @ApiOperation(value = "改变题目显示状态",notes = "isShow 为1 显示, 0不显示")
    @PostMapping("/showProblem")
    public Response showProblem(@RequestParam("id") Integer id,
                                @RequestParam("isShow") Integer isShow){
        Problems problems=problemsService.queryById(id);
        if (Objects.equals(problems, null)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (isShow!=1 && isShow!=0){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SHOW_OPTION)
                    .build();
        }
        problems.setIsShow(isShow);
        problems.setRevisedAt(new Date());
        problemsService.update(problems);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "批阅题目",notes = "返回下载链接及下载凭据,凭据放url query['auth-file-token'] isPdf: 1 pdf预览 0 附件")
    @PostMapping ("/handleUpload")
    public Response handleUpload(@RequestParam("problemsId") Integer problemsId,
                                 @RequestParam("userId") Integer userId,
                                 @RequestParam("username") String username,
                                 @RequestParam("solveRecordsId") Integer solveRecordsId,
                                 @RequestParam("isPdf") Integer isPdf){
        // nginx ver token -> ver path -> download
        String filePath;
        if (isPdf==1){
            filePath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",localPath,userId,username,problemsId,userId,username,problemsId);
        }else {
            String basePath=String.format("%s/%s_%s/%s",localPath,userId,username,problemsId);
            String pre=String.format("%s_%s_%s_appendix",userId,username,problemsId);
            File baseFile=new File(basePath);
            File[] fileList = baseFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir,name).isFile() && name.startsWith(pre);
                }
            });
            if (fileList.length > 1){ //多附件
                return Response
                        .builder()
                        .code(ResponseCode.SOLVE_RECORDS_FILE_ERROR)
                        .msg("数据异常,请通知管理员")
                        .build();
            }
            filePath=String.format("%s/%s_%s/%s/%s",localPath,userId,username,problemsId,fileList[0].getName());
        }
        File file=new File(filePath);
        if (!file.exists() || !file.isFile()){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_FILE_NOT_FOUND)
                    .msg("文件不存在")
                    .build();
        }
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        SolveRecords solveRecords=new SolveRecords();
        solveRecords.setStatus(2);
        solveRecords.setId(solveRecordsId);
        solveRecords.setHandlerId(jwtClaims.getUid());
        int res=solveRecordsService.handleUpload(solveRecords);
        if (res <= 1 ){
            String path=filePath.replaceAll("^(/jar/data/)(.*)$","/download/$2");
            String fileToken= JWTAuth.releaseFileToken(30,jwtClaims.getUsername(),-1,"download", jwtClaims.getUid(), path);
            HashMap<String,Object> data=new HashMap<>();
            data.put("token",fileToken);
            data.put("url","https://static.lingrui.online"+path);
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .data(data)
                    .build();
        }
        throw new RollBackException("发现重复项: solveRecords "+solveRecordsId+"重复"+res);
    }

    @ApiOperation(value = "评分",notes = "")
    @ManagerLog(tag = "打分",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/score")
    public Response score(@RequestParam("solveRecordsId") Integer solveRecordsId,
                          @RequestParam("degree") Float degree){
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        SolveRecords solveRecords=solveRecordsService.queryById(solveRecordsId);
        if (solveRecords==null){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_NOT_FOUND)
                    .build();
        }
        Problems problems=problemsService.queryById(solveRecords.getProblemId());
        if (problems==null){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (Objects.equals(problems.getSubmitType(),"flag")){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PERMISSION_DENIED)
                    .msg("此题不允许手动评分")
                    .build();
        }
        if (degree>2 || degree<0){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_ILLEGAL_DEGREE)
                    .msg("分数超出范围")
                    .build();
        }
        solveRecords.setId(solveRecordsId);
        solveRecords.setStatus(3);
        solveRecords.setDegree((float) Double.parseDouble(String.format("%.2f",degree)));
        solveRecords.setHandlerId(jwtClaims.getUid());
        int res = solveRecordsService.handleUpload(solveRecords);
        if (res == 0){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_NOT_FOUND)
                    .build();
        }
        if (res > 1){
            throw new RollBackException("发现重复项: solveRecords:"+solveRecordsId+"重复:"+res);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }
}


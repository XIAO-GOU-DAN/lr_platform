package com.lr.platform.controller.problems;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.GuestLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.problems.ProblemsGuestCardsVo;
import com.lr.platform.entity.problems.ProblemsGuestVo;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.SolveRecordsGuestForProblemsCardVo;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (Problems)表控制层
 *
 * @author makejava
 * @since 2022-07-07 13:13:36
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

    @Value("${data.problems.local.path}")
    private String savePath;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${data.problems.local.path}")
    private String localPath;

    @ApiOperation(value = "分页获取方向题目")
    @GetMapping("/page")
    public Response selectProblemsPage(@RequestParam("pageSize") Integer pageSize,
                                       @RequestParam("current") Integer current,
                                       @RequestParam("domainId") Integer domainId){
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
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        if (Objects.equals(domainId, null) || !domainsService.exists(domainId)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        Problems problems=new Problems();
        problems.setIsShow(1);
        problems.setDomainId(domainId);
        PageHelper.startPage(current,pageSize);
        List<Problems> problemsList=problemsService.queryAll(problems);
        PageInfo<Problems> pageInfo = new PageInfo<>(problemsList);
        List<ProblemsGuestCardsVo> problemsGuestCardsVos=Dozer.convert(pageInfo.getList(),ProblemsGuestCardsVo.class);
        for (ProblemsGuestCardsVo p :problemsGuestCardsVos){
            WhereRecords whereRecords=new WhereRecords();
            whereRecords.setProblemId(p.getId());
            whereRecords.setUserId(jwtClaims.getUid());
            SolveRecords solveRecords=solveRecordsService.queryBySolveRecords(whereRecords);
            SolveRecordsGuestForProblemsCardVo solveRecordsGuestForProblemsCardVo=Dozer.convert(solveRecords,SolveRecordsGuestForProblemsCardVo.class);
            if (solveRecordsGuestForProblemsCardVo!=null){
                if (solveRecordsGuestForProblemsCardVo.getDegree()!=null && p.getCurrentScore()!=null){
                    solveRecordsGuestForProblemsCardVo.setScore(Math.round(solveRecordsGuestForProblemsCardVo.getDegree()*p.getCurrentScore()));
                }
                p.setSolveRecord(solveRecordsGuestForProblemsCardVo);
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len((int) pageInfo.getTotal())
                .data(problemsGuestCardsVos)
                .build();
    }

    @ApiOperation(value = "获取单题具体内容")
    @GetMapping
    public Response selectSingleProblem(@RequestParam("id") Integer id){
        Problems problems = problemsService.queryById(id);
        if (Objects.equals(problems, null) || !Objects.equals(problems.getIsShow(),1)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        ProblemsGuestVo problemsGuestVo = Dozer.convert(problems, ProblemsGuestVo.class);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(problemsGuestVo)
                .build();
    }

    @ApiOperation(value = "上传文件",notes = "id为题目号,pdf以及附件 分两次调用接口来上传 pdf:0 1 ")
    @GuestLog(tag = "提交题目(上传)",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/handleUpload")
    public Response upload(@RequestParam("file") MultipartFile multipartFile,
                           @RequestParam("id") Integer id,
                           @RequestParam("isPdf") Integer isPdf) throws IOException {
        if (multipartFile.isEmpty()){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_FAIL)
                    .msg("?")
                    .build();
        }
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        Problems problems = problemsService.queryById(id);
        if (Objects.equals(problems, null) || !Objects.equals(problems.getIsShow(),1)) {
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (problems.getDeadline() != null && problems.getDeadline().getTime()<new Date().getTime()){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_SUBMIT_OVER_TIME)
                    .build();
        }
        String fileName= multipartFile.getOriginalFilename();
        String type=Validator.getSuffix(fileName);
        String filePath;
        if (type.substring(type.lastIndexOf(".")+1).equalsIgnoreCase("pdf") && isPdf==1) {
            //是 pdf
            //  /jar/data/x_xxxx/id/x_xxxx_id_pdf.pdf
            filePath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",savePath,jwtClaims.getUid(),jwtClaims.getUsername(),
                    id,jwtClaims.getUid(),jwtClaims.getUsername(),id);
            //filePath=savePath+"/"+jwtClaims.getUid()+"_"+jwtClaims.getUsername()+"/"+id+"/"+
              //      jwtClaims.getUid()+"_"+jwtClaims.getUsername()+"_"+id+".pdf";
        }else if (isPdf==1){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_WRONG_FILE_TYPE)
                    .build();
        }else {
            //不是 pdf
            //  /jar/data/x_xxxx/id/x_xxxx_id_appendix.xxx
            filePath=String.format("%s/%s_%s/%s/%s_%s_%s_appendix%s",savePath,jwtClaims.getUid(),jwtClaims.getUsername(),
                    id,jwtClaims.getUid(),jwtClaims.getUsername(),id,type);
            //filePath = savePath + "/" + jwtClaims.getUid() + "_" + jwtClaims.getUsername() + "/" + id + "/" +
                              // jwtClaims.getUid() + "_" + jwtClaims.getUsername() + "_" + id + "_appendix" + type;
        }
        File file=new File(filePath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);//写入文件
        //删除旧文件 为附件时, pdf直接覆盖
        if (isPdf==0){
            String basePath=String.format("%s/%s_%s/%s",savePath,jwtClaims.getUid(),jwtClaims.getUsername(),id);
            File baseFile=new File(basePath);
            String pre=String.format("%s_%s_%s_appendix",jwtClaims.getUid(),jwtClaims.getUsername(),id);
            File[] oldList=baseFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(pre) && !Objects.equals(name,pre+type);
                }
            });
            for (File e:oldList){//删除老文件
                if (e.isFile()){
                    e.delete();
                }
            }
        }
        //解题记录
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setProblemId(id);
        whereRecords.setUserId(jwtClaims.getUid());
        SolveRecords rawSolveRecords=solveRecordsService.queryBySolveRecords(whereRecords);
        SolveRecords solveRecords=new SolveRecords();
        if (rawSolveRecords == null){
            solveRecords.setProblemId(id);
            solveRecords.setUserId(jwtClaims.getUid());
            solveRecords.setDegree(0f);
            solveRecords.setStatus(1);
            solveRecords.setHandlerId(null);
            solveRecords.setSubmitTimes(1);
            solveRecords.setLatestSubmissionTime(new Date());
            int res= solveRecordsService.register(solveRecords);
            if (res != 1){//发现重复
                solveRecords=solveRecordsService.queryBySolveRecords(whereRecords);
                if (solveRecords==null) throw new RollBackException("数据异常: problemId:"+id+",uid:"+jwtClaims.getUid());
                SolveRecords newSolveRecords=new SolveRecords();
                newSolveRecords.setStatus(1);
                //newSolveRecords.setHandlerId(null);
                newSolveRecords.setId(solveRecords.getId());
                solveRecordsService.updateSolveRecordsAddOneToSubmitTimes(newSolveRecords);
            }
        }else {
            solveRecords.setId(rawSolveRecords.getId());
            solveRecords.setStatus(1);
            //solveRecords.setHandlerId(null);
            solveRecordsService.updateSolveRecordsAddOneToSubmitTimes(solveRecords);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "提交flag",notes = "id:题目id,flag:flag")
    @GuestLog(tag = "提交题目(flag)",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/handleFlag")
    public Response handleFlag(@RequestParam("id") Integer id,
                               @RequestParam("flag") String flag){
        if (!Validator.isValidFlag(flag)){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_WRONG_FLAG)
                    .build();
        }
        Problems problems=problemsService.queryById(id);
        if (problems==null || !Objects.equals(problems.getIsShow(),1)){//题目不存在或未显示
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (problems.getDeadline() != null && problems.getDeadline().getTime()<new Date().getTime()){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_SUBMIT_OVER_TIME)
                    .build();
        }
        if (!Objects.equals(problems.getSubmitType(),"flag")){//不是flag题
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ILLEGAL_SUBMIT_TYPE)
                    .build();
        }
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setProblemId(id);
        whereRecords.setUserId(jwtClaims.getUid());
        SolveRecords rawSolveRecords=solveRecordsService.queryBySolveRecords(whereRecords);
        if (rawSolveRecords!=null && rawSolveRecords.getDegree()==1){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_ALREADY_FINISH_FLAG)
                    .build();
        }
        float flagRes;
        if (Objects.equals(flag,problems.getFlag())){
            flagRes=1f;
        }else {
            flagRes=0f;
        }
        SolveRecords solveRecords=new SolveRecords();
        //solveRecords.setDegree(1f);
        solveRecords.setStatus(3);//已处理完成
        solveRecords.setHandlerId(1);//初始管理员账号
        solveRecords.setDegree(flagRes);
        if (rawSolveRecords == null){
            solveRecords.setProblemId(id);
            solveRecords.setUserId(jwtClaims.getUid());
            solveRecords.setSubmitTimes(1);
            solveRecords.setLatestSubmissionTime(new Date());
            int res= solveRecordsService.register(solveRecords);
            if (res != 1){//发现重复
                rawSolveRecords =solveRecordsService.queryBySolveRecords(whereRecords);
                if (rawSolveRecords==null) throw new RollBackException("数据异常: problemId:"+id+",uid:"+jwtClaims.getUid());
                if (rawSolveRecords.getDegree()==1){//已完成
                    return Response
                            .builder()
                            .code(ResponseCode.PROBLEMS_ALREADY_FINISH_FLAG)
                            .build();
                }
                //未完成
                solveRecords.setDegree(flagRes);
                solveRecords.setId(rawSolveRecords.getId());
                solveRecordsService.updateSolveRecordsAddOneToSubmitTimes(solveRecords);
            }
        }else {
            solveRecords.setId(rawSolveRecords.getId());
            solveRecordsService.updateSolveRecordsAddOneToSubmitTimes(solveRecords);
        }
        if (Objects.equals(flag,problems.getFlag())){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .msg("提交成功")
                    .build();
        }
        return Response
                .builder()
                .code(ResponseCode.PROBLEMS_WRONG_FLAG)
                .build();
    }

    @ApiOperation(value = "下载文件")
    @GetMapping("/downloadFile")
    public Response downloadFile(@RequestParam("problemsId") Integer problemsId,
                                 @RequestParam("isPdf") Integer isPdf){
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        Problems problems=problemsService.queryById(problemsId);
        if (problems==null || !Objects.equals(problems.getIsShow(),1)){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (Objects.equals(problems.getSubmitType(), "flag")){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_FILE_NOT_FOUND)
                    .msg("此题无附件")
                    .build();
        }
        String filePath;
        if (isPdf==1){
            filePath=String.format("%s/%s_%s/%s/%s_%s_%s.pdf",localPath,
                    jwtClaims.getUid(),jwtClaims.getUsername(),
                    problemsId,jwtClaims.getUid(),jwtClaims.getUsername()
                    ,problemsId);
        }else {
            String basePath=String.format("%s/%s_%s/%s",localPath,jwtClaims.getUid(),
                    jwtClaims.getUsername(),problemsId);
            String pre=String.format("%s_%s_%s_appendix",jwtClaims.getUid(),jwtClaims.getUsername(),problemsId);
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
            filePath=String.format("%s/%s_%s/%s/%s",localPath,jwtClaims.getUid()
                    ,jwtClaims.getUsername(),problemsId,fileList[0].getName());
        }
        File file=new File(filePath);
        if (!file.exists() || !file.isFile()){
            return Response
                    .builder()
                    .code(ResponseCode.SOLVE_RECORDS_FILE_NOT_FOUND)
                    .msg("文件不存在")
                    .build();
        }
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
}



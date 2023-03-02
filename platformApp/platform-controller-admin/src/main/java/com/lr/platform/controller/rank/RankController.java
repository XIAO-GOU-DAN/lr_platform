package com.lr.platform.controller.rank;

import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.rank.DomainRankGuestVo;
import com.lr.platform.entity.rank.SingleProblemRankAdminVo;
import com.lr.platform.entity.rank.TotalRankAdminVo;
import com.lr.platform.entity.users.Users;
import com.lr.platform.entity.users.UsersAdminVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.response.Response;
import com.lr.platform.service.DomainsService;
import com.lr.platform.service.ProblemsService;
import com.lr.platform.service.UsersService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.Redis;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = "排行榜模块")
@RestController
@RequestMapping("rank")
public class RankController {

    @Resource
    private UsersService usersService;

    @Resource
    private DomainsService domainsService;

    @Resource
    private ProblemsService problemsService;

    @ApiOperation(value = "获取排行榜",notes = "单题榜 总榜, 题目id和方向id同时发送默认返回对应单题榜,不填返回总榜, 同时附带用户各方向榜的排名以及解出时间,填方向id 表示按某方向排序,,单题榜附带题目所属的方向,返回一血(未来加上),grade : 1 屏蔽所有未填学号的用户 其他数字 20xx 返回具体年级的排行")
    @GetMapping("/page/total")
    public Response getPage(@RequestParam("pageSize") Integer pageSize,
                            @RequestParam("current") Integer current,
                            @RequestParam(value = "domainId",required = false) Integer domainId,
                            @RequestParam(value = "isShow",required = false) Integer isShow,
                            @RequestParam(value = "grade",required = false) Integer grade){
        // 查询 redis
        //zrange key (current-1)*pageSize current*pageSize-1 withscores
        String sub="-1";
        if (Objects.equals(isShow,0)){
            sub="-0";
        }
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
        if (grade == null)grade=0;
        Long start=  (current-1)*pageSize.longValue();
        Long stop= current*pageSize.longValue()-1;
        String scoreName;
        String numName;
        Domains domains=null;
        if (domainId != null){
            domains = domainsService.queryById(domainId);
            if (domains == null){
                return Response
                        .builder()
                        .code(ResponseCode.DOMAINS_DATA_NOT_FOUND)
                        .build();
            }
            scoreName="guest:DomainSolveScoreRank:"+grade+"-"+domainId+sub;
            numName="guest:DomainSolveNumRank:"+grade+"-"+domainId+sub;
        }else {
            scoreName="guest:TotalSolveScoreRank:"+grade+"-0"+sub;
            numName="guest:TotalSolveNumRank:"+grade+"-0"+sub;
        }
        boolean success = Objects.equals(Redis.exists(scoreName),true) && Objects.equals(Redis.exists(numName),true);
        if (!success){
            return Response
                    .builder()
                    .code(ResponseCode.RANK_NOT_DATA)
                    .msg("暂时没有数据,请再接再厉")
                    .build();
        }
        List<TotalRankAdminVo> totalRankGuestVoList = new LinkedList<>();
        Set<Tuple> rankSet=Redis.zrevrangeWithScores(scoreName,start,stop);
        Long len = Redis.zcard(scoreName);
        //int id=(current-1)*pageSize+1;
        //int id = 1;
        for (Tuple t:rankSet){
            List<DomainRankGuestVo> domainRankGuestVoList = new LinkedList<>();
            TotalRankAdminVo totalRankGuestVo = new TotalRankAdminVo();
            Users users = usersService.queryById(Integer.parseInt(t.getElement()));
            if (users == null) continue;
            Double redisScore = Redis.zscore(scoreName,t.getElement());
            if (redisScore != null){
                redisScore = Double.parseDouble(String.format("%.16f",redisScore));
                double rawSc=Math.floor(redisScore*Math.pow(10,6));
                if (domainId==null){
                    totalRankGuestVo.setScore(rawSc);
                    totalRankGuestVo.setSolveNum(Redis.zscore(numName,t.getElement()));
                }else {
                    double secSc=redisScore*Math.pow(10,6)-rawSc;
                    secSc = Math.round(secSc*Math.pow(10,10));
                    totalRankGuestVo.setScore(secSc);
                    totalRankGuestVo.setSolveNum(Redis.zscore("guest:TotalSolveNumRank:"+grade+"-0"+sub,t.getElement()));
                }
            }else {
                if (domainId== null){
                    totalRankGuestVo.setScore(0.);
                    totalRankGuestVo.setSolveNum(0.);
                }else {
                    redisScore =Redis.zscore("guest:TotalSolveScoreRank:"+grade+"-0"+sub,t.getElement());
                    if (redisScore == null){
                        totalRankGuestVo.setScore(0.);
                        totalRankGuestVo.setSolveNum(0.);
                    }else {
                        double rawSc=Math.floor(redisScore*Math.pow(10,6));
                        totalRankGuestVo.setScore(rawSc);
                        totalRankGuestVo.setSolveNum(Redis.zscore("guest:TotalSolveNumRank:"+grade+"-0"+sub,t.getElement()));
                    }
                }
            }
            Domains newDomains = new Domains();
            List<Domains> domainsList = domainsService.queryAll(newDomains);
            Long redisRank;
            for (Domains d : domainsList){
                DomainRankGuestVo domainRankGuestVo = new DomainRankGuestVo();
                //if (Objects.equals(d.getId(), domainId)) continue;
                redisScore = Redis.zscore("guest:DomainSolveScoreRank:"+grade+"-"+d.getId()+sub,t.getElement());
                redisRank = Redis.zrank("guest:DomainSolveScoreRank:"+grade+"-"+d.getId()+sub, t.getElement());
                Long redisCard = Redis.zcard("guest:DomainSolveScoreRank:"+grade+"-"+d.getId()+sub);
                if (redisRank==null || redisCard == null){
                    domainRankGuestVo.setId(null);
                }else{
                    domainRankGuestVo.setId((int)(redisCard - redisRank));
                }
                if (redisScore == null || redisScore == 0){
                    domainRankGuestVo.setScore(0.);
                    domainRankGuestVo.setSolveNum(0.);
                }else {
                    redisScore = Double.parseDouble(String.format("%.16f",redisScore));
                    double rawSc=Math.floor(redisScore*Math.pow(10,6));
                    domainRankGuestVo.setScore(rawSc);
                    domainRankGuestVo.setSolveNum(Redis.zscore("guest:DomainSolveNumRank:"+grade+"-"+d.getId()+sub,t.getElement()));
                }
                domainRankGuestVo.setDomain(Dozer.convert(d,DomainsOpVo.class));
                domainRankGuestVoList.add(domainRankGuestVo);
            }
            totalRankGuestVo.setDomainRank(domainRankGuestVoList);
            redisRank=Redis.zrank("guest:TotalSolveScoreRank:"+grade+"-0"+sub,t.getElement());
            if (redisRank!=null){
                totalRankGuestVo.setId((int) (len-redisRank));
            }
            totalRankGuestVo.setUser(Dozer.convert(users, UsersAdminVo.class));
            totalRankGuestVoList.add(totalRankGuestVo);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(totalRankGuestVoList)
                .len(len.intValue())
                .build();

    }

    @ApiOperation(value = "单题榜")
    @GetMapping("/page/problems")
    public Response getProblemsRankPage(@RequestParam("pageSize") Integer pageSize,
                                        @RequestParam("current") Integer current,
                                        @RequestParam(value = "id") Integer problemId,
                                        @RequestParam(value = "grade",required = false) Integer grade){
        //高6 分数 低10 10000000000 - 十位时间戳
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
        if (grade == null)grade=0;
        String scoreName = "guest:SingleProblemsRank:"+grade+"-"+problemId;
        Long start=  (current-1)*pageSize.longValue();
        Long stop= current*pageSize.longValue()-1;
        Problems problems = problemsService.queryById(problemId);
        if (problems == null){
            return Response
                    .builder()
                    .code(ResponseCode.PROBLEMS_NOT_FOUND)
                    .build();
        }
        if (!Objects.equals(Redis.exists(scoreName),true)){
            return Response
                    .builder()
                    .code(ResponseCode.RANK_NOT_DATA)
                    .msg("暂时没有数据,请再接再厉")
                    .build();
        }
        List<SingleProblemRankAdminVo> singleProblemRankGuestVoList=new LinkedList<>();
        Set<Tuple> rankSet=Redis.zrevrangeWithScores(scoreName,start,stop);
        Long len = Redis.zcard(scoreName);
        int id=(current-1)*pageSize+1;
        for (Tuple t:rankSet){
            SingleProblemRankAdminVo singleProblemRankGuestVo = new SingleProblemRankAdminVo();
            Users users = usersService.queryById(Integer.parseInt(t.getElement()));
            if (users == null) continue;
            singleProblemRankGuestVo.setUser(Dozer.convert(users, UsersAdminVo.class));
            double redisScore= t.getScore();
            redisScore=Double.parseDouble(String.format("%.16f",redisScore));
            double rawSc = Math.floor(redisScore*Math.pow(10,6));
            double secSc = redisScore*Math.pow(10,6)-rawSc;
            secSc=(1-secSc)*Math.pow(10,13);
            if (redisScore != 0){
                singleProblemRankGuestVo.setSolveTime(new Date((long) secSc));
            }
            singleProblemRankGuestVo.setScore(rawSc);
            singleProblemRankGuestVo.setId(id);
            id++;
            singleProblemRankGuestVoList.add(singleProblemRankGuestVo);
        }
        Domains domains = domainsService.queryById(problems.getDomainId());
        HashMap<String,Object> data=new HashMap<>();
        data.put("domain",Dozer.convert(domains, DomainsOpVo.class));
        data.put("rank",singleProblemRankGuestVoList);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(data)
                .len(len.intValue())
                .build();
    }

    @ApiOperation(value = "获取用户排名")
    @GetMapping("/user")
    public Response getSingleUserRank(@RequestParam(value = "id") Integer userId,
                                      @RequestParam(value = "domainId", required = false) Integer domainId,
                                      @RequestParam(value = "isShow",required = false) Integer isShow,
                                      @RequestParam(value = "grade", required = false) Integer grade) {
        Users user = usersService.queryById(userId);
        String sub = "-1";
        if (Objects.equals(isShow,0)){
            sub="-0";
        }
        if (user == null) {
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        //HashMap<String,Object> data=new HashMap<>();
        if (grade == null) {
            grade = 0;
        }
        String scoreName, numName;
        if (domainId != null){
            Domains domains = domainsService.queryById(domainId);
            if (domains == null){
                return Response
                        .builder()
                        .code(ResponseCode.DOMAINS_DATA_NOT_FOUND)
                        .build();
            }
            scoreName="guest:DomainSolveScoreRank:"+grade+"-"+domainId+sub;
            numName="guest:DomainSolveNumRank:"+grade+"-"+domainId+sub;
        }else {
            scoreName="guest:TotalSolveScoreRank:"+grade+"-0"+sub;
            numName="guest:TotalSolveNumRank:"+grade+"-0"+sub;
        }
        boolean success = Objects.equals(Redis.exists(scoreName),true) && Objects.equals(Redis.exists(numName),true);
        if (!success){
            return Response
                    .builder()
                    .code(ResponseCode.RANK_NOT_DATA)
                    .msg("暂时没有数据,请再接再厉")
                    .build();
        }
        TotalRankAdminVo totalRankGuestVo = new TotalRankAdminVo();
        String strUserId = Integer.toString(userId);
        Long len = Redis.zcard(scoreName);
        Long redisRank=Redis.zrank("guest:TotalSolveScoreRank:"+grade+"-0"+sub,strUserId);
        //Long redisRank = Redis.zrank(scoreName, strUserId);
        if (redisRank==null || len == null){
            return Response
                    .builder()
                    .code(ResponseCode.RANK_NOT_DATA)
                    .msg("未查询到该用户")
                    .build();
        }
        totalRankGuestVo.setId((int) (len-redisRank));
        Double redisScore = Redis.zscore(scoreName,strUserId);
        if (redisScore != null){
            redisScore = Double.parseDouble(String.format("%.16f",redisScore));
            double rawSc=Math.floor(redisScore*Math.pow(10,6));
            if (domainId==null){
                totalRankGuestVo.setScore(rawSc);
                totalRankGuestVo.setSolveNum(Redis.zscore(numName,strUserId));
            }else {
                double secSc=redisScore*Math.pow(10,6)-rawSc;
                secSc = Math.round(secSc*Math.pow(10,10));
                totalRankGuestVo.setScore(secSc);
                totalRankGuestVo.setSolveNum(Redis.zscore("guest:TotalSolveNumRank:"+grade+"-0"+sub,strUserId));
            }
        }else {
            if (domainId== null){
                totalRankGuestVo.setScore(0.);
                totalRankGuestVo.setSolveNum(0.);
            }else {
                redisScore =Redis.zscore("guest:TotalSolveScoreRank:"+grade+"-0"+sub,strUserId);
                if (redisScore == null){
                    totalRankGuestVo.setScore(0.);
                totalRankGuestVo.setSolveNum(0.);
                }else {
                    double rawSc=Math.floor(redisScore*Math.pow(10,6));
                    totalRankGuestVo.setScore(rawSc);
                    totalRankGuestVo.setSolveNum(Redis.zscore("guest:TotalSolveNumRank:"+grade+"-0"+sub,strUserId));
                }
            }
        }

        List<DomainRankGuestVo> domainRankGuestVoList = new LinkedList<>();
        Domains newDomains = new Domains();
        List<Domains> domainsList = domainsService.queryAll(newDomains);
        for (Domains d : domainsList){
            DomainRankGuestVo domainRankGuestVo = new DomainRankGuestVo();
            //if (Objects.equals(d.getId(), domainId)) continue;
            scoreName = "guest:DomainSolveScoreRank:"+grade+"-"+d.getId()+sub;
            redisScore = Redis.zscore(scoreName, strUserId);
            redisRank = Redis.zrank(scoreName, strUserId);
            Long redisCard = Redis.zcard(scoreName);
            if (redisRank==null || redisCard == null){
                domainRankGuestVo.setId(null);
            }else{
                domainRankGuestVo.setId((int)(redisCard - redisRank));
            }
            if (redisScore == null || redisScore == 0){
                domainRankGuestVo.setScore(0.);
                domainRankGuestVo.setSolveNum(0.);
            }else {
                redisScore = Double.parseDouble(String.format("%.16f",redisScore));
                double rawSc=Math.floor(redisScore*Math.pow(10,6));
                domainRankGuestVo.setScore(rawSc);
                domainRankGuestVo.setSolveNum(Redis.zscore("guest:DomainSolveNumRank:"+grade+"-"+d.getId()+sub,strUserId));
            }
            domainRankGuestVo.setDomain(Dozer.convert(d,DomainsOpVo.class));
            domainRankGuestVoList.add(domainRankGuestVo);
        }
        totalRankGuestVo.setDomainRank(domainRankGuestVoList);
        totalRankGuestVo.setUser(Dozer.convert(user, UsersAdminVo.class));
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(totalRankGuestVo)
                .len(len.intValue())
                .build();
    }
}

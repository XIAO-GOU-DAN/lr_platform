package com.lr.platform.task;

import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.guestLog.GuestLog;
import com.lr.platform.entity.managerLog.ManagerLog;
import com.lr.platform.entity.problems.Problems;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.entity.users.Users;
import com.lr.platform.service.*;
import com.lr.platform.utils.GeoIP;
import com.lr.platform.utils.Geoip2;
import com.lr.platform.utils.Redis;
import com.lr.platform.utils.Score;
import com.maxmind.geoip2.model.AsnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class ScheduledTasks {

    @Resource
    private SolveRecordsService solveRecordsService;

    @Resource
    private ProblemsService problemsService;

    @Resource
    private UsersService usersService;

    @Resource
    private DomainsService domainsService;

    @Resource
    private GuestLogService guestLogService;

    @Resource
    private ManagerLogService managerLogService;

    private Integer deletedUserNew;

    private Integer deletedUserOld;



    @Scheduled(fixedDelay = 3000)
    public void handleRankNew(){
        Long st = System.currentTimeMillis();
        Domains domains = new Domains();
        List<Domains> domainsList = domainsService.queryAll(domains);
        HashMap<Integer,Integer> userTotalSolveNumMap =new HashMap<>(); // id : rawNum
        HashMap<Integer,Integer> userTotalSolveScoreMap =new HashMap<>(); // id : rawScore
        HashMap<Integer,Long> userTotalFinalTime = new HashMap<>(); // id : userFinalTime
        HashMap<Integer,HashMap<Integer,Integer>> userDomainSolveNumMap = new HashMap<>(); //  domainId : map<userid,rawSolveNum>
        HashMap<Integer,HashMap<Integer,Integer>> userDomainSolveScoreMap = new HashMap<>();// domainId : map<userid,rawSolveScore>
        //新生, 只统计已显示的题目
        HashMap<Integer,Integer> showUserTotalSolveNumMap =new HashMap<>(); // id : rawNum
        HashMap<Integer,Integer> showUserTotalSolveScoreMap =new HashMap<>(); // id : rawScore
        HashMap<Integer,Long> showUserTotalFinalTime = new HashMap<>(); // id : userFinalTime
        HashMap<Integer,HashMap<Integer,Integer>> showUserDomainSolveNumMap = new HashMap<>(); //  domainId : map<userid,rawSolveNum>
        HashMap<Integer,HashMap<Integer,Integer>> showUserDomainSolveScoreMap = new HashMap<>();// domainId : map<userid,rawSolveScore>
        for (Domains d: domainsList){
            Problems problems = new Problems();
            problems.setDomainId(d.getId());
            List<Problems> problemsList = problemsService.queryAll(problems);
            WhereRecords whereRecords = new WhereRecords();
            whereRecords.setDegree(0f); // 只统计有分
            int problemSolveCount; // 解出人数
            float degreeSum; // 完成度加和
            Date firstTime;
            for (Problems p:problemsList){
                problemSolveCount = 0;
                degreeSum = 0;
                if (p.getFirstSolveTime() == null){
                    firstTime = new Date();
                }else {
                    firstTime = p.getFirstSolveTime();
                }
                Problems updateProblems=new Problems();
                updateProblems.setFirstSolverId(null);
                updateProblems.setFirstSolveTime(null);
                whereRecords.setProblemId(p.getId());
                List<SolveRecords> solveRecordsList=solveRecordsService.queryAll(whereRecords);//单题记录
                if (solveRecordsList.isEmpty()){
                    continue;
                }
                for (SolveRecords s :solveRecordsList){
                    if (s.getUserId() == null)continue;
                    problemSolveCount++;
                    degreeSum += s.getDegree();
                    if (s.getDegree()>=0.8 && //一血产生则无法修改, 80% 以下不参与 一血排名
                            s.getLatestSubmissionTime()!=null && s.getLatestSubmissionTime().getTime()<firstTime.getTime()){
                        firstTime=s.getLatestSubmissionTime();//一血
                        updateProblems.setFirstSolverId(s.getUserId());
                    }
                    if (userTotalFinalTime.get(s.getUserId())==null ||
                            userTotalFinalTime.get(s.getUserId())< s.getLatestSubmissionTime().getTime()){
                        userTotalFinalTime.put(s.getUserId(),s.getLatestSubmissionTime().getTime());
                    }
                    if (Objects.equals(p.getIsShow(),1) && (showUserTotalFinalTime.get(s.getUserId())==null ||
                            showUserTotalFinalTime.get(s.getUserId())< s.getLatestSubmissionTime().getTime())){
                        showUserTotalFinalTime.put(s.getUserId(),s.getLatestSubmissionTime().getTime());
                    }
                }
                if (updateProblems.getFirstSolverId()!=null){
                    updateProblems.setFirstSolveTime(firstTime);
                }
                Integer score= Score.CalculateCurrentScoreTest(p.getOriginalScore(),degreeSum);//实时分
                for (SolveRecords s:solveRecordsList){
                    if (s.getUserId() == null)continue;
                    int singleScore=(int)Math.ceil(s.getDegree()*score);
                    userTotalSolveNumMap.merge(s.getUserId(), 1, Integer::sum);//个人总解出数
                    userTotalSolveScoreMap.merge(s.getUserId(), singleScore, Integer::sum);//个人总分
                    userDomainSolveNumMap.computeIfAbsent(d.getId(), k -> new HashMap<>());
                    userDomainSolveScoreMap.computeIfAbsent(d.getId(),k -> new HashMap<>());
                    userDomainSolveNumMap.get(d.getId()).merge(s.getUserId(),1,Integer::sum);//个人方向总解出数
                    userDomainSolveScoreMap.get(d.getId()).merge(s.getUserId(),singleScore,Integer::sum);//个人方向总分
                    if (Objects.equals(p.getIsShow(),1)){
                        showUserTotalSolveNumMap.merge(s.getUserId(), 1, Integer::sum);//个人总解出数
                        showUserTotalSolveScoreMap.merge(s.getUserId(), singleScore, Integer::sum);//个人总分
                        showUserDomainSolveNumMap.computeIfAbsent(d.getId(), k -> new HashMap<>());
                        showUserDomainSolveScoreMap.computeIfAbsent(d.getId(),k -> new HashMap<>());
                        showUserDomainSolveNumMap.get(d.getId()).merge(s.getUserId(),1,Integer::sum);//个人方向总解出数
                        showUserDomainSolveScoreMap.get(d.getId()).merge(s.getUserId(),singleScore,Integer::sum);//个人方向总分
                    }
                    //单题榜添加 分数 //TODO cache
                    Users users=usersService.queryById(s.getUserId());
                    if (users==null)continue;
                    //单题总榜
                    double redisScore = singleScore*Math.pow(10.,-6);
                    redisScore = Double.parseDouble(String.format("%.16f",redisScore+(Math.pow(10,10)-(int)(s.getLatestSubmissionTime().getTime() /1000))*Math.pow(10.,-16)));
                    Redis.zadd("guest:SingleProblemsRank:0-"+s.getProblemId(), redisScore,s.getUserId().toString());
                    if (users.getGrade()!=null){
                        //单题 年级榜
                        Redis.zadd("guest:SingleProblemsRank:"+users.getGrade()+"-"+s.getProblemId(), redisScore,s.getUserId().toString());
                        Redis.zadd("guest:SingleProblemsRank:1-"+s.getProblemId(), redisScore,s.getUserId().toString());
                    }
                }
                updateProblems.setId(p.getId());
                updateProblems.setSolverNum(problemSolveCount);
                updateProblems.setCurrentScore(score);
                try {
                    problemsService.update(updateProblems);
                }catch (Exception exception){
                    exception.printStackTrace();
                    log.error("ERROR:",exception);
                }
            }
        }
        // 通过 单题榜生成 方向榜 总榜 同时更新 mysql
        Users offsetUser = new Users();
        offsetUser.setId(0);
        List<Users> grade = usersService.queryAllGrade(new Users());
        for (;;){
            List<Users> usersList = usersService.queryByOffset(offsetUser);
            if (usersList.isEmpty())break;
            offsetUser.setId(usersList.get(usersList.size()-1).getId());
            Users updateUsers;
            for (Users u : usersList){
                // step 1  更新 mysql
                updateUsers=new Users();
                updateUsers.setId(u.getId());
                Integer showTotalScore = showUserTotalSolveScoreMap.get(u.getId());
                Integer showTotalNum = showUserTotalSolveNumMap.get(u.getId());
                if (Objects.equals(showTotalScore,null)){
                    showTotalScore =0;
                    showTotalNum=0;
                    updateUsers.setTotalScore(0);
                    updateUsers.setTotalSolveNum(0);
                }else {
                    updateUsers.setTotalScore(showTotalScore);
                    updateUsers.setTotalSolveNum(showTotalNum);
                }
                try {
                    usersService.update(updateUsers);
                }catch (Exception exception){
                    exception.printStackTrace();
                    log.error("ERROR:" ,exception);
                }
                // step 2 更新 总榜 同时删除年级异常的总榜
                // 更新 后台总榜
                Integer totalScore = userTotalSolveScoreMap.get(u.getId());
                Integer totalNum = userTotalSolveNumMap.get(u.getId());
                double redisScore=0.;
                if (totalScore==null){
                    totalScore=0;
                    totalNum = 0;
                }else {
                    redisScore = totalScore*Math.pow(10.,-6);
                    redisScore = Double.parseDouble(String.format("%.16f",redisScore+(Math.pow(10,10)-(int)(userTotalFinalTime.get(u.getId()) /1000))*Math.pow(10.,-16)));
                }
                Redis.zadd("guest:TotalSolveNumRank:0-0-0",totalNum.doubleValue(),u.getId().toString());
                Redis.zadd("guest:TotalSolveScoreRank:0-0-0",redisScore, u.getId().toString());
                if (u.getGrade()!=null){
                    Redis.zadd("guest:TotalSolveNumRank:"+u.getGrade()+"-0-0",totalNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:"+u.getGrade()+"-0-0",redisScore, u.getId().toString());
                    Redis.zadd("guest:TotalSolveNumRank:1-0-0",totalNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:1-0-0",redisScore, u.getId().toString());
                }
                // 更新 新生总榜
                //showTotalScore = showUserTotalSolveScoreMap.get(u.getId());
                //showTotalNum = showUserTotalSolveNumMap.get(u.getId());
                double showRedisScore = 0.;
                if (!Objects.equals(showTotalScore,0)){
                    showRedisScore = showTotalScore*Math.pow(10.,-6);
                    showRedisScore = Double.parseDouble(String.format("%.16f",showRedisScore+(Math.pow(10,10)-(int)(showUserTotalFinalTime.get(u.getId()) /1000))*Math.pow(10.,-16)));
                }
                Redis.zadd("guest:TotalSolveNumRank:0-0-1",showTotalNum.doubleValue(),u.getId().toString());
                Redis.zadd("guest:TotalSolveScoreRank:0-0-1",showRedisScore, u.getId().toString());
                if (u.getGrade()!=null){
                    Redis.zadd("guest:TotalSolveNumRank:"+u.getGrade()+"-0-1",showTotalNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:"+u.getGrade()+"-0-1",showRedisScore, u.getId().toString());
                    Redis.zadd("guest:TotalSolveNumRank:1-0-1",showTotalNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:1-0-1",showRedisScore, u.getId().toString());
                }
                // 更新 方向榜
                for (Domains d :domainsList){
                    // 更新 后台方向榜
                    Integer domainScore = 0;
                    Integer domainNum = 0;
                    HashMap<Integer,Integer> domainScoreMap = userDomainSolveScoreMap.get(d.getId());
                    HashMap<Integer,Integer> domainNumMap = userDomainSolveNumMap.get(d.getId());
                    if (domainScoreMap!=null && domainScoreMap.get(u.getId())!=null){
                        domainScore=domainScoreMap.get(u.getId());
                        domainNum=domainNumMap.get(u.getId());
                    }
                    double redisDomainScore=0;
                    if (!Objects.equals(totalScore,0)){
                        redisDomainScore = domainScore*Math.pow(10.,-6);
                        redisDomainScore = Double.parseDouble(String.format("%.16f",redisDomainScore+totalScore*Math.pow(10.,-16)));
                    }
                    Redis.zadd("guest:DomainSolveNumRank:0-"+d.getId()+"-0",domainNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:DomainSolveScoreRank:0-"+d.getId()+"-0",redisDomainScore,u.getId().toString());
                    if (u.getGrade()!=null){
                        Redis.zadd("guest:DomainSolveNumRank:"+u.getGrade()+"-"+d.getId()+"-0",domainNum.doubleValue(),u.getId().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:"+u.getGrade()+"-"+d.getId()+"-0",redisDomainScore, u.getId().toString());
                        Redis.zadd("guest:DomainSolveNumRank:1-"+d.getId()+"-0",domainNum.doubleValue(),u.getId().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:1-"+d.getId()+"-0",redisDomainScore, u.getId().toString());
                    }
                    // 更新 新生方向榜
                    Integer showDomainScore = 0;
                    Integer showDomainNum = 0;
                    HashMap<Integer,Integer> showDomainScoreMap = showUserDomainSolveScoreMap.get(d.getId());
                    HashMap<Integer,Integer> showDomainNumMap = showUserDomainSolveNumMap.get(d.getId());
                    if (showDomainScoreMap!=null && showDomainScoreMap.get(u.getId())!=null){
                        showDomainScore=showDomainScoreMap.get(u.getId());
                        showDomainNum=showDomainNumMap.get(u.getId());
                    }
                    double showRedisDomainScore=0;
                    if (!Objects.equals(showTotalScore,0)){
                        showRedisDomainScore = showDomainScore*Math.pow(10.,-6);
                        showRedisDomainScore = Double.parseDouble(String.format("%.16f",showRedisDomainScore+showTotalScore*Math.pow(10.,-16)));
                    }
                    Redis.zadd("guest:DomainSolveNumRank:0-"+d.getId()+"-1",showDomainNum.doubleValue(),u.getId().toString());
                    Redis.zadd("guest:DomainSolveScoreRank:0-"+d.getId()+"-1",showRedisDomainScore,u.getId().toString());
                    if (u.getGrade()!=null){
                        Redis.zadd("guest:DomainSolveNumRank:"+u.getGrade()+"-"+d.getId()+"-1",showDomainNum.doubleValue(),u.getId().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:"+u.getGrade()+"-"+d.getId()+"-1",showRedisDomainScore, u.getId().toString());
                        Redis.zadd("guest:DomainSolveNumRank:1-"+d.getId()+"-1",showDomainNum.doubleValue(),u.getId().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:1-"+d.getId()+"-1",showRedisDomainScore, u.getId().toString());
                    }
                }
                // 年级处理
                for (Users g :grade){
                    if (u.getGrade()==null){
                        Redis.zrem("guest:TotalSolveNumRank:1-0-1",u.getId().toString());
                        Redis.zrem("guest:TotalSolveScoreRank:1-0-1", u.getId().toString());
                        Redis.zrem("guest:TotalSolveNumRank:1-0-0",u.getId().toString());
                        Redis.zrem("guest:TotalSolveScoreRank:1-0-0", u.getId().toString());
                        for (Domains d :domainsList){
                            Redis.zrem("guest:DomainSolveScoreRank:1-"+d.getId()+"-1",u.getId().toString());
                            Redis.zrem("guest:DomainSolveNumRank:1-"+d.getId()+"-0",u.getId().toString());
                            Problems problems = new Problems();
                            problems.setDomainId(d.getId());
                            List<Problems> problemsList = problemsService.queryAll(problems);
                            for (Problems p:problemsList){
                                Redis.zrem("guest:SingleProblemsRank:1-"+p.getId(), u.getId().toString());
                            }
                        }
                    }
                    if (u.getGrade()!=null && g!=null && !Objects.equals(u.getGrade(), g.getGrade())){
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0-1",u.getId().toString());
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0-1", u.getId().toString());
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0-0",u.getId().toString());
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0-0", u.getId().toString());
                        for (Domains d :domainsList){
                            Redis.zrem("guest:DomainSolveScoreRank:"+g.getGrade()+"-"+d.getId()+"-1",u.getId().toString());
                            Redis.zrem("guest:DomainSolveNumRank:"+g.getGrade()+"-"+d.getId()+"-0",u.getId().toString());
                            Problems problems = new Problems();
                            problems.setDomainId(d.getId());
                            List<Problems> problemsList = problemsService.queryAll(problems);
                            for (Problems p:problemsList){
                                Redis.zrem("guest:SingleProblemsRank:"+g.getGrade()+"-"+p.getId(), u.getId().toString());
                            }
                        }
                    }
                }
            }
        }

        //删除似人
        Long total=Redis.zcard("guest:TotalSolveScoreRank:0-0-0");
        deletedUserNew=0;
        long step=50L;
        if (total == null){
            total=0L;
        }
        int times=(int)Math.ceil(total.doubleValue()/step);
        for (int i=0;i<times;i++){
            Set<Tuple> userSet= Redis.zrevrangeWithScores("guest:TotalSolveScoreRank:0-0-0",i*step,(i+1)*step-1);
            if (userSet==null) continue;
            for (Tuple t:userSet){
                Users user= usersService.queryById(Integer.parseInt(t.getElement()));
                if (user!=null)continue;
                //删除总榜
                deletedUserNew++;
                Redis.zrem("guest:TotalSolveScoreRank:0-0-0",t.getElement());
                Redis.zrem("guest:TotalSolveScoreRank:1-0-0",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:0-0-0",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:1-0-0",t.getElement());
                Redis.zrem("guest:TotalSolveScoreRank:0-0-1",t.getElement());
                Redis.zrem("guest:TotalSolveScoreRank:1-0-1",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:0-0-1",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:1-0-1",t.getElement());
                for (Users g:grade){
                    if (g!=null && g.getGrade()!=null){
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0-0",t.getElement());
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0-0",t.getElement());
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0-1",t.getElement());
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0-1",t.getElement());
                    }
                }
                //删除方向榜, 单题榜
                for(Domains d:domainsList){
                    Redis.zrem("guest:DomainSolveNumRank:0-"+d.getId()+"-0",t.getElement());
                    Redis.zrem("guest:DomainSolveNumRank:1-"+d.getId()+"-0",t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:0-"+d.getId()+"-0",t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:1-"+d.getId()+"-0",t.getElement());
                    Redis.zrem("guest:DomainSolveNumRank:0-"+d.getId()+"-1",t.getElement());
                    Redis.zrem("guest:DomainSolveNumRank:1-"+d.getId()+"-1",t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:0-"+d.getId()+"-1",t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:1-"+d.getId()+"-1",t.getElement());
                    for (Users g:grade){
                        if (g!=null && g.getGrade()!=null){
                            Redis.zrem("guest:DomainSolveScoreRank:"+g.getGrade()+"-"+d.getId()+"-0",t.getElement());
                            Redis.zrem("guest:DomainSolveNumRank:"+g.getGrade()+"-"+d.getId()+"-0",t.getElement());
                            Redis.zrem("guest:DomainSolveScoreRank:"+g.getGrade()+"-"+d.getId()+"-1",t.getElement());
                            Redis.zrem("guest:DomainSolveNumRank:"+g.getGrade()+"-"+d.getId()+"-1",t.getElement());
                        }
                    }
                    Problems problem=new Problems();
                    problem.setDomainId(d.getId());
                    List<Problems> problemsList=problemsService.queryAll(problem);
                    for (Problems p:problemsList){
                        if (!Objects.equals(Redis.exists("guest:SingleProblemsRank:0-"+p.getId()),true)){
                            continue;
                        }
                        Redis.zrem("guest:SingleProblemsRank:0-"+p.getId(),t.getElement());
                        Redis.zrem("guest:SingleProblemsRank:1-"+p.getId(),t.getElement());
                        for (Users g:grade){
                            if (g!=null && g.getGrade()!=null){
                                Redis.zrem("guest:SingleProblemsRank:"+g.getGrade()+"-"+p.getId(),t.getElement());
                            }
                        }
                    }
                }
            }
        }
        //删除不存在的key
        ScanParams scanParams = new ScanParams();
        scanParams.match("guest:Domain*");
        ScanResult<String> res;
        String cursor = "0";
        int domainCount=0;
        int id;
        for (;;){
            res = Redis.scan(cursor,scanParams);
            if (res==null)break;
            cursor=res.getCursor();
            for (String s :res.getResult()){
                id = Integer.parseInt(s.replaceAll("^(.*)(-)(.*)(-)(.*)","$3"));
                if (!domainsService.exists(id)){
                    Redis.del(s);
                    domainCount++;
                }
            }
            if (Objects.equals(cursor, "0"))break;
        }
        cursor = "0";
        int problemsCount=0;
        scanParams.match("guest:SingleProblems*");
        HashMap<Integer,HashMap<String,Integer>> domainUpdateNumMap = new HashMap<>();
        HashMap<Integer,HashMap<String,Integer>> domainUpdateScMap = new HashMap<>();
        HashMap<Integer,Integer> isHandle = new HashMap<>();
        for (;;){
            res = Redis.scan(cursor,scanParams);
            if (res==null)break;
            cursor=res.getCursor();
            for (String s :res.getResult()){
                id = Integer.parseInt(s.replaceAll("^(.*)(-)(.*)","$3"));
                Problems problems = problemsService.queryById(id);
                if (problems==null){
                    Redis.del(s);
                    problemsCount++;
                }
            }
            if (Objects.equals(cursor, "0"))break;
        }
        Long end = System.currentTimeMillis();
        if (deletedUserNew!=0 || problemsCount!=0 || domainCount!=0){
            System.out.printf("[%s] 刷新榜单完成, 耗时 %s ms, 删除似人 %s 个,删除不存在的方向key %s 个,删除不存在的题目key %s 个%n", LocalDateTime.now(),end-st,deletedUserNew,domainCount,problemsCount);
        }
    }


    //@Scheduled(fixedDelay = 3000)
    public void handleRank(){
        //统计 单题榜
        //1 统计每题解出(degree 有分) 更新problems表
        Long st = System.currentTimeMillis();
        Domains domains=new Domains();
        List<Domains> domainsList = domainsService.queryAll(domains);
        HashMap<Integer,Integer> userTotalSolveNumMap =new HashMap<>();
        HashMap<Integer,Integer> userTotalSolveScoreMap =new HashMap<>();
        HashMap<Integer,Long> userTotalFinalTime = new HashMap<>(); // key userid v userfinalTime
        HashMap<Integer,HashMap<Integer,Integer>> userDomainSolveNumMap = new HashMap<>(); // key domainId v map<userid,solveNum>
        HashMap<Integer,HashMap<Integer,Integer>> userDomainSolveScoreMap = new HashMap<>();// key domainId v map<userid,solveScore>
        for (Domains d: domainsList){
            Problems problems=new Problems();
            //problems.setIsShow(1);//只统计显示的题目
            problems.setDomainId(d.getId());//按方向查
            List<Problems> problemsList=problemsService.queryAll(problems);
            WhereRecords whereRecords=new WhereRecords();
            whereRecords.setDegree(0f);//只统计有分
            int problemsCount;
            float solve;
            Date firstTime;
            for (Problems e:problemsList){
                problemsCount=0;
                //userTotalSolveNumMap.clear();
                solve=0;
                if (e.getFirstSolveTime()==null){
                    firstTime=new Date();
                }else {
                    firstTime=e.getFirstSolveTime();
                }
                Problems updateProblems=new Problems();
                updateProblems.setFirstSolverId(null);
                updateProblems.setFirstSolveTime(null);
                whereRecords.setProblemId(e.getId());
                List<SolveRecords> solveRecordsList=solveRecordsService.queryAll(whereRecords);//单题记录
                if (solveRecordsList.isEmpty()){
                    continue;
                }
                for (SolveRecords s:solveRecordsList){//统计单题总数
                    if (s.getUserId() != null){
                        problemsCount++;
                        solve+=s.getDegree();
                        if (/*e.getFirstSolverId() == null &&*/ s.getDegree()>=0.8 && //一血产生则无法修改, 80% 以下不参与 一血排名
                                s.getLatestSubmissionTime()!=null && s.getLatestSubmissionTime().getTime()<firstTime.getTime()){
                            firstTime=s.getLatestSubmissionTime();//一血
                            updateProblems.setFirstSolverId(s.getUserId());
                        }
                        if (userTotalFinalTime.get(s.getUserId())==null ||
                                userTotalFinalTime.get(s.getUserId())< s.getLatestSubmissionTime().getTime()){
                            userTotalFinalTime.put(s.getUserId(),s.getLatestSubmissionTime().getTime());
                        }
                    }
                }
                if (updateProblems.getFirstSolverId()!=null){
                    updateProblems.setFirstSolveTime(firstTime);
                }
                Integer score= Score.CalculateCurrentScoreTest(e.getOriginalScore(),solve);//实时分
                for (SolveRecords s:solveRecordsList){
                    if (s.getUserId() != null){
                        int singleScore=(int)Math.ceil(s.getDegree()*score);
                        userTotalSolveNumMap.merge(s.getUserId(), 1, Integer::sum);//个人总解出数
                        userTotalSolveScoreMap.merge(s.getUserId(), singleScore, Integer::sum);//个人总分
                        userDomainSolveNumMap.computeIfAbsent(d.getId(), k -> new HashMap<>());
                        userDomainSolveScoreMap.computeIfAbsent(d.getId(),k -> new HashMap<>());
                        userDomainSolveNumMap.get(d.getId()).merge(s.getUserId(),1,Integer::sum);//个人方向总解出数
                        userDomainSolveScoreMap.get(d.getId()).merge(s.getUserId(),singleScore,Integer::sum);//个人方向总分
                        //单题榜添加 分数 //TODO cache
                        Users users=usersService.queryById(s.getUserId());
                        if (users != null){
                            //单题总榜
                            double redisScore = singleScore*Math.pow(10.,-6);
                            redisScore = Double.parseDouble(String.format("%.16f",redisScore+(Math.pow(10,10)-(int)(s.getLatestSubmissionTime().getTime() /1000))*Math.pow(10.,-16)));
                            Redis.zadd("guest:SingleProblemsRank:0-"+s.getProblemId(), redisScore,s.getUserId().toString());
                            if (users.getGrade()!=null){
                                //单题 年级榜
                                Redis.zadd("guest:SingleProblemsRank:"+users.getGrade()+"-"+s.getProblemId(), redisScore,s.getUserId().toString());
                                Redis.zadd("guest:SingleProblemsRank:1-"+s.getProblemId(), redisScore,s.getUserId().toString());
                            }
                        }

                    }
                }
                updateProblems.setId(e.getId());
                updateProblems.setSolverNum(problemsCount);
                updateProblems.setCurrentScore(score);
                try {
                    problemsService.update(updateProblems);
                }catch (Exception exception){
                    log.error("ERROR:",exception);
                }
            }
        }
        //更新users表 生成 总榜 解出数 & 分数 guest:TotalSolveNumRank:1 & guest:TotalSolveScoreRank:1
        for (Map.Entry<Integer,Integer> e:userTotalSolveNumMap.entrySet()){
            Users updateUsers=new Users();
            updateUsers.setId(e.getKey());
            updateUsers.setTotalSolveNum(e.getValue());
            updateUsers.setTotalScore(userTotalSolveScoreMap.get(e.getKey()));
            try {
                usersService.update(updateUsers);
            }catch (Exception exception){
                log.error("ERROR:" ,exception);
            }
            Users users=usersService.queryById(e.getKey());//TODO cache
            if (users != null){
                double redisScore = userTotalSolveScoreMap.get(e.getKey())*Math.pow(10.,-6);
                redisScore = Double.parseDouble(String.format("%.16f",redisScore+(Math.pow(10,10)-(int)(userTotalFinalTime.get(e.getKey()) /1000))*Math.pow(10.,-16)));
                Redis.zadd("guest:TotalSolveNumRank:0-0",e.getValue().doubleValue(),e.getKey().toString());
                Redis.zadd("guest:TotalSolveScoreRank:0-0",redisScore, e.getKey().toString());
                if (users.getGrade() != null){
                    Redis.zadd("guest:TotalSolveNumRank:"+users.getGrade()+"-0",e.getValue().doubleValue(),e.getKey().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:"+users.getGrade()+"-0",redisScore, e.getKey().toString());
                    Redis.zadd("guest:TotalSolveNumRank:1-0",e.getValue().doubleValue(),e.getKey().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:1-0",redisScore, e.getKey().toString());
                }
            }

            /*
            if (updateUsers.getTotalScore()==null){
                log.error("ERROR: user{} 解出数{} 总分{} 不匹配",updateUsers.getId(),e.getValue(),updateUsers.getTotalScore());
            }

             */
        }
        // 生成 方向 排名
        for (Map.Entry<Integer,HashMap<Integer,Integer>> out:userDomainSolveNumMap.entrySet()){
            for (Map.Entry<Integer,Integer> in:out.getValue().entrySet()){
                Users users=usersService.queryById(in.getKey());
                if (users != null){
                    double redisScore=userDomainSolveScoreMap.get(out.getKey()).get(in.getKey())*Math.pow(10.,-6);
                    redisScore=Double.parseDouble(String.format("%.16f",redisScore+(userTotalSolveScoreMap.get(in.getKey()))*Math.pow(10.,-16)));
                    Redis.zadd("guest:DomainSolveNumRank:0-"+out.getKey(),in.getValue().doubleValue(),in.getKey().toString());
                    Redis.zadd("guest:DomainSolveScoreRank:0-"+out.getKey(),redisScore, in.getKey().toString());
                    if (users.getGrade() != null){
                        Redis.zadd("guest:DomainSolveNumRank:"+users.getGrade()+"-"+out.getKey(),in.getValue().doubleValue(),in.getKey().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:"+users.getGrade()+"-"+out.getKey(),redisScore,in.getKey().toString());
                        Redis.zadd("guest:DomainSolveNumRank:1-"+out.getKey(),in.getValue().doubleValue(),in.getKey().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:1-"+out.getKey(),redisScore, in.getKey().toString());
                    }
                }
            }
        }
        //将剩余 o 分人员加入表中 同时去除 年级异常的数据
        if (Objects.equals(Redis.exists("guest:TotalSolveScoreRank:0-0"),true)){
            Users users=new Users();
            List<Users> grade = usersService.queryAllGrade(users);
            List<Users> usersList=usersService.queryAll(users);
            Problems problems=new Problems();
            //problems.setIsShow(1);
            List<Problems> problemsList=problemsService.queryAll(problems);
            for (Users u:usersList){
                // 总榜
                Double score=Redis.zscore("guest:TotalSolveScoreRank:0-0",u.getId().toString());//总
                if (score==null || score==0){
                    Redis.zadd("guest:TotalSolveNumRank:0-0",0.,u.getId().toString());
                    Redis.zadd("guest:TotalSolveScoreRank:0-0",0.,u.getId().toString());
                }
                for (Domains d: domainsList){//方向榜 新增
                    score=Redis.zscore("guest:DomainSolveScoreRank:0-"+d.getId(),u.getId().toString());
                    // 0.1111112222222222
                    // 0.0000009999999999
                    // 0.000001
                    if (score == null || score == 0 || score<0.000001){
                        Integer totalScore = userTotalSolveScoreMap.get(u.getId());
                        double redisScore=0.;
                        if (totalScore != null){
                            redisScore = totalScore*Math.pow(10.,-16);
                        }
                        Redis.zadd("guest:DomainSolveNumRank:0-"+d.getId(),0.,u.getId().toString());
                        Redis.zadd("guest:DomainSolveScoreRank:0-"+d.getId(),redisScore,u.getId().toString());
                    }
                }
                if (u.getGrade() != null){
                    score=Redis.zscore("guest:TotalSolveScoreRank:1-0",u.getId().toString());//总 筛选年级
                    if (score == null || score == 0){ //未存在 存 o 分
                        Redis.zadd("guest:TotalSolveNumRank:1-0",0.,u.getId().toString());
                        Redis.zadd("guest:TotalSolveScoreRank:1-0",0.,u.getId().toString());
                        Redis.zadd("guest:TotalSolveNumRank:"+u.getGrade()+"-0",0.,u.getId().toString());//对应年级榜存 o 分
                        Redis.zadd("guest:TotalSolveScoreRank:"+u.getGrade()+"-0",0.,u.getId().toString());
                    }
                    for (Domains d: domainsList){//方向榜 新增
                        score=Redis.zscore("guest:DomainSolveScoreRank:1-"+d.getId(),u.getId().toString());
                        if (score == null || score == 0 || score < 0.000001){
                            Integer totalScore = userTotalSolveScoreMap.get(u.getId());
                            double redisScore=0.;
                            if (totalScore != null){
                                redisScore = totalScore*Math.pow(10.,-16);
                            }
                            Redis.zadd("guest:DomainSolveNumRank:1-"+d.getId(),0.,u.getId().toString());
                            Redis.zadd("guest:DomainSolveScoreRank:1-"+d.getId(),redisScore,u.getId().toString());
                            Redis.zadd("guest:DomainSolveNumRank:"+u.getGrade()+"-"+d.getId(),0.,u.getId().toString());
                            Redis.zadd("guest:DomainSolveScoreRank:"+u.getGrade()+"-"+d.getId(),redisScore,u.getId().toString());
                        }
                    }
                }else {
                    Redis.zrem("guest:TotalSolveNumRank:1-0",u.getId().toString());
                    Redis.zrem("guest:TotalSolveScoreRank:1-0",u.getId().toString());
                    for (Domains d: domainsList){
                        Redis.zrem("guest:DomainSolveNumRank:1-"+d.getId(),u.getId().toString());
                        Redis.zrem("guest:DomainSolveScoreRank:1-"+d.getId(),u.getId().toString());
                    }
                }
                for (Users g:grade){
                    // 所有 题目剔除
                    if (g != null && g.getGrade() != null && !Objects.equals(g.getGrade(), u.getGrade())){ // 年级不等 进行剔除
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0",u.getId().toString());
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0",u.getId().toString());
                        for (Domains d: domainsList){
                            Redis.zrem("guest:DomainSolveNumRank:"+g.getGrade()+"-"+d.getId(),u.getId().toString());
                            Redis.zrem("guest:DomainSolveScoreRank:"+g.getGrade()+"-"+d.getId(),u.getId().toString());
                        }
                    }
                }
                for (Problems p:problemsList) {
                    score = Redis.zscore("guest:SingleProblemsRank:0-"+p.getId(), u.getId().toString());
                    if (score == null || score == 0){
                        Redis.zadd("guest:SingleProblemsRank:0-"+p.getId(),0.,u.getId().toString());
                    }
                    if (u.getGrade() != null) {
                        score = Redis.zscore("guest:SingleProblemsRank:1-"+p.getId(), u.getId().toString());
                        if (score == null || score == 0){
                            Redis.zadd("guest:SingleProblemsRank:1-"+p.getId(),0.,u.getId().toString());
                            Redis.zadd("guest:SingleProblemsRank:"+u.getGrade()+"-"+p.getId(),0.,u.getId().toString());
                        }
                    }else {
                        Redis.zrem("guest:SingleProblemsRank:1-"+p.getId(),u.getId().toString());
                    }
                    for (Users g:grade){
                        if (g != null && g.getGrade() != null && !Objects.equals(g.getGrade(), u.getGrade())){ // 年级不等 进行剔除
                            Redis.zrem("guest:SingleProblemsRank:"+g.getGrade()+"-"+p.getId(),u.getId().toString());
                        }
                    }
                }
            }
        }
        //删除似人
        List<Users> grade = usersService.queryAllGrade(new Users());
        Long total=Redis.zcard("guest:TotalSolveScoreRank:0-0");
        deletedUserNew=0;
        long step=50L;
        if (total == null){
            total=0L;
        }
        int times=(int)Math.ceil(total.doubleValue()/step);
        for (int i=0;i<times;i++){
            Set<Tuple> userSet= Redis.zrevrangeWithScores("guest:TotalSolveScoreRank:0-0",i*step,(i+1)*step-1);
            if (userSet==null) continue;
            for (Tuple t:userSet){
                Users user= usersService.queryById(Integer.parseInt(t.getElement()));
                if (user!=null)continue;
                //删除总榜
                deletedUserNew++;
                Redis.zrem("guest:TotalSolveScoreRank:0-0",t.getElement());
                Redis.zrem("guest:TotalSolveScoreRank:1-0",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:0-0",t.getElement());
                Redis.zrem("guest:TotalSolveNumRank:1-0",t.getElement());
                for (Users g:grade){
                    if (g!=null && g.getGrade()!=null){
                        Redis.zrem("guest:TotalSolveScoreRank:"+g.getGrade()+"-0",t.getElement());
                        Redis.zrem("guest:TotalSolveNumRank:"+g.getGrade()+"-0",t.getElement());
                    }
                }
                //删除方向榜, 单题榜
                for(Domains d:domainsList){
                    Redis.zrem("guest:DomainSolveNumRank:0-"+d.getId(),t.getElement());
                    Redis.zrem("guest:DomainSolveNumRank:1-"+d.getId(),t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:0-"+d.getId(),t.getElement());
                    Redis.zrem("guest:DomainSolveScoreRank:1-"+d.getId(),t.getElement());
                    for (Users g:grade){
                        if (g!=null && g.getGrade()!=null){
                            Redis.zrem("guest:DomainSolveScoreRank:"+g.getGrade()+"-0",t.getElement());
                            Redis.zrem("guest:DomainSolveNumRank:"+g.getGrade()+"-0",t.getElement());
                        }
                    }
                    Problems problem=new Problems();
                    problem.setDomainId(d.getId());
                    List<Problems> problemsList=problemsService.queryAll(problem);
                    for (Problems p:problemsList){
                        if (!Objects.equals(Redis.exists("guest:SingleProblemsRank:0-"+p.getId()),true)){
                            continue;
                        }
                        Redis.zrem("guest:SingleProblemsRank:0-"+p.getId(),t.getElement());
                        Redis.zrem("guest:SingleProblemsRank:1-"+p.getId(),t.getElement());
                        for (Users g:grade){
                            if (g!=null && g.getGrade()!=null){
                                Redis.zrem("guest:SingleProblemsRank:"+g.getGrade()+"-0",t.getElement());
                            }
                        }
                    }
                }
            }
        }
        //删除不存在的key
        ScanParams scanParams = new ScanParams();
        scanParams.match("guest:Domain*");
        ScanResult<String> res;
        String cursor = "0";
        int domainCount=0;
        int id;
        for (;;){
            res = Redis.scan(cursor,scanParams);
            if (res==null)break;
            cursor=res.getCursor();
            for (String s :res.getResult()){
                id = Integer.parseInt(s.replaceAll("^(.*)(-)(.*)","$3"));
                if (!domainsService.exists(id)){
                    Redis.del(s);
                    domainCount++;
                }
            }
            if (Objects.equals(cursor, "0"))break;
        }
        cursor = "0";
        int problemsCount=0;
        scanParams.match("guest:SingleProblems*");
        for (;;){
            res = Redis.scan(cursor,scanParams);
            if (res==null)break;
            cursor=res.getCursor();
            for (String s :res.getResult()){
                id = Integer.parseInt(s.replaceAll("^(.*)(-)(.*)","$3"));
                if (!problemsService.exists(id)){
                    Redis.del(s);
                    problemsCount++;
                }
            }
            if (Objects.equals(cursor, "0"))break;
        }
        Long end = System.currentTimeMillis();
        if (deletedUserNew!=0 || problemsCount!=0 || domainCount!=0){
            System.out.printf("[%s] 刷新榜单完成, 耗时 %s ms, 删除似人 %s 个,删除不存在的方向key %s 个,删除不存在的题目key %s 个%n", LocalDateTime.now(),end-st,deletedUserNew,domainCount,problemsCount);
        }
    }
    //@Scheduled(fixedDelay = 3000)
    public void test(){
        ScanParams scanParams = new ScanParams();
        scanParams.match("guest:DomainTest*");
        ScanResult<String> res;
        String cursor = "0";
        int count=0;
        for (;;){
           res = Redis.scan(cursor,scanParams);
           if (res==null)break;
           cursor=res.getCursor();
           for (String s :res.getResult()){
               String id = s.replaceAll("^(.*)(-)(.*)","$3");
               //System.out.println(id);
               System.out.println(Integer.parseInt(id));
               count++;
           }
           if (Objects.equals(cursor, "0"))break;
        }
        System.out.println(count);
    }

    //@Scheduled(fixedDelay = 3600000)
    public void handleOldData(){
        int st=2705;
        GuestLog guestLog=new GuestLog();
        guestLog.setId(st);
        Integer count=0;
        Integer count2=0;
        for (;;){
            List<GuestLog> guestLogList =guestLogService.queryByOffset(guestLog);
            if (guestLogList.isEmpty())break;
            guestLog.setId(guestLogList.get(guestLogList.size()-1).getId());
            for (GuestLog g: guestLogList){
                if (g.getId()==692){
                    System.out.print(GeoIP.getIp(g.getIp()));
                }
                if (!GeoIP.isIp(g.getIp()))continue;
                try {
                    AsnResponse response = Geoip2.getInstance().getAsn(g.getIp());
                    g.setIp(g.getIp()+" "+ GeoIP.getCNASNName(response.getAutonomousSystemOrganization()));
                    guestLogService.update(g);
                    count++;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        System.out.printf("完成,%s,%s\n",count,count2);
    }
}

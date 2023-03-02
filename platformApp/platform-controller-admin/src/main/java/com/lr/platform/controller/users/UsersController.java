package com.lr.platform.controller.users;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.solveRecords.SolveRecords;
import com.lr.platform.entity.solveRecords.WhereRecords;
import com.lr.platform.entity.users.UserUpdateAdminVo;
import com.lr.platform.entity.users.Users;
import com.lr.platform.entity.users.UsersAdminVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import com.lr.platform.service.SolveRecordsService;
import com.lr.platform.service.UsersService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Security;
import com.lr.platform.utils.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * (Users)表控制层
 *
 * @author makejava
 * @since 2022-07-02 17:44:56
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("users")
public class UsersController {
    /**
     * 服务对象
     */
    @Resource
    private UsersService usersService;

    @Resource
    private SolveRecordsService solveRecordsService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "新增用户")
    @ManagerLog(tag = "新增用户",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/create")
    public Response createUsers(@RequestBody Users users){
        // TODO 应该不用做,空着
        return null;
    }

    @ApiOperation(value = "修改用户")
    @ManagerLog(tag = "修改用户",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateUsers(@RequestBody UserUpdateAdminVo userUpdateAdminVo){
        if (!usersService.exists(userUpdateAdminVo.getId())) {
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        if (jwtClaims.getUid()!=1){
            userUpdateAdminVo.setEmail(null);
        }else if (!Validator.isValidEmail(userUpdateAdminVo.getEmail())){
            userUpdateAdminVo.setEmail(null);
        }
        if (!Validator.isValidQQ(userUpdateAdminVo.getQq())){
            /*
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_QQ)
                    .build();
            */
            userUpdateAdminVo.setQq(null);
        }

        if (!Validator.isValidName(userUpdateAdminVo.getName())){
            /*
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_NAME)
                    .build();

             */
            userUpdateAdminVo.setName(null);
        }
        if (!Validator.isValidPhone(userUpdateAdminVo.getPhone())){
            /*
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_PHONE)
                    .build();

             */
            userUpdateAdminVo.setPhone(null);
        }
        String password = userUpdateAdminVo.getPassword();
        if (!Validator.isValidPassword(password)) {
            userUpdateAdminVo.setPassword(null);
        }else {
            userUpdateAdminVo.setPassword(Security.bCryptEncodePassword(password));
        }
        if (!Validator.isValidStudentNumber(userUpdateAdminVo.getStudentNum())){
            userUpdateAdminVo.setStudentNum(null);
            userUpdateAdminVo.setGrade(null);
        }else {
            userUpdateAdminVo.setGrade(userUpdateAdminVo.getStudentNum().substring(0, 4));
        }
        Users users= Dozer.convert(userUpdateAdminVo,Users.class);
        usersService.update(users);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "删除用户")
    @ManagerLog(tag = "删除用户",level = WarningLevel.COMMON_DELETE)
    @GetMapping("/delete")
    public Response deleteProblems(@RequestParam("id") Integer id){
        Users users = usersService.queryById(id);
        if (Objects.equals(users, null)) {
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        users.setDeletedAt(new Date());
        usersService.update(users);
        WhereRecords whereRecords=new WhereRecords();
        whereRecords.setUserId(id);
        SolveRecords solveRecords=new SolveRecords();
        solveRecords.setDeletedAt(new Date());
        solveRecordsService.updateBySolveRecords(solveRecords,whereRecords);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "分页获取用户")
    @GetMapping("/page")
    public Response selectProblemsPage(@RequestParam("pageSize") Integer pageSize,
                                       @RequestParam("current") Integer current,
                                       @RequestParam(value = "query",required = false) String query){
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
        Users users=new Users();
        if (query!=null){
            users.setUsername(query+"%");
        }
        PageHelper.startPage(current,pageSize);
        List<Users> usersList=usersService.queryByQueryUser(users);
        PageInfo<Users> pageInfo=new PageInfo<>(usersList);
        List<UsersAdminVo> usersAdminVoList=Dozer.convert(pageInfo.getList(),UsersAdminVo.class);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len((int) pageInfo.getTotal())
                .data(usersAdminVoList)
                .build();
    }

    @ApiOperation(value = "获取所有年级",notes = "返回数组, 年级的所有可能取值")
    @GetMapping("/grade")
    public Response getGrade(){
        List<Users> usersList=usersService.queryAllGrade(new Users());
        List<String> grade=new LinkedList<>();
        for (Users u:usersList){
            if (u!=null) {
                grade.add(u.getGrade());
            }else {
                grade.add(null);
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(grade)
                .build();
    }


}


package com.west2java.netdisk.controller;

import com.alibaba.fastjson.JSONObject;
import com.west2java.netdisk.dao.UserDao;
import com.west2java.netdisk.entity.Directory;
import com.west2java.netdisk.entity.User;
import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.service.DirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@Tag(name = "user-api", description = "用户相关的api")
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private DirectoryService directoryService;
    /**
     * 根据用户名和密码注册
     */
    @Operation(summary = "注册",
            parameters = {
                    @Parameter(name = "username", description = "用户名"),
                    @Parameter(name = "password", description= "密码")
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PostMapping("/register")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户名和密码",required = true)
    public ResponseData register(@RequestBody String jsonStr) {
        String username = JSONObject.parseObject(jsonStr).getString("username");
        String password = JSONObject.parseObject(jsonStr).getString("password");
        User user = new User(username,password);
        try {
            //检查用户名是否已被使用
            User user_ = userDao.findByUsername(user.getUsername());
            if (user_ != null) {
                return new ResponseData(ExceptionMsg.UserNameUsed);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            //密码加密
            user.setPassword(encoder.encode(user.getPassword()));
            //设置角色
            user.setRoles("ROLE_USER");
            //存入数据库
            User u = userDao.save(user);
            int userId = u.getId();//取id
            //新建用户根目录
            Directory dir = new Directory("root",userId);//为用户创建根目录
            Integer rootId = directoryService.create(dir).getId();
            u.setPassword(password);//返回密码明文
            log.info(u.getUsername()+"注册成功");
            JSONObject json  = new JSONObject();
            json.put("userId",u.getId());
            json.put("username",u.getUsername());
            json.put("password",u.getPassword());
            json.put("roles",u.getRoles());
            json.put("rootId",rootId);
            return new ResponseData(ExceptionMsg.SUCCESS,json);
        } catch (Exception e) {
            return new ResponseData(ExceptionMsg.FAILED);
        }
    }
}

package com.west2java.netdisk.config.authenticationhandler;

import com.alibaba.fastjson.JSONObject;
import com.west2java.netdisk.dao.DirectoryDao;
import com.west2java.netdisk.entity.Directory;
import com.west2java.netdisk.entity.User;
import com.west2java.netdisk.service.DirectoryService;
import com.west2java.netdisk.service.UserService;
import com.west2java.netdisk.util.JwtTokenUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

@Component("jwtAuthenticationSuccessHandler")
@Slf4j
public class JwtAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    UserService userService;
    @Autowired
    DirectoryService directoryService;
    //用户名和密码正确执行
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof UserDetails) {
            UserDetails user = (UserDetails) principal;
            httpServletRequest.getSession().setAttribute("userDetail", user);
            String role = "";
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            for (GrantedAuthority authority : authorities){
                role = authority.getAuthority();
            }


            String token = JwtTokenUtils.createToken(user.getUsername(), role, true);
            System.out.println("role "+role+" login");
            // 返回创建成功的token
            httpServletResponse.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
            httpServletResponse.setContentType("application/json;charset=utf-8");
            PrintWriter out = httpServletResponse.getWriter();
            //
            String username = user.getUsername();
            User u = userService.getUserByUsername(username);
            Directory rootDir = directoryService.getRootDirByUserId(u.getId());
            JSONObject json  = new JSONObject();
            json.put("status","ok");
            json.put("role",role);
            json.put("id",u.getId());
            json.put("username",user.getUsername());
            json.put("rootId",rootDir.getId());
            json.put("message","登录成功");
            String str = json.toJSONString();
            out.write(str);
            out.flush();
            out.close();
        }
    }

}

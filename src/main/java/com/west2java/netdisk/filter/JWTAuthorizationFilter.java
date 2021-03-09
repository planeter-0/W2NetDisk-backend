package com.west2java.netdisk.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.util.JwtTokenUtils;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        //没有token，不进行token验证
//        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
//            logger.info("无tokenHeader");
//
//            chain.doFilter(request, response);
//            return;
//        }
        // 如果请求头中有token，解析，设置认证信息
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        if (tokenHeader != null) {
            if (tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
                if (getAuthentication(tokenHeader) != null) {
                    SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
                    super.doFilterInternal(request, response, chain);
                    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    logger.info("token认证成功");
                    return;
                }
            }
//        }else{
//            log.warn("无tokenHeader");
//            ServletOutputStream out = response.getOutputStream();
//            out.print(new ResponseData(ExceptionMsg.NO_TOKEN).toString());
//            out.flush();
//            out.close();
        }
    }

    // tokenHeader -> token对象
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        try {
            String username = JwtTokenUtils.getUsername(token);
        } catch (SignatureException e){
            log.error("token不匹配");
        }
        String username = JwtTokenUtils.getUsername(token);
        String role = JwtTokenUtils.getUserRole(token);
        if (username != null) {
            return new UsernamePasswordAuthenticationToken(username, null,
                    Collections.singleton(new SimpleGrantedAuthority(role))
            );
        }
        return null;
    }
}

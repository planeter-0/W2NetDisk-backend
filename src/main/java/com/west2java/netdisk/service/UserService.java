package com.west2java.netdisk.service;

import com.west2java.netdisk.dao.UserDao;
import com.west2java.netdisk.entity.User;
import com.west2java.netdisk.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        return user;
    }
    public User getUserByUsername(String username){
        return userDao.findByUsername(username);
    }
}

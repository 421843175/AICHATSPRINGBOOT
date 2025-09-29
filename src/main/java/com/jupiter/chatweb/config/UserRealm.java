package com.jupiter.chatweb.config;


import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.service.impl.UserServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的UserRelm 这里要写授权和认证权限
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权=>doGetAuthorizationInfo()");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //拿到当前登录的对象
        Subject subject = SecurityUtils.getSubject();
        //拿到数据库查到的这个user
        UserEntity currentUser = (UserEntity) subject.getPrincipal();

        //增加vip权限
        info.addStringPermission(currentUser.getPerms());
        //
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了认证doGetAuthenticationInfo()");

        UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken) token;

        UserEntity user = userService.selectone(usernamePasswordToken.getUsername());
        if(user==null){//没有这个人
            return null;

        }


        //密码认证
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}

package com.jupiter.chatweb.config;


import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    //该BEAN防止出现Consider defining a bean named 'shiroFilterFactoryBean' in your configuration.问题
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("mysecurityManager")DefaultWebSecurityManager securityManager)
    {
        ShiroFilterFactoryBean bean=new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(securityManager);


        //添加shiro内置的过滤器
        /**
         anon:无需认证就可以访问
         authc:必须认证才可访问
         user：必须拥有   记住我才能用
         perms：拥有对某个资源的权限才能访问
         role:拥有某个角色权限才能访问
         **/
        Map<String,String> filterMap=new LinkedHashMap<>();
//        filterMap.put("/user/add","authc");
//        filterMap.put("/user/update","authc");
        //授权，vip才可以访问


        /*
        * 1	admin	123456	user:vip
          2	user	1112	*/
//        filterMap.put("/user/add","perms[user:vip]");

//       TODO:NOTICE  登录和注册不拦截
        filterMap.put("/user/login", "anon");
        filterMap.put("/user/register", "anon");


//        filterMap.put("/user/*","authc");
        bean.setFilterChainDefinitionMap(filterMap);

        //设置登录的请求(没有权限往哪跳转)
        bean.setLoginUrl("/user/login");

        //未授权跳转
        bean.setUnauthorizedUrl("/noauth");


        return bean;
    }
    //DefaultWebSecurityManager:2   安全对象 创建管理用户需要用到
    @Bean(name="mysecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }


    //创建realm对象，需要自定义的类
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }



}

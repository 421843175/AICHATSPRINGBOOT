package com.jupiter.chatweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jupiter.chatweb.entity.FriendshipsEntity;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.mapper.FriendshipsDao;
import com.jupiter.chatweb.mapper.UserMapper;
import com.jupiter.chatweb.service.GoodsService;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.EncryTool;
import com.jupiter.chatweb.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FriendshipsDao friendshipsDao;

    @Autowired
    @Lazy
    private GoodsService goodsService;

    @Override
    public UserEntity selectone(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public AjaxResult register(UserEntity user) {
        log.info("user={}",user);
        if(user==null){
            return new AjaxResult(400,"用户为空");
        }
        // 检查密码是否符合要求
        String password = user.getPassword().replaceAll(" ", "");
        String username = user.getUsername().replaceAll(" ", "");
        String nickname = user.getNick().replaceAll(" ", "");

        if(password.equals("") || username.equals("") || nickname.equals("")){
            return new AjaxResult(400,"用户名、密码或昵称为空");
        }

        //判断是否有这个用户
        UserEntity userEntity = userMapper.selectByUsername(username);
        if(userEntity!=null){
            return new AjaxResult(405,"该用户已经存在");

        }
        //存入数据
        UserEntity newUser = new UserEntity();
        newUser.setNick(nickname);
        //再进行MD5加密
        newUser.setPassword(EncryTool.getDoubleMd5(password));

        newUser.setUsername(username);
        newUser.setLogintime(new Date());
        newUser.setRole(user.getRole());

//        设置默认头像
        newUser.setHead("https://img.88icon.com/download/jpg/20210107/400b226fbbb6b3d710cf1ceb00add1a1_512_512.jpg!bg");

        log.info("newUser=>:{}",newUser);

        int insert = userMapper.insert(newUser);




        if(insert!=1) {
            return AjaxResult.error("注册失败");
        }

        //        如果注册的用户不是客服 那么与所有客服为好友关系
        if(user.getRole()!=0){
            List<UserEntity> customs = userMapper.selectList(new QueryWrapper<UserEntity>().eq("role", 0));
            customs.forEach(custom->{
                goodsService.toChat(username,custom.getUsername(),null);
            });
        }

        return AjaxResult.success("注册成功");

    }

    @Override
    public AjaxResult login(UserEntity user) {
        Subject subject = SecurityUtils.getSubject();

        if(user.getUsername().equals("") || user.getPassword().equals("")){
            return new AjaxResult(400,"用户名或密码为空");
        }

        //查看数据库
        UserEntity userEntity = userMapper.selectByUsername(user.getUsername());
        if(userEntity==null){
            return AjaxResult.error("用户名不存在");
        }

        //封装用户的登录数据 MD5
        UsernamePasswordToken token =
                new UsernamePasswordToken(user.getUsername()
                        , EncryTool.getDoubleMd5(user.getPassword()));
        try{
            subject.login(token);
        }catch (UnknownAccountException e)
        {
           return AjaxResult.error("用户名不存在");
        }catch (IncorrectCredentialsException e)//密码不存在
        {
            return AjaxResult.error("密码不正确");
        }


//        token
        user.setRole(userEntity.getRole());
        return AjaxResult.success(TokenUtils.sign(user.getUsername()
                , LocalDateTime.now().toString(),user.getRole()));
    }

    // UserService中添加方法
    public UserEntity getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
    }

    @Override
    public AjaxResult<String> updateUser(String username,String nick, String avatar, String oldPassword, String newPassword) {
        // 验证原密码
        UserEntity user = userMapper.selectByUsername(username);


        if (!EncryTool.getDoubleMd5(oldPassword).equals(user.getPassword())) {
            return AjaxResult.error("原密码错误");
        }

        // 更新信息
        if(!nick.equals(""))
        user.setNick(nick);
        if(!avatar.equals(""))
        user.setHead(avatar);


        if(newPassword!=null){
            if (StringUtils.hasText(newPassword)) {
                user.setPassword(EncryTool.getDoubleMd5(newPassword));
            }
        }


        userMapper.updateById(user);

//         设置与它为好友关系的好友表的头像
        QueryWrapper<FriendshipsEntity> username1 = new QueryWrapper<FriendshipsEntity>().eq("username1", username);


        List<FriendshipsEntity> friendshipsEntities = friendshipsDao.selectList(username1);
        friendshipsEntities.forEach(friendships->{
            friendships.setUsername1head(avatar);
            friendshipsDao.updateById(friendships);
        });


        QueryWrapper<FriendshipsEntity> username2 = new QueryWrapper<FriendshipsEntity>().eq("username2", username);


        List<FriendshipsEntity> friendshipsEntities2 = friendshipsDao.selectList(username2);
        friendshipsEntities2.forEach(friendships->{
            friendships.setUsername2head(avatar);
            friendshipsDao.updateById(friendships);
        });


        return AjaxResult.success("修改成功");

    }


}

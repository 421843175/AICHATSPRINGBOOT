package com.jupiter.chatweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jupiter.chatweb.entity.FriendshipsEntity;
import com.jupiter.chatweb.entity.GoodsEntity;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.mapper.FriendshipsDao;
import com.jupiter.chatweb.mapper.GoodsDao;
import com.jupiter.chatweb.pojo.GoodsUpdateDTO;
import com.jupiter.chatweb.service.ChatService;
import com.jupiter.chatweb.service.GoodsService;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    @Autowired
    FriendshipsDao friendshipsDao;

    @Autowired
    FileUploadService fileUploadService;

    @Override
    public AjaxResult<JSONArray> getAllGoods(){
//        商品必须上架
        QueryWrapper<GoodsEntity> isList = new QueryWrapper<GoodsEntity>().eq("isList", 1);
        List<GoodsEntity> goodsEntities = goodsDao.selectList(isList);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(goodsEntities);
        return AjaxResult.success(jsonArray);
    }

    //去聊天
    public AjaxResult<String> toChat(String username, String merchant, Integer goodsId) {
        // 1. 验证用户存在性
        UserEntity user = userService.getByUsername(username);
        UserEntity merchantUser = userService.getByUsername(merchant);

        if (user == null ||merchantUser==null) {
            return AjaxResult.error("用户或商家不存在");
        }

        // 2. 确定用户名顺序
        String username1 = username.compareTo(merchant) < 0 ? username : merchant;
        String username2 = username.compareTo(merchant) < 0 ? merchant : username;

        // 3. 检查关系是否存在
        FriendshipsEntity existingRelation = getExistingFriendship(username1, username2);



        if (existingRelation == null) {
            // 4. 创建新好友关系
            FriendshipsEntity entity = new FriendshipsEntity();
            entity.setUsername1(username1);
            entity.setUsername2(username2);
            entity.setCreateAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            entity.setUser1unreadnum(0);
            entity.setUser2unreadnum(0);
            entity.setIsRole1(user.getRole());
            entity.setIsRole2(merchantUser.getRole());

            entity.setUsername1head(user.getHead());
            entity.setUsername2head(merchantUser.getHead());

            entity.setGoodsId(goodsId);


            entity.setIsRobot(1); // 假设默认是机器人
            friendshipsDao.insert(entity);
        }else{
            return updateGoodsId(existingRelation, goodsId);
        }

        //TODO:如果存在关系更新GOODS_id
        return AjaxResult.success("进入聊天");

    }
    // 新增方法：获取已存在的好友关系
    private FriendshipsEntity getExistingFriendship(String user1, String user2) {
        return friendshipsDao.selectOne(new QueryWrapper<FriendshipsEntity>()
                .eq("username1", user1)
                .eq("username2", user2));
    }
    // 新增方法：更新商品ID
    private AjaxResult<String> updateGoodsId(FriendshipsEntity relation, Integer goodsId) {
        try {
            // 创建更新对象
            FriendshipsEntity updateEntity = new FriendshipsEntity();
            updateEntity.setId(relation.getId());
            updateEntity.setGoodsId(goodsId);
            updateEntity.setCreateAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // 执行更新
            int result = friendshipsDao.updateById(updateEntity);

            return result > 0 ?
                    AjaxResult.success("商品信息已更新") :
                    AjaxResult.error("更新商品信息失败");
        } catch (Exception e) {
            return AjaxResult.error("更新异常: " + e.getMessage());
        }
    }

    // FriendshipsServiceImpl.java
    public AjaxResult<GoodsEntity> getGoodsByUsernames(String userA, String userB) {
        // 确保用户名顺序
        String[] sortedUsers = Stream.of(userA, userB)
                .sorted()
                .toArray(String[]::new);

        FriendshipsEntity friendship = friendshipsDao.selectOne(
                new QueryWrapper<FriendshipsEntity>()
                        .eq("username1", sortedUsers[0])
                        .eq("username2", sortedUsers[1]));

        if (friendship == null) {
            return AjaxResult.error("对话关系不存在");
        }

        if (friendship.getGoodsId() == null) {
            return AjaxResult.success(new GoodsEntity()); // 返回空对象
        }

        GoodsEntity goods = goodsDao.selectById(friendship.getGoodsId());
        return goods != null ?
                AjaxResult.success(goods) :
                AjaxResult.error("关联商品不存在");
    }


@Override
    public AjaxResult getSellerGoods(String usertoken, Integer page, Integer size) {
        String merchant = TokenUtils.getLoginName(usertoken);
        Page<GoodsEntity> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<GoodsEntity> query = Wrappers.lambdaQuery();
        query.eq(GoodsEntity::getMerchant, merchant)
                .orderByDesc(GoodsEntity::getDate);
    Page<GoodsEntity> goodsEntityPage = goodsDao.selectPage(pageInfo, query);
    return AjaxResult.success(goodsEntityPage);
    }

    @Override
    public AjaxResult updateGoodsInfo(GoodsUpdateDTO dto,String username) {
        QueryWrapper<GoodsEntity> merchant = new QueryWrapper<GoodsEntity>().eq("merchant", username)
                .eq("id",dto.getId());

        // 验证商品归属
        GoodsEntity exist = goodsDao.selectOne(merchant);
        if (exist==null) {
            return AjaxResult.error("无修改权限");
        }

        GoodsEntity update = new GoodsEntity();
        update.setId(dto.getId());
        update.setName(dto.getName());
        update.setPrice(dto.getPrice());
        if (dto.getSrc() != null) {
            update.setSrc(dto.getSrc());
        }
        return AjaxResult.success(goodsDao.updateById(update));
    }

    @Override
    public AjaxResult uploadGoodsImage(MultipartFile file, Integer goodsId, HttpServletRequest request) {
        return fileUploadService.uploadFile(file, request, "goods");
    }

    @Override
    public AjaxResult updateGoodsStatus(Integer id, Integer status, String loginName) {
        // 2. 验证商品存在性
        GoodsEntity existGoods = goodsDao.selectById(id);
        if (existGoods == null) {
            return AjaxResult.error("商品不存在");
        }

        // 3. 验证商品归属
        if (!existGoods.getMerchant().equals(loginName)) {
            return AjaxResult.error("无权修改该商品状态");
        }


        // 4. 验证状态值合法性
        if (status != 0 && status != 1) {
            return AjaxResult.error("非法的状态值");
        }


        // 5. 执行更新
        GoodsEntity update = new GoodsEntity();
        update.setId(id);
        update.setIslist(status);

        int result = goodsDao.updateById(update);

        // 6. 返回结果
        return result > 0
                ? AjaxResult.success("状态更新成功")
                : AjaxResult.error("状态未发生变化");


    }


    @Override
    public AjaxResult deleteGoods(Integer id, String merchant) {
        try {
            // 验证商品存在性
            GoodsEntity exist = goodsDao.selectById(id);
            if (exist == null) {
                return AjaxResult.error("商品不存在");
            }

            // 验证商品归属
            if (!exist.getMerchant().equals(merchant)) {
                return AjaxResult.error("无权删除该商品");
            }

            // 执行删除
            int result = goodsDao.deleteById(id);

            return result > 0
                    ? AjaxResult.success("商品删除成功")
                    : AjaxResult.error("商品删除失败");

        } catch (Exception e) {
            log.error("商品删除异常: {}", e.getMessage());
            return AjaxResult.error("服务器内部错误");
        }
    }

    @Override
    public AjaxResult createGoods(String merchant, String name, BigDecimal price, String src) {
        try {
            // 参数校验
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("价格必须大于0");
            }

            // 创建商品实体
            GoodsEntity goods = new GoodsEntity();
            goods.setMerchant(merchant);
            goods.setName(name);
            goods.setPrice(price);
            goods.setSrc(src != null ? src : "");
            goods.setDate(new Date());
            goods.setIslist(0); // 默认下架状态

            int result = goodsDao.insert(goods);

            return result > 0
                    ? AjaxResult.success( goods.getId())
                    : AjaxResult.error("商品创建失败");

        } catch (Exception e) {
            log.error("商品创建异常: {}", e.getMessage());
            return AjaxResult.error("服务器内部错误");
        }
    }

}

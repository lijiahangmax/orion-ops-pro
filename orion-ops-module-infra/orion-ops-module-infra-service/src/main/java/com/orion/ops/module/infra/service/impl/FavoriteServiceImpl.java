package com.orion.ops.module.infra.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.collect.Lists;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.redis.core.utils.RedisUtils;
import com.orion.ops.module.infra.convert.FavoriteConvert;
import com.orion.ops.module.infra.dao.FavoriteDAO;
import com.orion.ops.module.infra.define.FavoriteCacheKeyDefine;
import com.orion.ops.module.infra.entity.domain.FavoriteDO;
import com.orion.ops.module.infra.entity.request.favorite.FavoriteCreateRequest;
import com.orion.ops.module.infra.entity.request.favorite.FavoriteQueryRequest;
import com.orion.ops.module.infra.entity.vo.FavoriteVO;
import com.orion.ops.module.infra.enums.FavoriteTypeEnum;
import com.orion.ops.module.infra.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-1 10:30
 */
@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteDAO favoriteDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Long addFavorite(FavoriteCreateRequest request) {
        // 转换
        FavoriteDO record = FavoriteConvert.MAPPER.to(request);
        // 插入
        int effect = favoriteDAO.insert(record);
        log.info("FavoriteService-addFavorite effect: {}, record: {}", effect, JSON.toJSONString(record));
        // 设置缓存
        String key = FavoriteCacheKeyDefine.FAVORITE.format(request.getType(), request.getUserId());
        RedisUtils.listPush(key, request.getRelId(), String::valueOf);
        // 设置过期时间
        RedisUtils.setExpire(key, FavoriteCacheKeyDefine.FAVORITE);
        return record.getId();
    }

    @Override
    public List<FavoriteVO> getFavoriteList(FavoriteQueryRequest request) {
        // 条件
        LambdaQueryWrapper<FavoriteDO> wrapper = this.buildQueryWrapper(request);
        // 查询
        return favoriteDAO.of(wrapper).list(FavoriteConvert.MAPPER::to);
    }

    @Override
    public List<Long> getFavoriteRelIdList(FavoriteQueryRequest request) {
        String type = request.getType();
        Long userId = request.getUserId();
        String cacheKey = FavoriteCacheKeyDefine.FAVORITE.format(type, userId);
        // 获取缓存
        List<Long> cacheRelIdList = RedisUtils.listRange(cacheKey, Long::valueOf);
        if (cacheRelIdList.isEmpty()) {
            // 条件
            LambdaQueryWrapper<FavoriteDO> wrapper = this.buildQueryWrapper(request);
            // 查询数据库
            cacheRelIdList = favoriteDAO.of(wrapper)
                    .stream()
                    .map(FavoriteDO::getRelId)
                    .distinct()
                    .collect(Collectors.toList());
            // 添加默认值 防止穿透
            if (cacheRelIdList.isEmpty()) {
                cacheRelIdList.add(Const.NONE_ID);
            }
            // 设置缓存
            RedisUtils.listPushAll(cacheKey, cacheRelIdList, String::valueOf);
            // 设置过期时间
            RedisUtils.setExpire(cacheKey, FavoriteCacheKeyDefine.FAVORITE);
        }
        // 删除防止穿透的 key
        cacheRelIdList.remove(Const.NONE_ID);
        return cacheRelIdList;
    }

    @Override
    public void deleteFavoriteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        // 删除缓存
        List<String> favoriteKeyList = Arrays.stream(FavoriteTypeEnum.values())
                .map(s -> FavoriteCacheKeyDefine.FAVORITE.format(s, userId))
                .collect(Collectors.toList());
        redisTemplate.delete(favoriteKeyList);
        // 删除库
        FavoriteQueryRequest request = new FavoriteQueryRequest();
        request.setUserId(userId);
        favoriteDAO.delete(this.buildQueryWrapper(request));
    }

    @Override
    public void deleteFavoriteByUserIdList(List<Long> userIdList) {
        if (Lists.isEmpty(userIdList)) {
            return;
        }
        // 删除缓存
        List<String> favoriteKeyList = new ArrayList<>();
        for (Long userId : userIdList) {
            Arrays.stream(FavoriteTypeEnum.values())
                    .map(s -> FavoriteCacheKeyDefine.FAVORITE.format(s, userId))
                    .forEach(favoriteKeyList::add);
        }
        redisTemplate.delete(favoriteKeyList);
        // 删除库
        FavoriteQueryRequest request = new FavoriteQueryRequest();
        request.setUserIdList(userIdList);
        favoriteDAO.delete(this.buildQueryWrapper(request));
    }

    @Override
    public void deleteFavoriteByRelId(String type, Long relId) {
        if (relId == null) {
            return;
        }
        FavoriteQueryRequest request = new FavoriteQueryRequest();
        request.setType(type);
        request.setRelId(relId);
        // 只删除数据库 redis 等自动失效
        favoriteDAO.delete(this.buildQueryWrapper(request));
    }

    @Override
    public void deleteFavoriteByRelIdList(String type, List<Long> relIdList) {
        if (Lists.isEmpty(relIdList)) {
            return;
        }
        FavoriteQueryRequest request = new FavoriteQueryRequest();
        request.setType(type);
        request.setRelIdList(relIdList);
        // 只删除数据库 redis 等自动失效
        favoriteDAO.delete(this.buildQueryWrapper(request));
    }

    /**
     * 构建查询 wrapper
     *
     * @param request request
     * @return wrapper
     */
    private LambdaQueryWrapper<FavoriteDO> buildQueryWrapper(FavoriteQueryRequest request) {
        return favoriteDAO.wrapper()
                .eq(FavoriteDO::getId, request.getId())
                .eq(FavoriteDO::getUserId, request.getUserId())
                .eq(FavoriteDO::getRelId, request.getRelId())
                .eq(FavoriteDO::getType, request.getType())
                .in(FavoriteDO::getUserId, request.getUserIdList())
                .in(FavoriteDO::getRelId, request.getRelIdList());
    }

}
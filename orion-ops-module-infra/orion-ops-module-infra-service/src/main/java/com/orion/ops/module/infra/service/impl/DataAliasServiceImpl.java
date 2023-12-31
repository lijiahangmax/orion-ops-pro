package com.orion.ops.module.infra.service.impl;

import com.orion.lang.utils.Refs;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.framework.redis.core.utils.RedisMaps;
import com.orion.ops.framework.redis.core.utils.barrier.CacheBarriers;
import com.orion.ops.module.infra.constant.DataExtraItems;
import com.orion.ops.module.infra.define.cache.DataExtraCacheKeyDefine;
import com.orion.ops.module.infra.entity.request.data.DataAliasUpdateRequest;
import com.orion.ops.module.infra.entity.request.data.DataExtraQueryRequest;
import com.orion.ops.module.infra.entity.request.data.DataExtraSetRequest;
import com.orion.ops.module.infra.service.DataAliasService;
import com.orion.ops.module.infra.service.DataExtraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据别名 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-12-18 17:37
 */
@Slf4j
@Service
public class DataAliasServiceImpl implements DataAliasService {

    @Resource
    private DataExtraService dataExtraService;

    @Override
    public Integer updateDataAlias(DataAliasUpdateRequest request) {
        Long userId = request.getUserId();
        String type = request.getType();
        // 更新
        DataExtraSetRequest update = new DataExtraSetRequest();
        update.setUserId(userId);
        update.setRelId(request.getRelId());
        update.setType(type);
        update.setItem(DataExtraItems.ALIAS);
        update.setValue(Refs.json(request.getAlias()));
        Integer effect = dataExtraService.setExtraItem(update);
        // 删除缓存
        RedisMaps.delete(DataExtraCacheKeyDefine.DATA_ALIAS.format(userId, type));
        return effect;
    }

    @Override
    public String getDataAlias(Long userId, String type, Long relId) {
        return this.getDataAlias(userId, type).get(relId);
    }

    @Override
    public Map<Long, String> getDataAlias(Long userId, String type) {
        // 查询缓存
        String key = DataExtraCacheKeyDefine.DATA_ALIAS.format(userId, type);
        Map<String, String> entities = RedisMaps.entities(key);
        if (Maps.isEmpty(entities)) {
            // 查询数据库
            DataExtraQueryRequest request = DataExtraQueryRequest.builder()
                    .userId(userId)
                    .type(type)
                    .item(DataExtraItems.ALIAS)
                    .build();
            Map<Long, String> extras = dataExtraService.getExtraItemValues(request);
            entities = Maps.map(extras, String::valueOf, Refs::unrefToString);
            // 设置屏障 防止穿透
            CacheBarriers.MAP.check(entities);
            // 设置缓存
            RedisMaps.putAll(key, DataExtraCacheKeyDefine.DATA_ALIAS, entities);
        }
        // 删除屏障
        CacheBarriers.MAP.remove(entities);
        // 转换
        return Maps.map(entities, Long::valueOf, Function.identity());
    }

}

package com.orion.ops.module.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.ops.framework.biz.operator.log.core.utils.OperatorLogs;
import com.orion.ops.framework.common.constant.ErrorMessage;
import com.orion.ops.framework.common.security.PasswordModifier;
import com.orion.ops.framework.common.utils.CryptoUtils;
import com.orion.ops.framework.common.utils.Valid;
import com.orion.ops.framework.redis.core.utils.RedisMaps;
import com.orion.ops.framework.redis.core.utils.barrier.CacheBarriers;
import com.orion.ops.module.asset.convert.HostIdentityConvert;
import com.orion.ops.module.asset.dao.HostConfigDAO;
import com.orion.ops.module.asset.dao.HostIdentityDAO;
import com.orion.ops.module.asset.dao.HostKeyDAO;
import com.orion.ops.module.asset.define.cache.HostCacheKeyDefine;
import com.orion.ops.module.asset.entity.domain.HostIdentityDO;
import com.orion.ops.module.asset.entity.domain.HostKeyDO;
import com.orion.ops.module.asset.entity.dto.HostIdentityCacheDTO;
import com.orion.ops.module.asset.entity.request.host.HostIdentityCreateRequest;
import com.orion.ops.module.asset.entity.request.host.HostIdentityQueryRequest;
import com.orion.ops.module.asset.entity.request.host.HostIdentityUpdateRequest;
import com.orion.ops.module.asset.entity.vo.HostIdentityVO;
import com.orion.ops.module.asset.service.HostIdentityService;
import com.orion.ops.module.infra.api.DataExtraApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 主机身份 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-20 11:55
 */
@Slf4j
@Service
public class HostIdentityServiceImpl implements HostIdentityService {

    @Resource
    private HostIdentityDAO hostIdentityDAO;

    @Resource
    private HostKeyDAO hostKeyDAO;

    @Resource
    private HostConfigDAO hostConfigDAO;

    @Resource
    private DataExtraApi dataExtraApi;

    @Override
    public Long createHostIdentity(HostIdentityCreateRequest request) {
        log.info("HostIdentityService-createHostIdentity request: {}", JSON.toJSONString(request));
        // 检查秘钥是否存在
        this.checkKeyIdPresent(request.getKeyId());
        // 转换
        HostIdentityDO record = HostIdentityConvert.MAPPER.to(request);
        // 查询数据是否冲突
        this.checkHostIdentityPresent(record);
        // 加密密码
        String password = record.getPassword();
        if (!Strings.isBlank(password)) {
            record.setPassword(CryptoUtils.encryptAsString(password));
        }
        // 插入
        int effect = hostIdentityDAO.insert(record);
        log.info("HostIdentityService-createHostIdentity effect: {}", effect);
        // 删除缓存
        RedisMaps.delete(HostCacheKeyDefine.HOST_IDENTITY);
        return record.getId();
    }

    @Override
    public Integer updateHostIdentityById(HostIdentityUpdateRequest request) {
        log.info("HostIdentityService-updateHostIdentityById request: {}", JSON.toJSONString(request));
        // 查询
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        HostIdentityDO record = hostIdentityDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 检查秘钥是否存在
        this.checkKeyIdPresent(request.getKeyId());
        // 转换
        HostIdentityDO updateRecord = HostIdentityConvert.MAPPER.to(request);
        // 查询数据是否冲突
        this.checkHostIdentityPresent(updateRecord);
        // 设置密码
        String newPassword = PasswordModifier.getEncryptNewPassword(request);
        updateRecord.setPassword(newPassword);
        // 更新
        LambdaUpdateWrapper<HostIdentityDO> wrapper = Wrappers.<HostIdentityDO>lambdaUpdate()
                .set(HostIdentityDO::getKeyId, request.getKeyId())
                .eq(HostIdentityDO::getId, id);
        int effect = hostIdentityDAO.update(updateRecord, wrapper);
        log.info("HostIdentityService-updateHostIdentityById effect: {}", effect);
        // 删除缓存
        if (!record.getName().equals(updateRecord.getName()) ||
                !record.getUsername().equals(updateRecord.getUsername())) {
            RedisMaps.delete(HostCacheKeyDefine.HOST_IDENTITY);
        }
        return effect;
    }

    @Override
    public HostIdentityVO getHostIdentityById(Long id) {
        // 查询
        HostIdentityDO record = hostIdentityDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        return HostIdentityConvert.MAPPER.to(record);
    }

    @Override
    public List<HostIdentityVO> getHostIdentityList() {
        // 查询缓存
        List<HostIdentityCacheDTO> list = RedisMaps.valuesJson(HostCacheKeyDefine.HOST_IDENTITY);
        if (list.isEmpty()) {
            // 查询数据库
            list = hostIdentityDAO.of().list(HostIdentityConvert.MAPPER::toCache);
            // 设置屏障 防止穿透
            CacheBarriers.checkBarrier(list, HostIdentityCacheDTO::new);
            // 设置缓存
            RedisMaps.putAllJson(HostCacheKeyDefine.HOST_IDENTITY, s -> s.getId().toString(), list);
        }
        // 删除屏障
        CacheBarriers.removeBarrier(list);
        // 转换
        return list.stream()
                .map(HostIdentityConvert.MAPPER::to)
                .sorted(Comparator.comparing(HostIdentityVO::getId))
                .collect(Collectors.toList());
    }

    @Override
    public DataGrid<HostIdentityVO> getHostIdentityPage(HostIdentityQueryRequest request) {
        // 条件
        LambdaQueryWrapper<HostIdentityDO> wrapper = this.buildQueryWrapper(request);
        // 查询
        DataGrid<HostIdentityVO> dataGrid = hostIdentityDAO.of(wrapper)
                .page(request)
                .dataGrid(HostIdentityConvert.MAPPER::to);
        if (dataGrid.isEmpty()) {
            return dataGrid;
        }
        // 设置秘钥名称
        List<Long> keyIdList = dataGrid.stream()
                .map(HostIdentityVO::getKeyId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!keyIdList.isEmpty()) {
            // 查询秘钥名称
            Map<Long, String> keyNameMap = hostKeyDAO.selectBatchIds(keyIdList)
                    .stream()
                    .collect(Collectors.toMap(HostKeyDO::getId, HostKeyDO::getName));
            dataGrid.forEach(s -> {
                if (s.getKeyId() == null) {
                    return;
                }
                s.setKeyName(keyNameMap.get(s.getKeyId()));
            });
        }
        return dataGrid;
    }

    @Override
    public Integer deleteHostIdentityById(Long id) {
        log.info("HostIdentityService-deleteHostIdentityById id: {}", id);
        // 检查数据是否存在
        HostIdentityDO record = hostIdentityDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 添加日志参数
        OperatorLogs.add(OperatorLogs.NAME, record.getName());
        // 删除数据库
        int effect = hostIdentityDAO.deleteById(id);
        // 删除主机配置
        hostConfigDAO.setIdentityIdWithNull(id);
        // 删除主机身份额外配置
        dataExtraApi.deleteHostIdentityExtra(id);
        // 删除缓存
        RedisMaps.delete(HostCacheKeyDefine.HOST_IDENTITY.getKey(), record.getId());
        log.info("HostIdentityService-deleteHostIdentityById effect: {}", effect);
        return effect;
    }

    /**
     * 检查对象是否存在
     *
     * @param domain domain
     */
    private void checkHostIdentityPresent(HostIdentityDO domain) {
        // 构造条件
        LambdaQueryWrapper<HostIdentityDO> wrapper = hostIdentityDAO.wrapper()
                // 更新时忽略当前记录
                .ne(HostIdentityDO::getId, domain.getId())
                // 用其他字段做重复校验
                .eq(HostIdentityDO::getName, domain.getName());
        // 检查是否存在
        boolean present = hostIdentityDAO.of(wrapper).present();
        Valid.isFalse(present, ErrorMessage.DATA_PRESENT);
    }

    /**
     * 检查秘钥是否存在
     *
     * @param keyId keyId
     */
    private void checkKeyIdPresent(Long keyId) {
        if (keyId == null) {
            return;
        }
        Valid.notNull(hostKeyDAO.selectById(keyId), ErrorMessage.KEY_ABSENT);
    }

    /**
     * 构建查询 wrapper
     *
     * @param request request
     * @return wrapper
     */
    private LambdaQueryWrapper<HostIdentityDO> buildQueryWrapper(HostIdentityQueryRequest request) {
        String searchValue = request.getSearchValue();
        return hostIdentityDAO.wrapper()
                .eq(HostIdentityDO::getId, request.getId())
                .like(HostIdentityDO::getName, request.getName())
                .like(HostIdentityDO::getUsername, request.getUsername())
                .eq(HostIdentityDO::getKeyId, request.getKeyId())
                .and(Strings.isNotEmpty(searchValue), c -> c
                        .eq(HostIdentityDO::getId, searchValue).or()
                        .like(HostIdentityDO::getName, searchValue).or()
                        .like(HostIdentityDO::getUsername, searchValue)
                );
    }

}

package com.orion.ops.module.asset.service;

import com.orion.ops.module.asset.entity.request.asset.AssetAuthorizedDataQueryRequest;
import com.orion.ops.module.asset.entity.vo.AuthorizedHostWrapperVO;
import com.orion.ops.module.asset.entity.vo.HostIdentityVO;
import com.orion.ops.module.asset.entity.vo.HostKeyVO;
import com.orion.ops.module.asset.enums.HostConfigTypeEnum;
import com.orion.ops.module.infra.enums.DataPermissionTypeEnum;

import java.util.List;

/**
 * 资产模块 授权数据服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/11/30 18:33
 */
public interface AssetAuthorizedDataService {

    /**
     * 获取已被授权的数据 id
     *
     * @param request request
     * @param type    type
     * @return dataId
     */
    List<Long> getAuthorizedDataRelId(DataPermissionTypeEnum type, AssetAuthorizedDataQueryRequest request);

    /**
     * 查询用户已授权的主机
     *
     * @param userId userId
     * @param type   type
     * @return hostId
     */
    List<Long> getUserAuthorizedHostId(Long userId, HostConfigTypeEnum type);

    /**
     * 查询用户已授权的主机
     *
     * @param userId userId
     * @param type   type
     * @return group
     */
    AuthorizedHostWrapperVO getUserAuthorizedHost(Long userId, String type);

    /**
     * 获取用户已授权的主机id 不查询角色
     *
     * @param userId userId
     * @return hostId
     */
    List<Long> getUserAuthorizedHostId(Long userId);

    /**
     * 查询用户已授权的主机秘钥
     *
     * @param userId userId
     * @return key
     */
    List<HostKeyVO> getUserAuthorizedHostKey(Long userId);

    /**
     * 查询用户已授权的主机身份
     *
     * @param userId userId
     * @return identity
     */
    List<HostIdentityVO> getUserAuthorizedHostIdentity(Long userId);

}

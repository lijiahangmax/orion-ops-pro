package com.orion.ops.module.asset.service;

import com.orion.ops.module.asset.entity.request.asset.AssetDataGrantRequest;
import com.orion.ops.module.infra.enums.DataPermissionTypeEnum;

/**
 * 资产模块 数据授权服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/11/30 18:33
 */
public interface AssetDataGrantService {

    /**
     * 主机分组授权
     *
     * @param request request
     */
    void grantHostGroup(AssetDataGrantRequest request);

    /**
     * 主机秘钥授权
     *
     * @param request request
     */
    void grantHostKey(AssetDataGrantRequest request);

    /**
     * 主机身份授权
     *
     * @param request request
     */
    void grantHostIdentity(AssetDataGrantRequest request);

    /**
     * 数据授权
     *
     * @param type    type
     * @param request request
     */
    void grantData(DataPermissionTypeEnum type, AssetDataGrantRequest request);

}

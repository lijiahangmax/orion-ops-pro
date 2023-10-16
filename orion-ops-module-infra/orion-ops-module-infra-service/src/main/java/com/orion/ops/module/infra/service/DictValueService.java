package com.orion.ops.module.infra.service;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.module.infra.entity.request.dict.DictValueCreateRequest;
import com.orion.ops.module.infra.entity.request.dict.DictValueQueryRequest;
import com.orion.ops.module.infra.entity.request.dict.DictValueUpdateRequest;
import com.orion.ops.module.infra.entity.vo.DictValueVO;

import java.util.List;

/**
 * 字典配置值 服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-16 16:33
 */
public interface DictValueService {

    /**
     * 创建字典配置值
     *
     * @param request request
     * @return id
     */
    Long createDictValue(DictValueCreateRequest request);

    /**
     * 更新字典配置值
     *
     * @param request request
     * @return effect
     */
    Integer updateDictValueById(DictValueUpdateRequest request);

    /**
     * 查询字典配置值
     *
     * @param id id
     * @return row
     */
    DictValueVO getDictValueById(Long id);

    /**
     * 查询全部字典配置值
     *
     * @param request request
     * @return rows
     */
    List<DictValueVO> getDictValueList(DictValueQueryRequest request);

    /**
     * 通过缓存查询字典配置值
     *
     * @return rows
     */
    List<DictValueVO> getDictValueListByCache();

    /**
     * 分页查询字典配置值
     *
     * @param request request
     * @return rows
     */
    DataGrid<DictValueVO> getDictValuePage(DictValueQueryRequest request);

    /**
     * 删除字典配置值
     *
     * @param id id
     * @return effect
     */
    Integer deleteDictValueById(Long id);

    /**
     * 批量删除字典配置值
     *
     * @param idList idList
     * @return effect
     */
    Integer batchDeleteDictValueByIdList(List<Long> idList);

}

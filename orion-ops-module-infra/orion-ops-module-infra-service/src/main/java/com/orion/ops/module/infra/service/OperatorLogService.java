package com.orion.ops.module.infra.service;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.framework.biz.operator.log.core.model.OperatorLogModel;
import com.orion.ops.module.infra.entity.request.operator.OperatorLogQueryRequest;
import com.orion.ops.module.infra.entity.vo.LoginHistoryVO;
import com.orion.ops.module.infra.entity.vo.OperatorLogVO;

import java.util.List;

/**
 * 操作日志 服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-10 17:08
 */
public interface OperatorLogService {

    /**
     * 添加操作日志
     *
     * @param model model
     */
    void addOperatorLog(OperatorLogModel model);

    /**
     * 分页查询操作日志
     *
     * @param request request
     * @return rows
     */
    DataGrid<OperatorLogVO> getOperatorLogPage(OperatorLogQueryRequest request);

    /**
     * 查询用户登录日志
     *
     * @param username username
     * @return rows
     */
    List<LoginHistoryVO> getLoginHistory(String username);

}

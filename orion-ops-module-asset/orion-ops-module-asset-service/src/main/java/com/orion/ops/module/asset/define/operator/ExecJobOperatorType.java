 package com.orion.ops.module.asset.define.operator;

import com.orion.ops.framework.biz.operator.log.core.annotation.Module;
import com.orion.ops.framework.biz.operator.log.core.factory.InitializingOperatorTypes;
import com.orion.ops.framework.biz.operator.log.core.model.OperatorType;

import static com.orion.ops.framework.biz.operator.log.core.enums.OperatorRiskLevel.*;

/**
 * 计划执行任务 操作日志类型
 *
 * @author Jiahang Li
 * @version 1.0.3
 * @since 2024-3-28 12:03
 */
@Module("asset:exec-job")
public class ExecJobOperatorType extends InitializingOperatorTypes {

    public static final String CREATE = "exec-job:create";

    public static final String UPDATE = "exec-job:update";

    public static final String UPDATE_STATUS = "exec-job:update-status";

    public static final String EXEC = "exec-job:exec";

    public static final String DELETE = "exec-job:delete";

    @Override
    public OperatorType[] types() {
        return new OperatorType[]{
                new OperatorType(L, CREATE, "创建计划任务 <sb>${name}</sb>"),
                new OperatorType(M, EXEC, "手动执行计划任务 <sb>${name}</sb>"),
                new OperatorType(M, UPDATE, "更新计划任务 <sb>${name}</sb>"),
                new OperatorType(M, UPDATE_STATUS, "更新计划任务状态 <sb>${name}</sb> <sb>${statusName}</sb>"),
                new OperatorType(H, DELETE, "删除计划任务 <sb>${name}</sb>"),
        };
    }

}

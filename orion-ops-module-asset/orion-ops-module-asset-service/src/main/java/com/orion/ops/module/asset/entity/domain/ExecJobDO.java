package com.orion.ops.module.asset.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.orion.ops.framework.mybatis.core.domain.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 计划执行任务 实体对象
 *
 * @author Jiahang Li
 * @version 1.0.3
 * @since 2024-3-28 12:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "exec_job", autoResultMap = true)
@Schema(name = "ExecJobDO", description = "计划执行任务 实体对象")
public class ExecJobDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "任务名称")
    @TableField("name")
    private String name;

    @Schema(description = "cron 表达式")
    @TableField("expression")
    private String expression;

    @Schema(description = "超时时间")
    @TableField("timeout")
    private Integer timeout;

    @Schema(description = "执行命令")
    @TableField("command")
    private String command;

    @Schema(description = "命令参数")
    @TableField("parameter_schema")
    private String parameterSchema;

    @Schema(description = "启用状态 0禁用 1启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "最近执行id")
    @TableField("recent_log_id")
    private Long recentLogId;

}
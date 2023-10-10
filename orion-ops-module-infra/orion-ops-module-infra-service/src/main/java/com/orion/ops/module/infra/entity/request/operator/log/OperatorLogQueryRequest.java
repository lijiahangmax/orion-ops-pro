package com.orion.ops.module.infra.entity.request.operator.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orion.ops.framework.common.entity.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 操作日志 查询请求对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-10 17:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "OperatorLogQueryRequest", description = "操作日志 查询请求对象")
public class OperatorLogQueryRequest extends PageRequest {

    @Schema(description = "用户id")
    private Long userId;

    @Size(max = 32)
    @Schema(description = "模块")
    private String module;

    @Size(max = 64)
    @Schema(description = "操作类型")
    private String type;

    @Schema(description = "操作结果 0失败 1成功")
    private Integer result;

    @Schema(description = "开始时间-开区间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTimeStart;

    @Schema(description = "开始时间-闭区间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTimeEnd;

}

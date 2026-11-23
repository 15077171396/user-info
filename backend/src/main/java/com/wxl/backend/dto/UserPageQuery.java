package com.wxl.backend.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Schema(description = "用户列表查询条件")
public class UserPageQuery {

    @Schema(description = "页码，从 1 开始", example = "1")
    private long pageNum = 1;

    @Schema(description = "每页条数，最大 100", example = "10")
    private long pageSize = 10;

    @Schema(description = "昵称，模糊匹配")
    private String nickname;

    @Schema(description = "状态：1-正常 2-禁用 3-锁定")
    private Integer status;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "创建时间起，格式 yyyy-MM-dd HH:mm:ss", example = "2026-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "创建时间止，格式 yyyy-MM-dd HH:mm:ss", example = "2026-12-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

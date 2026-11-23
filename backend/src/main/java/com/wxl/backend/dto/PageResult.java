package com.wxl.backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分页结果")
public record PageResult<T>(
        @Schema(description = "当前页") long pageNum,
        @Schema(description = "每页条数") long pageSize,
        @Schema(description = "总条数") long total,
        @Schema(description = "总页数") long pages,
        @Schema(description = "数据列表") List<T> records) {
}

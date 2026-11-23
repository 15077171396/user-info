package com.wxl.backend.controller;

import java.time.Instant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "健康检查", description = "服务存活状态探测")
@RestController
@RequestMapping("/health")
public class HealthController {

    private final String applicationName;

    public HealthController(@Value("${spring.application.name:backend}") String applicationName) {
        this.applicationName = applicationName;
    }

    @Operation(summary = "健康检查", description = "返回服务运行状态与应用名")
    @GetMapping
    public HealthResponse health() {
        return new HealthResponse("UP", applicationName, Instant.now());
    }

    @Schema(description = "健康检查响应")
    public record HealthResponse(
            @Schema(description = "服务状态", example = "UP") String status,
            @Schema(description = "应用名", example = "backend") String applicationName,
            @Schema(description = "响应时间（UTC）") Instant timestamp) {
    }
}

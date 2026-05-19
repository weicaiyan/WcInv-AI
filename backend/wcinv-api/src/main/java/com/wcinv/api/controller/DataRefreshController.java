package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.DataRefreshResponse;
import com.wcinv.infrastructure.script.LixingerScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
public class DataRefreshController {

    private static final Logger log = LoggerFactory.getLogger(DataRefreshController.class);

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_PARTIAL_SUCCESS = "PARTIAL_SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String ERR_TOKEN_EXPIRED = "LIXINGER_TOKEN_EXPIRED";

    private final LixingerScriptService scriptService;

    public DataRefreshController(LixingerScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * 全局刷新所有已接入数据源。前端可后台等待结果，不阻塞页面浏览。
     */
    @PostMapping("/refresh")
    public ApiResponse<DataRefreshResponse> refreshAll() {
        LixingerScriptService.ScriptResult dryRun = scriptService.runFetchDryRun();
        if (!dryRun.isSuccess()) {
            log.warn("全局刷新 token 检测失败: {}", safeMessage(dryRun));
            if (LixingerScriptService.isTokenExpired(dryRun.getOutput())) {
                return ApiResponse.error(ERR_TOKEN_EXPIRED, "理杏仁 token 已过期，请重新登录");
            }
            return ApiResponse.error("DATA_REFRESH_FAILED", safeMessage(dryRun));
        }

        List<DataRefreshResponse.Item> items = new ArrayList<>();

        LixingerScriptService.ScriptResult indices = scriptService.runFetch();
        if (isTokenExpired(indices)) {
            return ApiResponse.error(ERR_TOKEN_EXPIRED, "理杏仁 token 已过期，请重新登录");
        }
        items.add(toItem("lixinger_indices", "宽基指数估值", indices));

        LixingerScriptService.ScriptResult bogle = scriptService.runFetchBogleIndices();
        if (isTokenExpired(bogle)) {
            return ApiResponse.error(ERR_TOKEN_EXPIRED, "理杏仁 token 已过期，请重新登录");
        }
        items.add(toItem("bogle_indices", "博格公式行业指数", bogle));

        LixingerScriptService.ScriptResult cheapStocks = scriptService.runFetchCheapStocks();
        if (isTokenExpired(cheapStocks)) {
            return ApiResponse.error(ERR_TOKEN_EXPIRED, "理杏仁 token 已过期，请重新登录");
        }
        items.add(toItem("cheap_stocks", "便宜组合股票池", cheapStocks));

        long failedCount = items.stream().filter(item -> STATUS_FAILED.equals(item.getStatus())).count();
        String status = resolveStatus(failedCount, items.size());
        String message = resolveMessage(status);

        return ApiResponse.success(new DataRefreshResponse(message, status, items));
    }

    private DataRefreshResponse.Item toItem(String source, String name, LixingerScriptService.ScriptResult result) {
        if (result.isSuccess()) {
            log.info("{} 刷新成功", name);
            return new DataRefreshResponse.Item(source, name, STATUS_SUCCESS, "刷新成功");
        }

        String message = safeMessage(result);
        log.warn("{} 刷新失败: {}", name, message);
        return new DataRefreshResponse.Item(source, name, STATUS_FAILED, message);
    }

    private String resolveStatus(long failedCount, int totalCount) {
        if (failedCount == 0) {
            return STATUS_SUCCESS;
        }
        if (failedCount == totalCount) {
            return STATUS_FAILED;
        }
        return STATUS_PARTIAL_SUCCESS;
    }

    private String resolveMessage(String status) {
        if (STATUS_SUCCESS.equals(status)) {
            return "全局数据刷新完成";
        }
        if (STATUS_FAILED.equals(status)) {
            return "全局数据刷新失败";
        }
        return "部分数据刷新失败，请查看结果";
    }

    private boolean isTokenExpired(LixingerScriptService.ScriptResult result) {
        return !result.isSuccess() && LixingerScriptService.isTokenExpired(result.getOutput());
    }

    private String safeMessage(LixingerScriptService.ScriptResult result) {
        String output = result.getStderr();
        if (output == null || output.trim().isEmpty()) {
            output = result.getStdout();
        }
        if (output == null || output.trim().isEmpty()) {
            return "脚本执行失败，exitCode=" + result.getExitCode();
        }
        String trimmed = output.trim().replaceAll("[\r\n]+", " ");
        return trimmed.length() > 300 ? trimmed.substring(0, 300) : trimmed;
    }
}

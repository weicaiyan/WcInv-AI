package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.LixingerLoginRequest;
import com.wcinv.application.dto.LixingerTokenResponse;
import com.wcinv.infrastructure.script.LixingerScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lixinger")
public class LixingerController {

    private static final Logger log = LoggerFactory.getLogger(LixingerController.class);

    private static final String ERR_TOKEN_EXPIRED = "LIXINGER_TOKEN_EXPIRED";

    private final LixingerScriptService scriptService;

    public LixingerController(LixingerScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * 触发数据刷新。先 dry-run 检测 token，有效则真正入库。
     */
    @PostMapping("/refresh")
    public ApiResponse<String> refresh() {
        LixingerScriptService.ScriptResult dryRun = scriptService.runFetchDryRun();

        if (!dryRun.isSuccess()) {
            log.warn("fetch_lixinger dry-run 失败: {}", dryRun.getStderr());

            if (LixingerScriptService.isTokenExpired(dryRun.getOutput())) {
                return ApiResponse.error(ERR_TOKEN_EXPIRED, "理杏仁 token 已过期，请重新登录");
            }
            return ApiResponse.error("LIXINGER_FETCH_FAILED", dryRun.getStderr());
        }

        // token 有效，执行真正入库
        LixingerScriptService.ScriptResult save = scriptService.runFetch();
        if (!save.isSuccess()) {
            log.warn("fetch_lixinger 入库失败: {}", save.getStderr());
            return ApiResponse.error("LIXINGER_SAVE_FAILED", save.getStderr());
        }

        log.info("理杏仁数据刷新成功，入库完成");
        return ApiResponse.success("数据刷新成功");
    }

    /**
     * 用账号密码登录理杏仁，获取新 token，然后自动拉数据入库。
     */
    @PostMapping("/login")
    public ApiResponse<LixingerTokenResponse> login(@RequestBody LixingerLoginRequest request) {
        LixingerScriptService.ScriptResult loginResult =
                scriptService.runLogin(request.getAccount(), request.getPassword());

        if (!loginResult.isSuccess()) {
            log.warn("理杏仁登录失败: {}", loginResult.getStderr());
            return ApiResponse.error("LIXINGER_LOGIN_FAILED", loginResult.getStderr());
        }

        String token = loginResult.getStdout().trim();
        if (token.isEmpty()) {
            return ApiResponse.error("LIXINGER_LOGIN_FAILED", "未能获取 token");
        }

        log.info("理杏仁登录成功，token 已更新，开始拉数据入库");

        // 登录成功后自动拉数据入库
        LixingerScriptService.ScriptResult fetch = scriptService.runFetch();
        if (!fetch.isSuccess()) {
            log.warn("登录后拉数据失败: {}", fetch.getStderr());
        }

        return ApiResponse.success(new LixingerTokenResponse(token));
    }
}

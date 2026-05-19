package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.DataRefreshResponse;
import com.wcinv.infrastructure.script.LixingerScriptService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataRefreshControllerTest {

    @Test
    void refreshAllReturnsSuccessWhenAllScriptsPass() {
        FakeScriptService scriptService = new FakeScriptService();
        DataRefreshController controller = new DataRefreshController(scriptService);

        ApiResponse<DataRefreshResponse> response = controller.refreshAll();

        assertNull(response.getError());
        assertNotNull(response.getData());
        assertEquals("SUCCESS", response.getData().getStatus());
        assertEquals(3, response.getData().getItems().size());
        assertEquals("lixinger_indices", response.getData().getItems().get(0).getSource());
        assertEquals("bogle_indices", response.getData().getItems().get(1).getSource());
        assertEquals("cheap_stocks", response.getData().getItems().get(2).getSource());
    }

    @Test
    void refreshAllReturnsPartialSuccessWhenOneScriptFails() {
        FakeScriptService scriptService = new FakeScriptService();
        scriptService.cheapStocks = new LixingerScriptService.ScriptResult(1, "", "cheap failed");
        DataRefreshController controller = new DataRefreshController(scriptService);

        ApiResponse<DataRefreshResponse> response = controller.refreshAll();

        assertNull(response.getError());
        assertNotNull(response.getData());
        assertEquals("PARTIAL_SUCCESS", response.getData().getStatus());
        assertEquals("FAILED", response.getData().getItems().get(2).getStatus());
        assertEquals("cheap failed", response.getData().getItems().get(2).getMessage());
    }

    @Test
    void refreshAllReturnsFailedWhenAllScriptsFail() {
        FakeScriptService scriptService = new FakeScriptService();
        scriptService.indices = new LixingerScriptService.ScriptResult(1, "", "indices failed");
        scriptService.bogle = new LixingerScriptService.ScriptResult(1, "", "bogle failed");
        scriptService.cheapStocks = new LixingerScriptService.ScriptResult(1, "", "cheap failed");
        DataRefreshController controller = new DataRefreshController(scriptService);

        ApiResponse<DataRefreshResponse> response = controller.refreshAll();

        assertNull(response.getError());
        assertNotNull(response.getData());
        assertEquals("FAILED", response.getData().getStatus());
        assertEquals("全局数据刷新失败", response.getData().getMessage());
    }

    @Test
    void refreshAllReturnsTokenErrorWhenLaterScriptDetectsExpiredToken() {
        FakeScriptService scriptService = new FakeScriptService();
        scriptService.bogle = new LixingerScriptService.ScriptResult(1, "", "token权限验证错误");
        DataRefreshController controller = new DataRefreshController(scriptService);

        ApiResponse<DataRefreshResponse> response = controller.refreshAll();

        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("LIXINGER_TOKEN_EXPIRED", response.getError().getCode());
    }

    @Test
    void refreshAllReturnsTokenErrorWhenDryRunDetectsExpiredToken() {
        FakeScriptService scriptService = new FakeScriptService();
        scriptService.dryRun = new LixingerScriptService.ScriptResult(1, "", "token权限验证错误");
        DataRefreshController controller = new DataRefreshController(scriptService);

        ApiResponse<DataRefreshResponse> response = controller.refreshAll();

        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("LIXINGER_TOKEN_EXPIRED", response.getError().getCode());
    }

    private static class FakeScriptService extends LixingerScriptService {
        private LixingerScriptService.ScriptResult dryRun = ok();
        private LixingerScriptService.ScriptResult indices = ok();
        private LixingerScriptService.ScriptResult bogle = ok();
        private LixingerScriptService.ScriptResult cheapStocks = ok();

        @Override
        public LixingerScriptService.ScriptResult runFetchDryRun() {
            return dryRun;
        }

        @Override
        public LixingerScriptService.ScriptResult runFetch() {
            return indices;
        }

        @Override
        public LixingerScriptService.ScriptResult runFetchBogleIndices() {
            return bogle;
        }

        @Override
        public LixingerScriptService.ScriptResult runFetchCheapStocks() {
            return cheapStocks;
        }

        private static LixingerScriptService.ScriptResult ok() {
            return new LixingerScriptService.ScriptResult(0, "ok", "");
        }
    }
}

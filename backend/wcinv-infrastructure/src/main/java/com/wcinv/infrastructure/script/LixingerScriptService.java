package com.wcinv.infrastructure.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 调用 Python 脚本（fetch_lixinger.py / lixinger_login.py）。
 */
@Service
public class LixingerScriptService {

    private static final Logger log = LoggerFactory.getLogger(LixingerScriptService.class);

    private static final String SCRIPTS_DIR;
    private static final String PYTHON = "python";

    static {
        // scripts 目录相对于项目根 backend/../../scripts/
        String userDir = System.getProperty("user.dir");
        // userDir 通常是 backend/ 目录，scripts 在项目根下
        File scriptsDir = new File(userDir, "../scripts");
        if (!scriptsDir.exists()) {
            // fallback: 从 wcinv-api 模块内查找
            scriptsDir = new File(userDir, "../../scripts");
        }
        SCRIPTS_DIR = scriptsDir.getAbsolutePath();
    }

    /**
     * 跑 fetch_lixinger.py --dry-run，只检测 token、不写库。
     */
    public ScriptResult runFetchDryRun() {
        return runPython("fetch_lixinger.py", "--dry-run");
    }

    /**
     * 跑 fetch_lixinger.py，真正拉数据入库。
     */
    public ScriptResult runFetch() {
        return runPython("fetch_lixinger.py");
    }

    /**
     * 跑 fetch_bogle_indices.py，刷新博格公式行业指数数据。
     */
    public ScriptResult runFetchBogleIndices() {
        return runPython("fetch_bogle_indices.py");
    }

    /**
     * 跑 fetch_cheap_stocks.py，刷新便宜组合股票池数据。
     */
    public ScriptResult runFetchCheapStocks() {
        return runPython("fetch_cheap_stocks.py");
    }

    /**
     * 调理杏仁登录脚本，获取新 token。
     */
    public ScriptResult runLogin(String account, String password) {
        return runPython("lixinger_login.py", account, password);
    }

    private ScriptResult runPython(String script, String... args) {
        try {
            File scriptFile = new File(SCRIPTS_DIR, script);
            if (!scriptFile.exists()) {
                return ScriptResult.error("脚本不存在: " + scriptFile.getAbsolutePath());
            }

            String[] cmd = new String[args.length + 2];
            cmd[0] = PYTHON;
            cmd[1] = scriptFile.getAbsolutePath();
            System.arraycopy(args, 0, cmd, 2, args.length);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(SCRIPTS_DIR));
            pb.redirectErrorStream(false);
            pb.environment().putIfAbsent("DB_HOST", "localhost");
            pb.environment().putIfAbsent("DB_PORT", "3306");
            pb.environment().putIfAbsent("DB_USER", "overduefree");
            pb.environment().putIfAbsent("DB_PASSWORD", "overduefree_pwd");
            pb.environment().putIfAbsent("DB_NAME", "wcinv");

            Process process = pb.start();

            // 读 stdout
            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());

            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return ScriptResult.error("脚本超时: " + script);
            }

            int exitCode = process.exitValue();
            return new ScriptResult(exitCode, stdout, stderr);

        } catch (Exception e) {
            log.error("执行脚本失败: {}", script, e);
            return ScriptResult.error("脚本异常: " + e.getMessage());
        }
    }

    private String readStream(java.io.InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }

    /**
     * 检测是否是 token 过期错误。
     */
    public static boolean isTokenExpired(String output) {
        return output != null && output.contains("token权限验证错误");
    }

    public static class ScriptResult {
        private final int exitCode;
        private final String stdout;
        private final String stderr;

        public ScriptResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        public static ScriptResult error(String message) {
            return new ScriptResult(-1, "", message);
        }

        public boolean isSuccess() {
            return exitCode == 0;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getStdout() {
            return stdout;
        }

        public String getStderr() {
            return stderr;
        }

        /** stdout + stderr 合并，用于错误检测。 */
        public String getOutput() {
            return stdout + "\n" + stderr;
        }
    }
}

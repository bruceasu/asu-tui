package me.asu.tui.js;

import me.asu.tui.api.CliContext;

import java.nio.file.Path;
import java.util.Map;

public class JsEngineUtil {

    /**
     * 判断当前环境是否支持 JavaScript 引擎（Nashorn 或 GraalVM）
     */
    public static boolean hasJavaScriptSupport() {
        // 1. 检查 javax.script (Nashorn)
        try {
            Class<?> managerClass = Class.forName("javax.script.ScriptEngineManager");
            Object manager = managerClass.getConstructor().newInstance();
            Object engine = managerClass
                    .getMethod("getEngineByName", String.class)
                    .invoke(manager, "javascript");
            if (engine != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            // javax.script 不存在
        } catch (Throwable ignore) {
            // 有类但初始化失败，继续检查 GraalVM
        }

        // 2. 检查 GraalVM Polyglot
        try {
            Class<?> contextClass = Class.forName("org.graalvm.polyglot.Context");
            Object context = contextClass.getMethod("create").invoke(null);
            Object engineObj = contextClass.getMethod("getEngine").invoke(context);
            Class<?> engineClass = engineObj.getClass();
            @SuppressWarnings("unchecked")
            Map<String, ?> langs = (Map<String, ?>) engineClass
                    .getMethod("getLanguages")
                    .invoke(engineObj);
            if (langs.containsKey("js")) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            // GraalVM 不存在
        } catch (Throwable ignore) {
            // 有类但初始化失败
        }

        return false;
    }

    public static Object execute(CliContext ctx, Path file, String[] args) {
        // 1. 尝试 Nashorn / javax.script
        try {
            Class<?> managerClass = Class.forName("javax.script.ScriptEngineManager");
            Object manager = managerClass.getConstructor().newInstance();
            Object engine = managerClass
                    .getMethod("getEngineByName", String.class)
                    .invoke(manager, "javascript");

            if (engine != null) {
                java.io.Reader scriptFile = java.nio.file.Files.newBufferedReader(file);

                // SimpleBindings simpleBindings = new SimpleBindings();
                Class<?> bindingsClass = Class.forName("javax.script.SimpleBindings");
                Object bindings = bindingsClass.getConstructor().newInstance();
                bindingsClass.getMethod("put", Object.class, Object.class)
                        .invoke(bindings, "arguments", args);

                return engine.getClass()
                        .getMethod("eval", java.io.Reader.class, Class.forName("javax.script.Bindings"))
                        .invoke(engine, scriptFile, bindings);
            }
        } catch (ClassNotFoundException e) {
            // javax.script 不存在（JDK15+ 无 Nashorn）
        } catch (Throwable e) {
            ctx.getCliConsole().printf("Cause error (Nashorn): %s%n", e.getMessage());
            return null;
        }

        // 2. 尝试 GraalVM Polyglot
        try {
            Class<?> contextClass = Class.forName("org.graalvm.polyglot.Context");
            // 创建 Context
            Object context = contextClass.getMethod("create", String[].class)
                    .invoke(null, (Object) new String[]{"js"});

            // 获取绑定
            Object bindings = contextClass.getMethod("getBindings", String.class)
                    .invoke(context, "js");
            Class<?> valueClass = Class.forName("org.graalvm.polyglot.Value");
            // bindings.putMember("arguments", args)
            valueClass.getMethod("putMember", String.class, Object.class)
                    .invoke(bindings, "arguments", args);

            // 读取脚本内容
            String script = new String(java.nio.file.Files.readAllBytes(file), java.nio.charset.StandardCharsets.UTF_8);

            // eval("js", script)
            Object result = contextClass.getMethod("eval", String.class, CharSequence.class)
                    .invoke(context, "js", script);

            return result != null ? result.toString() : null;
        } catch (ClassNotFoundException e) {
            // GraalVM 不存在
        } catch (Throwable e) {
            ctx.getCliConsole().printf("Cause error (GraalVM): %s%n", e.getMessage());
            return null;
        }

        // 3. 两者都没有
        ctx.getCliConsole().printf("No JavaScript engine found (Nashorn/GraalVM missing)%n");
        return null;
    }
}
/*


 */
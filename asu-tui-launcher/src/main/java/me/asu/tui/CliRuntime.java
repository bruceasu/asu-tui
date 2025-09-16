package me.asu.tui;

import me.asu.tui.api.CliConfigurator;
import me.asu.tui.api.CliContext;
import me.asu.tui.api.CliPlugin;
import me.asu.tui.builtin.DefaultCliConfigurator;
import me.asu.tui.builtin.DefaultCliContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import static me.asu.tui.api.CliContext.*;
import static me.asu.tui.I18nUtils.text;

/**
 * @author suk
 */
public class CliRuntime {

    private static final String LINUX = "Linux";
    private static final String windows = "Windows";
    private static CliContext ctx;
    private static CliConfigurator config;
    private static PluginManager pluginManager;

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static void init() {
        getContext();
        getConfigurator();
        initDir();
        initClassLoader();
        loadPlugins();
    }

    public static CliContext getContext() {
        return (ctx == null) ? ctx = DefaultCliContext.createInstance() : ctx;
    }

    public static CliConfigurator getConfigurator() {
        return (config == null) ? config = DefaultCliConfigurator.createNewInstance() : config;
    }

    private static void initDir() {
        String appHome = config.getAppHomeDir();

        String libDir = config.getProperty("lib.dir");
        File file1 = null;
        if (libDir != null) {
            file1 = new File(libDir);
        }
        File file2 = new File(appHome, CliConfigurator.VALUE_CONFIG_LIBDIR);
        File file3 = new File(CliConfigurator.VALUE_USER_DIR, CliConfigurator.VALUE_CONFIG_LIBDIR);
        if (file1 != null && file1.exists()) {
            ctx.putValue(KEY_LIB_DIR, file1);
        } else if (file2.exists()) {
            ctx.putValue(KEY_LIB_DIR, file2);
        } else {
            ctx.putValue(KEY_LIB_DIR, file3);
        }

        String pluginsDir = config.getProperty("plugins.dir");
        File file5 = null;
        if (pluginsDir != null) {
            file5 = new File(pluginsDir);
        }
        File file6 = new File(appHome, CliConfigurator.VALUE_CONFIG_PLUGINS_DIR);
        File file7 = new File(CliConfigurator.VALUE_USER_DIR,
                CliConfigurator.VALUE_CONFIG_PLUGINS_DIR);
        if (file5 != null && file5.exists()) {
            ctx.putValue(KEY_PLUGINS_DIR, file5);
        } else if (file6.exists()) {
            ctx.putValue(KEY_PLUGINS_DIR, file6);
        } else {
            ctx.putValue(KEY_PLUGINS_DIR, file7);
        }

        pluginManager = new PluginManager((File)ctx.getValue(KEY_PLUGINS_DIR));
    }

    // ------------------------------------------------------------
    // initClassLoader
    // ------------------------------------------------------------
    private static void initClassLoader() {
        // Create/get Context, if something goes wrong, exit.
        createLibClassloader();

        // load Plugins classloader
        createPluginClassloader();

    }

    private static void createLibClassloader() {
        File libDir = (File) ctx.getValue(KEY_LIB_DIR);
        if (libDir.exists()) {
            // load classes from lib directory
            ClassLoader libDirCl = null;
            try {
                ClassLoader parent = Thread.currentThread().getContextClassLoader();
                libDirCl = ClassManager.getClassLoaderFromFiles(new File[]{libDir},
                        CliConfigurator.JAR_FILE_PATTERN, parent);
                Thread.currentThread().setContextClassLoader(libDirCl);
            } catch (Exception ex) {
                System.out.printf(text("lib.dir.load.class.error"), libDir, ex.getMessage());
            } finally {
                if (libDirCl == null) {
                    System.out.printf(text("lib.dir.load.class.error"), libDir);
                }
            }
        } else {
            System.out.printf(text("lib.dir.not.found.error"), libDir.getAbsolutePath());

        }
    }

    private static void createPluginClassloader() {
        File pluginsDir = (File) ctx.getValue(KEY_PLUGINS_DIR);
        if (pluginsDir.exists()) {
            ClassLoader pluginsCl = null;
            try {
                pluginsCl = ClassManager.getClassLoaderFromFiles(new File[]{pluginsDir},
                        CliConfigurator.JAR_FILE_PATTERN,
                        Thread.currentThread().getContextClassLoader());
                ctx.putValue(CliContext.KEY_CLASS_LOADER, pluginsCl);
            } catch (Exception ex) {
                System.out.printf(text("plugins.dir.load.class.error"), pluginsDir,
                        ex.getMessage());
            } finally {
                if (pluginsCl == null) {
                    ctx.getCliConsole().printf(text("plugins.dir.load.class.error"), pluginsDir);
                }
            }
        } else {
            System.out.printf(text("plugins.dir.not.found.error"), pluginsDir.getAbsolutePath());
        }
    }

    // ------------------------------------------------------------

    private static void loadPlugins() {
        try {
            pluginManager.loadPlugins();
            ClassLoader pluginsCl = ctx.getClassLoader();
            // load plugins
            ServiceLoader<CliPlugin> loadedTypes = ServiceLoader.load(CliPlugin.class, pluginsCl);
            for (CliPlugin t : loadedTypes) {
                pluginManager.addPlugin(t);
            }

            if (pluginManager.isEmpty()) {
                System.out.printf(text("plugins.not.found.error"));
                System.exit(1);
            } else {
                if (ctx.isDebug()) {
                    pluginManager.getPlugins().forEach(p -> System.out.printf("Load plugin %s%n", p.getClass()));
                }
            }
            ctx.putValue(KEY_PLUGINS, pluginManager);

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return (osName.startsWith(windows));
    }

    public static boolean isLinux() {
        String osName = System.getProperty("os.name");
        return (LINUX.equals(osName));
    }


    /**
     * Filters the provided list using the specified type.
     *
     * @param <T> The type provided.
     */
    public static <T> List<T> filterPluginsByType(Collection<? extends CliPlugin> services, Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (CliPlugin p : services) {
            if (type.isAssignableFrom(p.getClass())) {
                result.add((T) p);
            }
        }
        return result;
    }


}
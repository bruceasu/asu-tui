package me.asu.tui.framework.core;

import static me.asu.tui.framework.api.Context.*;
import static me.asu.tui.framework.core.ResourceUtils.text;
import static me.asu.tui.framework.core.Shell.ClassManager.getClassLoaderFromFiles;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import me.asu.tui.framework.api.*;
import sun.security.action.GetPropertyAction;

/**
 * Utility class.
 *
 * @author suk
 */
public final class Shell {

    private static final Logger log = Logger.getLogger(Shell.class.getName());

    private Shell() {
    }

    public static class Runtime {

        private static final String        LINUX   = "Linux";
        private static final String        windows = "Windows";
        private static ShellContext ctx;
        private static Configurator config;

        static {
            init();
        }

        private static void init() {
            getContext();
            ctx.putValue(Context.KEY_INPUT_STREAM, System.in);
            ctx.putValue(Context.KEY_OUTPUT_STREAM, System.out);
            ctx.putValue(Context.KEY_ERROR_STREAM, System.err);

            getConfigurator();
            initDir();
            initClassLoader();
            loadPlugins();
            loadComponents();
            java.lang.Runtime.getRuntime().addShutdownHook(
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Runtime.unloadComponent();
                        }
                    }, "Unload-Component-Thread"));
        }

        public static ShellContext getContext() {
            return (ctx == null) ? ctx = ShellContext.createInstance() : ctx;
        }

        public static Configurator getConfigurator() {
            return (config == null) ? config = ShellConfigurator.createNewInstance() : config;
        }

        private static void initDir() {
            String appHome = config.getAppHomeDir();

            String libDir = config.getProperty("lib.dir");
            File file1 = null;
            if (libDir != null) {
                file1 = new File(libDir);
            }
            File file2 = new File(appHome, Configurator.VALUE_CONFIG_LIBDIR);
            File file3 = new File(Configurator.VALUE_USER_DIR, Configurator.VALUE_CONFIG_LIBDIR);
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
            File file6 = new File(appHome, Configurator.VALUE_CONFIG_PLUGINS_DIR);
            File file7 = new File(Configurator.VALUE_USER_DIR, Configurator.VALUE_CONFIG_PLUGINS_DIR);
            if (file5 != null && file5.exists()) {
                ctx.putValue(KEY_PLUGINS_DIR, file5);
            } else if (file6.exists()) {
                ctx.putValue(KEY_PLUGINS_DIR, file6);
            } else {
                ctx.putValue(KEY_PLUGINS_DIR, file7);
            }
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
                    libDirCl = getClassLoaderFromFiles(new File[]{libDir},
                            Configurator.JAR_FILE_PATTERN, parent);
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
                    pluginsCl = getClassLoaderFromFiles(new File[]{pluginsDir},
                            Configurator.JAR_FILE_PATTERN,
                            Thread.currentThread().getContextClassLoader());
                    ctx.putValue(Context.KEY_CLASS_LOADER, pluginsCl);
                } catch (Exception ex) {
                    System.out.printf(text("plugins.dir.load.class.error"), pluginsDir,
                            ex.getMessage());
                } finally {
                    if (pluginsCl == null) {
                        ctx.getConsole()
                           .printf(text("plugins.dir.load.class.error"), pluginsDir);
                    }
                }
            } else {
                System.out.printf(text("plugins.dir.not.found.error"),
                        pluginsDir.getAbsolutePath());
            }
        }

        // ------------------------------------------------------------

        private static void loadPlugins() {
            try {

                ClassLoader pluginsCl = ctx.getClassLoader();
                // load plugins
                List<Plugin> plugins = loadServicePlugins(Plugin.class, pluginsCl);
                if (plugins.isEmpty()) {
                    System.out.printf(text("plugins.not.found.error"));
                    System.exit(1);
                }

                ctx.putValue(Context.KEY_PLUGINS, plugins);

            } catch (RuntimeException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        /**
         * Load components.
         * Create default where possible if none found on classpath.
         */
        private static void loadComponents() {
            // activate console
            activateConsole();

            // activate controllers
            activateControllers();
        }

        private static void activateControllers() {
            List<InputController> controllers = ctx.getPluginsByType(InputController.class);
            if (controllers.size() > 0) {
                ctx.putValue(KEY_CONTROLLERS, controllers);
                boolean sucOne = false;
                for (InputController ctrl : controllers) {
                    try {
                        ctrl.plug(ctx);
                        sucOne = true;
                    } catch (Exception ex) {
                        System.out.printf(text("controller.plug.error"), ctrl.getClass(),
                                ex.getMessage());
                        ctrl.setEnabled(false);
                    }
                }
                if (!sucOne) {
                    System.exit(1);
                }
            } else {
                System.out.printf(text("controller.plug.not.found.error"));
                System.exit(1);
            }
        }

        private static void activateConsole() {
            List<IoConsole> ioConsoles = ctx.getPluginsByType(IoConsole.class);
            IoConsole console = null;
            if (ioConsoles != null && !ioConsoles.isEmpty()) {
                String property = config.getProperty(KEY_CONSOLE_COMPONENT);
                if (property != null) {
                    // try lookup
                    for (IoConsole ioConsole : ioConsoles) {
                        if (ioConsole.getClass().equals(property)) {
                            console = ioConsole;
                            break;
                        }
                    }
                }
                if (console == null) {
                    console = ioConsoles.get(0);
                }
                console.plug(ctx);
                ctx.putValue(KEY_CONSOLE_COMPONENT, console);
            } else {
                System.out.printf(text("console.plug.not.found.error"));
                System.exit(1);
            }
        }

        /**
         * This function loads/returns all Classes of type T from classpath.
         * It uses Java's ServiceProvider architecture to locate specified type.
         */
        public static <T> List<T> loadServicePlugins(Class<T> type, ClassLoader parent) {
            ServiceLoader<T> loadedTypes = ServiceLoader.load(type, parent);
            List<T> result = new ArrayList<T>();
            for (T t : loadedTypes) {
                result.add(t);
            }
            return result;
        }

        public static boolean isWindows() {
            PrivilegedAction pa = new GetPropertyAction("os.name");
            String osname = (String) AccessController.doPrivileged(pa);
            return (osname.startsWith(windows));
        }

        public static boolean isLinux() {
            PrivilegedAction pa = new GetPropertyAction("os.name");
            String osname = (String) AccessController.doPrivileged(pa);
            return (LINUX.equals(osname));
        }

        private static void unloadComponent() {
            // unplug controllers
            for (InputController ctrl : ctx.getControllers()) {
                try {
                    ctrl.unplug(ctx);
                } catch (Exception ex) {
                    System.out.printf(text("controller.unplug.error"), ctrl.getClass(),
                            ex.getMessage());
                }
            }
        }

        /**
         * Filters the provided list using the specified type.
         *
         * @param <T> The type provided.
         */
        public static <T> List<T> filterPluginsByType(List<? extends Plugin> services,
                                                      Class<T> type) {
            List<T> result = new ArrayList<T>();
            for (Plugin p : services) {
                if (type.isAssignableFrom(p.getClass())) {
                    result.add((T) p);
                }
            }
            return result;
        }


    }

    /**
     * Shell ClassManager utility Classes/Methods.
     */
    public static class ClassManager {

        /**
         * Creates classloader from directories.  The specified directory must
         * contain class files that will be searched by the ClassLoader
         *
         * @param dirs   directories to be used for class loading
         * @param parent parent directory
         * @return ClassLoader
         */
        public static ClassLoader getClassLoaderFromDirs(File[] dirs, ClassLoader parent)
                throws Exception {
            File[] fileDirs = correctPaths(dirs);
            List<URL> classpath = new ArrayList<URL>();
            for (int i = 0; i < fileDirs.length; i++) {
                File f = fileDirs[i];
                if (f.isDirectory()) {
                    classpath.add(fileDirs[i].toURI().toURL());
                }
            }
            return new URLClassLoader(classpath.toArray(new URL[classpath.size()]), parent);
        }

        /**
         * Adds the trailing slash in the path name.
         */
        private static File[] correctPaths(File[] paths) throws Exception {
            File[] correctedPaths = new File[paths.length];
            for (int i = 0; i < paths.length; i++) {
                String pathName = paths[i].getName();
                if (!pathName.endsWith(System.getProperty("file.separator"))) {
                    String fullPath = paths[i].getCanonicalPath();
                    correctedPaths[i] = new File(fullPath + System.getProperty("file.separator"));
                } else {
                    correctedPaths[i] = paths[i];
                }
            }
            return correctedPaths;
        }

        /**
         * Creates ClassLoader instance from files searched in provided directories.
         *
         * @param paths  directories to search for files to include in ClassLoader
         * @param parent parent class loader
         * @return ClassLoader
         */
        public static ClassLoader createClassLoaderFromFiles(File[] paths, ClassLoader parent)
                throws Exception {

            return getClassLoaderFromFiles(paths, Configurator.JAR_FILE_PATTERN, parent);
        }

        /**
         * Creates a classloader by searching for specified files in given
         * search directories.
         *
         * @param filePaths   directory or files to add to class loader.  If DIR search content of
         *                    the dir that maches expression.  if FILE and matches expression, add
         *                    to classloader.
         * @param filePattern regex pattern used to match filename.
         * @param parent      parent class loader
         */
        public static ClassLoader getClassLoaderFromFiles(final File[] filePaths,
                                                          final Pattern filePattern,
                                                          final ClassLoader parent)
                throws Exception {
            List<URL> classpath = new ArrayList<URL>();

            for (int i = 0; i < filePaths.length; i++) {
                File filePath = filePaths[i].getCanonicalFile();

                // if file is FILE and matches search, add to classloader
                if (filePath.isFile() && filePattern.matcher(filePath.getName()).matches()) {
                    classpath.add(filePath.toURI().toURL());
                    continue;
                }

                // if directory, search all matching files to add to classloader
                if (filePath.isDirectory()) {
                    File[] files = filePath.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return filePattern.matcher(file.getName()).matches();
                        }
                    });

                    for (int j = 0; j < files.length; j++) {
                        URL url = files[j].toURI().toURL();
                        classpath.add(url);
                    }
                }
            }

            URL[] urls = new URL[classpath.size()];
            ClassLoader cl = new URLClassLoader(classpath.toArray(urls), parent);
            return cl;
        }
    }
}

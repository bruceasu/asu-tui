package me.asu.tui.framework.core;

import static me.asu.tui.framework.api.CliContext.KEY_LIB_DIR;
import static me.asu.tui.framework.api.CliContext.KEY_PLUGINS;
import static me.asu.tui.framework.api.CliContext.KEY_PLUGINS_DIR;
import static me.asu.tui.framework.core.util.ResourceUtils.text;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Pattern;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliPlugin;

/**
 * @author suk
 */
public class CliRuntime
{

    private static final String          LINUX   = "Linux";
    private static final String          windows = "Windows";
    private static       CliContext      ctx;
    private static       CliConfigurator config;

    //static {
    //    init();
    //}

    public static void init()
    {
        getContext();
        getConfigurator();
        initDir();
        initClassLoader();
        loadPlugins();
    }

    public static CliContext getContext()
    {
        return (ctx == null) ? ctx = AsuCliContext.createInstance() : ctx;
    }

    public static CliConfigurator getConfigurator()
    {
        return (config == null) ? config = AsuCliConfigurator.createNewInstance() : config;
    }

    private static void initDir()
    {
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
    }

    // ------------------------------------------------------------
    // initClassLoader
    // ------------------------------------------------------------
    private static void initClassLoader()
    {
        // Create/get Context, if something goes wrong, exit.
        createLibClassloader();

        // load Plugins classloader
        createPluginClassloader();

    }

    private static void createLibClassloader()
    {
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

    private static void createPluginClassloader()
    {
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

    private static void loadPlugins()
    {
        try {

            ClassLoader pluginsCl = ctx.getClassLoader();
            // load plugins
            List<CliPlugin> plugins = loadServicePlugins(CliPlugin.class, pluginsCl);
            if (plugins.isEmpty()) {
                System.out.printf(text("plugins.not.found.error"));
                System.exit(1);
            } else {
                if (ctx.isDebug()) {
                    plugins.forEach(p -> System.out.printf("Load plugin %s%n", p.getClass()));
                }
            }

            ctx.putValue(KEY_PLUGINS, plugins);

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * This function loads/returns all Classes of type T from classpath. It uses Java's
     * ServiceProvider architecture to locate specified type.
     */
    public static <T> List<T> loadServicePlugins(Class<T> type, ClassLoader parent)
    {
        ServiceLoader<T> loadedTypes = ServiceLoader.load(type, parent);
        List<T> result = new ArrayList<T>();
        for (T t : loadedTypes) {
            result.add(t);
        }
        return result;
    }

    public static boolean isWindows()
    {
        String osName = System.getProperty("os.name");
        return (osName.startsWith(windows));
    }

    public static boolean isLinux()
    {
        String osName = System.getProperty("os.name");
        return (LINUX.equals(osName));
    }


    /**
     * Filters the provided list using the specified type.
     *
     * @param <T> The type provided.
     */
    public static <T> List<T> filterPluginsByType(List<? extends CliPlugin> services, Class<T> type)
    {
        List<T> result = new ArrayList<T>();
        for (CliPlugin p : services) {
            if (type.isAssignableFrom(p.getClass())) {
                result.add((T) p);
            }
        }
        return result;
    }


    /**
     * Shell ClassManager utility Classes/Methods.
     */
    public static class ClassManager
    {

        /**
         * Creates classloader from directories.  The specified directory must contain class files
         * that will be searched by the ClassLoader
         *
         * @param dirs   directories to be used for class loading
         * @param parent parent directory
         * @return ClassLoader
         */
        public static ClassLoader getClassLoaderFromDirs(File[] dirs, ClassLoader parent)
        throws Exception
        {
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
        private static File[] correctPaths(File[] paths) throws Exception
        {
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
        throws Exception
        {

            return getClassLoaderFromFiles(paths, CliConfigurator.JAR_FILE_PATTERN, parent);
        }

        /**
         * Creates a classloader by searching for specified files in given search directories.
         *
         * @param filePaths   directory or files to add to class loader.  If DIR search content of
         *                    the dir that maches expression.  if FILE and matches expression, add
         *                    to classloader.
         * @param filePattern regex pattern used to match filename.
         * @param parent      parent class loader
         */
        public static ClassLoader getClassLoaderFromFiles(final File[] filePaths,
                                                          final Pattern filePattern,
                                                          final ClassLoader parent) throws Exception
        {
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
                    File[] files = filePath.listFiles(new FileFilter()
                    {
                        @Override
                        public boolean accept(File file)
                        {
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
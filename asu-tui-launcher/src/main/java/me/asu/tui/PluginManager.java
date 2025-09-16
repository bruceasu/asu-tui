package me.asu.tui;

import me.asu.tui.api.CliContext;
import me.asu.tui.api.CliPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginManager {
    CliContext context;
    private final File pluginDir;
    private final Map<String, CliPlugin> plugins = new HashMap<>();

    public PluginManager(File pluginDir) {
        this.pluginDir = pluginDir;
    }

    public void loadPlugins() {
        File[] files = pluginDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            try {
                loadPlugin(f.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("加载插件失败: " + f.getName() + " -> " + e.getMessage());
            }
        }
    }

    public void loadPlugin(String pluginPath) throws Exception {
        File file = new File(pluginPath);
        Properties props = new Properties();
        // 判断是目录还是 jar
        if (file.isDirectory()) {
            File propFile = new File(file, "plugin.properties");
            try (FileInputStream in = new FileInputStream(propFile)) {
                props.load(in);
            }
            URL[] urls = collectClasspath(file);
            URLClassLoader cl = new URLClassLoader(urls, getClass().getClassLoader());
            CliPlugin plugin = createPlugin(props, cl);
            plugins.put(plugin.getName(), plugin);
        } else if (pluginPath.endsWith(".jar")) {
            try (JarFile jar = new JarFile(file)) {
                JarEntry entry = jar.getJarEntry("META-INF/plugin.properties");
                if (entry == null) throw new RuntimeException("缺少 plugin.properties");
                try (InputStream in = jar.getInputStream(entry)) {
                    props.load(in);
                }
            }
            URL[] urls = {file.toURI().toURL()};
            URLClassLoader cl = new URLClassLoader(urls, getClass().getClassLoader());
            CliPlugin plugin = createPlugin(props, cl);
            plugins.put(plugin.getName(), plugin);
        }
    }

    private URL[] collectClasspath(File pluginDir) throws MalformedURLException {
        List<URL> urls = new ArrayList<>();
        File libDir = new File(pluginDir, "lib");
        if (libDir.exists()) {
            for (File jar : Objects.requireNonNull(libDir.listFiles(f -> f.getName().endsWith(".jar")))) {
                urls.add(jar.toURI().toURL());
            }
        }
        File classesDir = new File(pluginDir, "classes");
        if (classesDir.exists()) {
            urls.add(classesDir.toURI().toURL());
        }
        File[] jars = pluginDir.listFiles(f -> f.getName().endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            for (File jar : jars) {
                urls.add(jar.toURI().toURL());
            }
        }
        return urls.toArray(new URL[0]);
    }

    private CliPlugin createPlugin(Properties props, ClassLoader cl) throws Exception {
        String mainClass = props.getProperty("main-class");
        Class<?> cls = cl.loadClass(mainClass);
        CliPlugin plugin = (CliPlugin) cls.getDeclaredConstructor().newInstance();
        plugin.plug(context);
        return plugin;
    }

    public void unloadPlugin(String pluginName) {
        CliPlugin plugin = plugins.remove(pluginName);
        if (plugin != null) {
            plugin.unplug(context);
        }
    }

    public Collection<CliPlugin> getPlugins() {
        return plugins.values();
    }

    public CliPlugin getPlugin(String commandName) {
        return plugins.values().stream()
                .filter(p -> commandName.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    public void addPlugin(CliPlugin plugin) {
        if (plugin == null) return;

        if (!plugins.containsKey(plugin.getName())) {
            plugins.put(plugin.getName(), plugin);
        }
    }

    public boolean isEmpty() {
        return plugins.isEmpty();
    }

    public <T> List<T> getPlugin(Class<T> commandType) {
        return (List<T>) plugins.values().stream()
                .filter(p->commandType.isAssignableFrom(p.getClass()))
                .collect(Collectors.toList());
    }
}
/*
plugin.properties 示例：
name=user
version=1.0.0
main-class=com.mycli.plugin.user.UserPlugin
description=User management commands
command=user

 */
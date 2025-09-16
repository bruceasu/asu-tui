package me.asu.tui.api;


import java.util.Collection;
import java.util.List;

public interface PluginManager {
    void loadPlugins();
    void loadPlugin(String pluginPath) throws Exception ;
    void unloadPlugin(String pluginName);
    Collection<CliPlugin> getPlugins();
    CliPlugin getPlugin(String commandName);
    <T> List<T> getPlugin(Class<T> commandType);
    void addPlugin(CliPlugin plugin);
    boolean isEmpty();

}
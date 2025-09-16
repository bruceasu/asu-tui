package me.asu.tui.builtin;


import me.asu.tui.CliRuntime;
import me.asu.tui.PluginManager;
import me.asu.tui.api.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCliContext implements CliContext {

    private Map<String, Object> values;

    /**
     * Private constructor
     */
    private DefaultCliContext() {
        values = new HashMap<String, Object>();
    }

    /**
     * Creates an instance of ShellContext.
     *
     * @return ShellContex
     */
    public static DefaultCliContext createInstance() {
        return new DefaultCliContext();
    }

    /**
     * Returns a copy of the internal Map that stores context values.
     *
     * @return copy of values.
     */
    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Adds a full map of values to the internal context Map.
     */
    @Override
    public void putValues(Map<String, ? extends Object> values) {
        this.values.putAll(values);
    }

    /**
     * Adds one value to the internal context map.
     */
    @Override
    public void putValue(String key, Object val) {
        values.put(key, val);
    }

    /**
     * Returns a value from the context map.
     */
    @Override
    public Object getValue(String key) {
        return values.get(key);
    }

    /**
     * Removes a single value from the context map.
     */
    @Override
    public void removeValue(String key) {
        values.remove(key);
    }

    /**
     * Returns the cli's context classloader.
     */
    @Override
    public ClassLoader getClassLoader() {
        return (ClassLoader) values.get(KEY_CLASS_LOADER);
    }

    /**
     * Returns an instance of the Configurator object.
     */
    @Override
    public CliConfigurator getConfigurator() {
        return CliRuntime.getConfigurator();
    }

    /**
     * Returns a list of the loaded Plugin instances.
     *
     * @return List <Plugin>
     */
    @Override
    public Collection<CliPlugin> getPlugins() {
        return ((PluginManager) values.get(KEY_PLUGINS)).getPlugins();
    }

    /**
     * Retrieves a list of Class instances using the provided Type.
     *
     * @param <T>  The generic type used to filter the plugins by type
     * @param type the Class to used as filter
     * @return List of components of type <T>
     */
    @Override
    public <T> List<T> getPluginsByType(Class<T> type) {
        return CliRuntime.filterPluginsByType(getPlugins(), type);
    }

    @Override
    public List<CliController> getControllers() {
        return (List<CliController>) values.get(KEY_CONTROLLERS);
    }

    @Override
    public List<CliCommand> getCommands() {
        return (List<CliCommand>) values.get(KEY_COMMANDS);
    }

    @Override
    public CliConsole getCliConsole() {
        return (CliConsole) getValue(KEY_CONSOLE_COMPONENT);
    }

    @Override
    public void setCliConsole(CliConsole plugin) {
        putValue(KEY_CONSOLE_COMPONENT, plugin);
    }

    @Override
    public Object getShell() {
        return getValue(KEY_SHELL_COMPONENT);
    }

    @Override
    public void setShell(Object shell) {
        putValue(KEY_SHELL_COMPONENT, shell);
    }
}
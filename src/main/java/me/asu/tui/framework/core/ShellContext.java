/*
 * #%L
 * clamshell-commons
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package me.asu.tui.framework.core;


import java.util.*;
import me.asu.tui.framework.api.*;

/**
 * Implementation of the Context used to provide shell information at runtime.
 *
 * @author vvivien
 */
public class ShellContext implements Context {

    private Map<String, Object> values;

    /**
     * Private constructor
     */
    private ShellContext() {
        values = new HashMap<String, Object>();
    }

    /**
     * Creates an instance of ShellContext.
     *
     * @return ShellContex
     */
    public static ShellContext createInstance() {
        return new ShellContext();
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
    public Configurator getConfigurator() {
        return Shell.Runtime.getConfigurator();
    }

    @Override
    public IoConsole getConsole() {
        return (IoConsole) getValue(KEY_CONSOLE_COMPONENT);
    }

    /**
     * Returns a list of the loaded Plugin instances.
     *
     * @return List <Plugin>
     */
    @Override
    public List<Plugin> getPlugins() {
        return (List<Plugin>) values.get(KEY_PLUGINS);
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
        return Shell.Runtime.filterPluginsByType(getPlugins(), type);
    }

    @Override
    public List<InputController> getControllers() {
        return (List<InputController>) values.get(KEY_CONTROLLERS);
    }

    @Override
    public List<Command> getCommands() {
        return (List<Command>) values.get(KEY_COMMANDS);
    }

    @Override
    public List<Command> getCommandsByNamespace(String namespace) {
        List<Command> result = new ArrayList();
        for (Command cmd : getCommands()) {
            Command.Descriptor desc = cmd.getDescriptor();
            if (desc != null && desc.getNamespace().equals(namespace)) {
                result.add(cmd);
            }
        }
        return result;
    }

    @Override
    public Map<String, Command> mapCommands(List<Command> commands) {
        Map<String, Command> cmdMap = new HashMap<String, Command>();
        for (Command cmd : commands) {
            Command.Descriptor desc = cmd.getDescriptor();
            if (desc != null && desc.getName() != null) {
                cmdMap.put(desc.getName(), cmd);
            }
        }
        return cmdMap;
    }
}

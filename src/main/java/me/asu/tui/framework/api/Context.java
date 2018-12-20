/*
 * #%L
 * clamshell-api
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
package me.asu.tui.framework.api;

import java.util.List;
import java.util.Map;

/**
 * The global context object that serves as the operating context for all components.
 *
 * @author vladimir.vivien
 */
public interface Context {

    /**
     * Key to point to shared cli classloader
     */
    static final String KEY_CLASS_LOADER = "key.classLoader";


    /**
     * Key for command-line command arguments
     */
    static final String KEY_COMMAND_LINE_ARGS = "key.commandParams";

    /**
     * Key for saving/retrieving the command map in the context.
     */
    static final String KEY_COMMAND_MAP = "key.commandMap";

    static final String KEY_LIB_DIR = "key.lib.dir";

    static final String KEY_PLUGINS_DIR = "key.plugins.dir";

    /**
     * Key to IOConsole instance
     */
    public static final String KEY_CONSOLE_COMPONENT = "key.consoleComponent";

    /**
     * Key to store loaded controllers
     */
    static final String KEY_CONTROLLERS = "key.controllers";

    /**
     * Key to save/retrieve Plugin instances.
     */
    static final String KEY_PLUGINS = "key.plugins";

    /**
     * Key to store/load Command instances.
     */
    static final String KEY_COMMANDS = "key.command";

    /**
     * Key to retrieve instance of InputStream;
     */
    static final String KEY_INPUT_STREAM = "key.InputStream";

    /**
     * Key to retrieve instance of OutputStream
     */
    static final String KEY_OUTPUT_STREAM = "key.OutputStream";

    /**
     * Output error stream
     */
    static final String KEY_ERROR_STREAM = "key.ErrorStream";

    /**
     * Returns the context's store copy of it's internal map.
     *
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getValues();

    /**
     * Overwrites the context's internal map store.
     *
     * @param values Map to use
     */
    void putValues(Map<String, ? extends Object> values);

    /**
     * Stores a value in the context's map store.impelements
     */
    void putValue(String key, Object val);

    /**
     * Retrieves a value from the context's map store.
     */
    Object getValue(String key);

    /**
     * Removes a value from the context's map store.
     */
    void removeValue(String key);

    /**
     * Returns the context classloader
     */
    ClassLoader getClassLoader();

    /**
     * Returns an instance of the Configurator object.
     *
     * @return Configurator
     */
    Configurator getConfigurator();


    /**
     * Returns an instance of IOConsole
     *
     * @return IOConsole
     */
    IoConsole getConsole();

    /**
     * Retrieves a collection of all plugins loaded at startup.  Note the
     * collection is returning instances and not class types.
     * Implementation should think about caching strategy for performance.
     *
     * @return List<Plugin>
     */
    List<Plugin> getPlugins();

    /**
     * Convenience method that retrieves a filtered list of instances based on
     * the typed specified.
     *
     * @param <T>  the type to use as filter.
     * @param type the actual type instance
     * @return list of objects that implements the filter type.
     */
    <T> List<T> getPluginsByType(Class<T> type);

    /***
     * Returns a list of loaded controllers.
     * @return List&lt;{@link InputController}&gt;
     */
    List<InputController> getControllers();

    /**
     * A convenience method that retrieves a list of Command plugins.
     *
     * @return list of Command instances.
     */
    List<Command> getCommands();

    /**
     * A convenience method to retrieve Command instances from the classpath
     * using the Command.Descriptor.getNamespace() value.
     *
     * @param namespace namespace filter used to retrieve command
     * @return List&lt;Command&gt;
     */
    List<Command> getCommandsByNamespace(String namespace);

    /**
     * Maps all of the command.  The default implementation should map
     * each command using Command.Descriptor.getName() as the key.
     *
     * @param commands a collection of command to map.
     * @return Map&lt;String, Command&gt; where Command.Descriptor.getName() is the key.
     */
    Map<String, Command> mapCommands(List<Command> commands);
}

package me.asu.tui.framework.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The global context object that serves as the operating context for all components.
 *
 * @author vladimir.vivien
 */
public interface CliContext
{

    /**
     * Key to point to shared cli classloader
     */
    String KEY_CLASS_LOADER = "key.classLoader";

    /**
     * Key to retrieve command-line input value from context store.
     */
    String KEY_COMMAND_LINE_INPUT = "key.commandlineInput";

    /**
     * Key for command-line command arguments
     */
    String KEY_COMMAND_LINE_ARGS = "key.commandParams";

    /**
     * Key for saving/retrieving the command map in the context.
     */
    String KEY_COMMAND_MAP = "key.commandMap";

    String KEY_LIB_DIR = "key.lib.dir";

    String KEY_PLUGINS_DIR = "key.plugins.dir";

    /**
     * Key to IOConsole instance
     */
    String KEY_CONSOLE_COMPONENT = "key.consoleComponent";

    /**
     * Key to store Prompt instance
     */
    String KEY_PROMPT_COMPONENT = "key.promptComponent";


    /**
     * Key to store loaded controllers
     */
    String KEY_CONTROLLERS = "key.controllers";

    /**
     * Key to save/retrieve Plugin instances.
     */
    String KEY_PLUGINS = "key.plugins";

    /**
     * Key to a Shell instance
     */
    String KEY_SHELL_COMPONENT = "key.shellComponent";

    /**
     * Key to store loaded splash screen
     */
    String KEY_SPLASH_SCREENS = "key.splashScreens";

    /**
     * Key to store/load Command instances.
     */
    String KEY_COMMANDS = "key.command";

    /**
     * Key to retrieve instance of InputStream;
     */
    String KEY_INPUT_STREAM = "key.InputStream";

    /**
     * Key to retrieve instance of OutputStream
     */
    String KEY_OUTPUT_STREAM = "key.OutputStream";

    /**
     * Output error stream
     */
    String KEY_ERROR_STREAM = "key.ErrorStream";
    
    String KEY_DEBUG_FLAG = "key.debugFlag";

    default void setDebug(boolean flag)
    {
        putValue(KEY_DEBUG_FLAG, flag);
    }

    default boolean isDebug()
    {
        Object value = getValue(KEY_DEBUG_FLAG);
        if (value == null) {
            return false;
        }
        return (boolean) value;
    }

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
    CliConfigurator getConfigurator();

    /**
     * Retrieves a collection of all plugins loaded at startup.  Note the
     * collection is returning instances and not class types.
     * Implementation should think about caching strategy for performance.
     *
     * @return List<Plugin>
     */
    List<CliPlugin> getPlugins();

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
     * @return List&lt;{@link CliController}&gt;
     */
    List<CliController> getControllers();

    /**
     * A convenience method that retrieves a list of Command plugins.
     *
     * @return list of Command instances.
     */
    List<CliCommand> getCommands();
    default void setCommands(List<CliCommand> commands)
    {
        putValue(CliContext.KEY_COMMANDS, commands);
    }
    /**
     * A convenience method to retrieve Command instances from the classpath
     * using the Command.Descriptor.getNamespace() value.
     *
     * @param namespace namespace filter used to retrieve command
     * @return List&lt;Command&gt;
     */
    default List<CliCommand> getCommandsByNamespace(String namespace) {
        List<CliCommand> result = new ArrayList<>();
        for (CliCommand cmd : getCommands()) {
            CliCommand.Descriptor desc = cmd.getDescriptor();
            if (desc != null && desc.getNamespace().equals(namespace)) {
                result.add(cmd);
            }
        }
        return result;
    }

    /**
     * Maps all of the command.  The default implementation should map
     * each command using Command.Descriptor.getName() as the key.
     *
     * @param commands a collection of command to map.
     * @return Map&lt;String, Command&gt; where Command.Descriptor.getName() is the key.
     */
    default Map<String, CliCommand> mapCommands(List<CliCommand> commands) {
        Map<String, CliCommand> cmdMap = new HashMap<String, CliCommand>();
        for (CliCommand cmd : commands) {
            CliCommand.Descriptor desc = cmd.getDescriptor();
            if (desc != null && desc.getName() != null) {
                cmdMap.put(desc.getName(), cmd);
            }
        }
        return cmdMap;
    }
    CliConsole getCliConsole();

    void setCliConsole(CliConsole plugin);

    Object getShell();

    void setShell(Object shell);
}

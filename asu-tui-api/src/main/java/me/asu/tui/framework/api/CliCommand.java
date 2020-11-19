package me.asu.tui.framework.api;

import java.util.*;

/**
 * The Command component is there to allow Controllers to delegate tasks.
 * Each command exposes a textual id.  This can be used to identify the action
 * (request) that will invoke the execute() method on that command.
 * @author vladimir.vivien
 */
public interface CliCommand extends CliPlugin
{
    /**
     * This method returns an instance of Command.Descritpor.
     * The descriptor is meta information about the command.  The descriptor
     * would also be a good place to describe and keep track of expected 
     * parameters for the command.
     * @return {@link CliCommand.Descriptor}
     */
    CliCommand.Descriptor getDescriptor();
    
    /**
     * This method will be called as the starting point to execute the logic
     * for the action mapped to this command.
     * @param ctx
     * @return Object
     */
    Object execute(CliContext ctx, String[] args);
    
    /**
     * An interface that can be used to describe the the functionality of the 
     * command implementation.  This is a very important concept in a tex-driven
     * environment such as a command-line user interface.
     * Implementation of this class should use JCommander (http://jcommander.org)
     * to implement command-line argument handlers.
     */
    public static interface Descriptor {
        /**
         * The purpose of the namespace is to provide an identifier to group
         * command without relying on class name or other convoluted approaches
         * to group command.
         * @return the command's namespace
         */
        String getNamespace();
        
        /**
         * Implementation of this method should return a simple string (with no spaces)
         * that identifies the action mapped to this command.
         * @return the name of the action mapped to this command.
         */
        String getName();
        
        /**
         * This method should return a descriptive text about the command 
         * it is attached to.
         * @return description
         */
        String getDescription();
        
        
        /**
         * Implementation of this method should return helpful hint on how
         * to use the associated command and further description of options that
         * are supported by the command.
         * @return usage
         */
        String getUsage();
        
        /**
         * Use this method is to provide a map of the command arguments.
         * @return Map&lt;String,String&gt; key is argument, value = description of arg.
         */
        Map<String,String> getArguments();

        default void printUsage(CliConsole c) {
            String usage = getUsage();
            c.printf("%s%n",usage);
            Map<String, String> arguments = getArguments();
            if (arguments != null && arguments.size() > 0) {
                c.printf("%nOptions:");
                c.printf("%n--------");
                List<String> strings = new ArrayList<>(arguments.keySet());
                strings.sort(Comparator.naturalOrder());
                for (String key : strings) {
                    c.printf("%n%1$-10s\t%2$s", key, arguments.get(key));
                }
            }
            c.printf("%n");
        }
    }
}

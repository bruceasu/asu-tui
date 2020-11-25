package me.asu.tui.framework.api;

import java.util.*;
import me.asu.tui.framework.util.CliArguments;
import me.asu.tui.framework.util.CliCmdLineParser;

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
    interface Descriptor
    {
        /**
         * The purpose of the namespace is to provide an identifier to group
         * command without relying on class name or other convoluted approaches
         * to group command.
         * @return the command's namespace
         */
        default String getNamespace(){return "";}
        
        /**
         * Implementation of this method should return a simple string (with no spaces)
         * that identifies the action mapped to this command.
         * @return the name of the action mapped to this command.
         */
        default String getName() {return "";}
        
        /**
         * This method should return a descriptive text about the command 
         * it is attached to.
         * @return description
         */
        default String getDescription()
        {
            return "";
        }

        CliCmdLineParser getCliCmdLineParser();

        default  CliArguments parse(String[] args)
        {
            CliCmdLineParser cliCmdLineParser = getCliCmdLineParser();
            if (cliCmdLineParser != null) {
                return cliCmdLineParser.parse(args);
            } else {
                return new CliArguments();
            }
        }

        default void printUsage(CliConsole c) {
            CliCmdLineParser cliCmdLineParser = getCliCmdLineParser();
            if (cliCmdLineParser != null) {
                String usage = cliCmdLineParser.usage(getName());
                c.printf("%s%n%s%n", getDescription(), usage);
                c.printf("%n");
            }
        }
    }
}

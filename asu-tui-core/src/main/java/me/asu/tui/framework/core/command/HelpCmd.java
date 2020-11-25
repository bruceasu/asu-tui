/*
 * Copyright 2012 ClamShell-Cli.
 *
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
 */
package me.asu.tui.framework.core.command;

import java.util.List;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConsole;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * This class implements the Help command.
 * <ul>
 * <li> Usage: help - displays description for all installed commands.
 * <li> Usage: help [command_name] displays command usage.
 * </ul>
 *
 * @author vladimir.vivien
 */
public class HelpCmd implements CliCommand
{

    private static final String            NAMESPACE  = "syscmd";
    private static final String            CMD_NAME   = "help";
    private static final HelpCmdDescriptor DESCRIPTOR = new HelpCmdDescriptor();

    @Override
    public CliCommand.Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    /**
     * Executes the Help command. The help command expects format 'help [command_name]'. If the
     * optional command_name parameter is present, this class will display info about about the
     * command.  If command_name is not present the Help command displays a list of help for all
     * installed command.
     */
    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        // if arg passed, display help for command matching arg.
        if (args != null && args.length > 0) {
            printCommandHelp(ctx, args[0].trim());
        } else {
            printAllHelp(ctx);
        }
        ctx.getCliConsole().printf("%n%n");
        return null;
    }

    private void printCommandHelp(CliContext ctx, String cmdName)
    {
        Map<String, CliCommand> commands = ctx.mapCommands(ctx.getCommands());
        if (commands != null) {
            CliCommand cmd = commands.get(cmdName.trim());
            if (cmd != null) {
                printCommandHelp(ctx, cmd);
            } else {
                ctx.getCliConsole().printf("%nUnable to find command [%s].", cmdName);
            }
        }
    }

    private void printAllHelp(CliContext ctx)
    {
        CliConsole c = ctx.getCliConsole();
        c.printf("%nAvailable Commands");
        c.printf("%n------------------");
        List<CliCommand> commands = ctx.getCommands();
        for (CliCommand cmd : commands) {
            c.printf("%n%1$10s %2$5s %3$s", cmd.getDescriptor().getName(), " ",
                    cmd.getDescriptor().getDescription());
        }
    }

    private void printCommandHelp(CliContext ctx, CliCommand cmd)
    {
        if (cmd != null && cmd.getDescriptor() != null) {
            CliConsole io = ctx.getCliConsole();
            cmd.getDescriptor().printUsage(io);
        } else {
            ctx.getCliConsole().printf("Unable to display help for command.%n");
        }
    }


    @Override
    public void plug(CliContext plug)
    {
        // no plugin action needed
    }

    @Override
    public void unplug(CliContext plug)
    {
        // nothing to do
    }

    private static class HelpCmdDescriptor implements CliCommand.Descriptor
    {

        CliCmdLineParser parser = new CliCmdLineParser();

        @Override
        public String getNamespace()
        {
            return NAMESPACE;
        }

        @Override
        public String getName()
        {
            return CMD_NAME;
        }

        @Override
        public String getDescription()
        {
            return "Type 'help' or 'help [command_name]' to displays help information for available commands.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }

}

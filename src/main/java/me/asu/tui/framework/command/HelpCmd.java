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
package me.asu.tui.framework.command;

import java.util.*;
import me.asu.tui.framework.api.*;

/**
 * This class implements the Help command.
 * <ul>
 * <li> Usage: help - displays description for all installed commands.
 * <li> Usage: help [command_name] displays command usage.
 * </ul>
 *
 * @author vladimir.vivien
 */
public class HelpCmd implements Command {

    private static final String            NAMESPACE  = "syscmd";
    private static final String            CMD_NAME   = "help";
    private              HelpCmdDescriptor descriptor = new HelpCmdDescriptor();

    @Override
    public Command.Descriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Executes the Help command.
     * The help command expects format 'help [command_name]'.
     * If the optional command_name parameter is present, this class will
     * display info about about the command.  If command_name is not present
     * the Help command displays a list of help for all installed command.
     */
    @Override
    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);

        // if arg passed, display help for command matching arg.
        if (args != null && args.length > 0) {
            printCommandHelp(ctx, args[0].trim());
        } else {
            printAllHelp(ctx);
        }
        ctx.getConsole().printf("%n%n");
        return null;
    }

    private void printCommandHelp(Context ctx, String cmdName) {
        Map<String, Command> commands = ctx.mapCommands(ctx.getCommands());
        if (commands != null) {
            Command cmd = commands.get(cmdName.trim());
            if (cmd != null) {
                printCommandHelp(ctx, cmd);
            } else {
                ctx.getConsole().printf("%nUnable to find command [%s].", cmdName);
            }
        }
    }

    private void printAllHelp(Context ctx) {
        IoConsole c = ctx.getConsole();
        c.printf("%nAvailable Commands");
        c.printf("%n------------------");
        List<Command> commands = ctx.getCommands();
        for (Command cmd : commands) {
            c.printf("%n%1$10s %2$5s %3$s", cmd.getDescriptor().getName(), " ",
                    cmd.getDescriptor().getDescription());
        }
    }

    private void printCommandHelp(Context ctx, Command cmd) {
        if (cmd != null && cmd.getDescriptor() != null) {
            IoConsole io = ctx.getConsole();
            io.printf("%n%s - %s%n", cmd.getDescriptor().getName(),
                    cmd.getDescriptor().getDescription());
            io.printf("Usage: %s", cmd.getDescriptor().getUsage());
            printCommandParamsDetail(ctx, cmd);
        } else {
            ctx.getConsole().printf("Unable to display help for command.%n");
        }
    }

    private void printCommandParamsDetail(Context ctx, Command cmd) {
        Command.Descriptor desc = cmd.getDescriptor();
        if (desc == null || desc.getArguments() == null || desc.getArguments().isEmpty()) {
            return;
        }
        IoConsole c = ctx.getConsole();
        c.printf("%nOptions:");
        c.printf("%n--------");
        for (Map.Entry<String, String> entry : desc.getArguments().entrySet()) {
            c.printf("%n%1$-25s\t%2$s", entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void plug(Context plug) {
        // no plugin action needed
    }

    @Override
    public void unplug(Context plug) {
        // nothing to do
    }

    private class HelpCmdDescriptor implements Command.Descriptor {

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getName() {
            return CMD_NAME;
        }

        @Override
        public String getDescription() {
            return "Displays help information for available commands.";
        }

        @Override
        public String getUsage() {
            return "Type 'help' or 'help [command_name]' ";
        }

        @Override
        public Map<String, String> getArguments() {
            return Collections.emptyMap();

        }
    }

}

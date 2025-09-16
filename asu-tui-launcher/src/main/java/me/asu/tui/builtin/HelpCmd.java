package me.asu.tui.builtin;

import me.asu.tui.AlignPrint;
import me.asu.tui.CliCmdLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliConsole;
import me.asu.tui.api.CliContext;

import java.util.*;

/**
 * This class implements the Help command.
 * <ul>
 * <li> Usage: help - displays description for all installed commands.
 * <li> Usage: help [command_name] displays command usage.
 * </ul>
 */
public class HelpCmd extends CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "help";

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
        return "Type 'help' or 'help [command_name]' to displays help information for available commands.";
    }

    /**
     * Executes the Help command. The help command expects format 'help [command_name]'. If the
     * optional command_name parameter is present, this class will display info about about the
     * command.  If command_name is not present the Help command displays a list of help for all
     * installed command.
     */
    @Override
    public Object execute(CliContext ctx, String[] args) {
        // if arg passed, display help for command matching arg.
        if (args != null && args.length > 0) {
            printCommandHelp(ctx, args[0].trim());
        } else {
            printAllHelp(ctx);
        }
        ctx.getCliConsole().printf("%n%n");
        return null;
    }

    private void printCommandHelp(CliContext ctx, String cmdName) {
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

    private void printAllHelp(CliContext ctx) {
        CliConsole c = ctx.getCliConsole();
        c.printf("%nAvailable Commands");
        c.printf("%n------------------%n");
        List<CliCommand> commands = ctx.getCommands();
        Map<String, String> map = new TreeMap<>();
        for (CliCommand cmd : commands) {
            map.put(cmd.getName(), cmd.getDescription());
        }
        String helpText = AlignPrint.formatAligned(map, 80);
        System.out.println(helpText);

    }

    private void printCommandHelp(CliContext ctx, CliCommand cmd) {
        if (cmd != null) {
            CliConsole io = ctx.getCliConsole();
            printUsage(io);
        } else {
            ctx.getCliConsole().printf("Unable to display help for command.%n");
        }
    }
}
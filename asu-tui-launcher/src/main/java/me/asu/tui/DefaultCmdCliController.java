package me.asu.tui;

import me.asu.tui.api.*;

import java.util.*;

import static me.asu.tui.I18nUtils.text;


/**
 * @author suk
 */
public class DefaultCmdCliController implements CmdCliController {

    private static Class<CliCommand> COMMAND_TYPE = CliCommand.class;
    private static String DEFAULT_NAMESPACE = "syscmd";
    CliContext ctx;
    Jobs jobs = new Jobs();
    private Map<String, CliCommand> commands;
    private Boolean enabled = Boolean.TRUE;

    /**
     * Handles incoming command-line input.  CmdController first splits the input into token[N]
     * tokens.  It uses token[0] as the action name mapped to the Command.
     *
     * @param ctx the shell context.
     */
    @Override
    public boolean handle(CliContext ctx) {
        CommandLineParser parser = new CommandLineParser();
        String[] cmdLine = (String[]) ctx.getValue(CliContext.KEY_COMMAND_LINE_ARGS);
        CliConsole console = ctx.getCliConsole();
        if (cmdLine == null) {
            // try
            String line = (String) ctx.getValue(CliContext.KEY_COMMAND_LINE_INPUT);
            if (line != null) {
                parser.parse(line);
            } else {
                console.printf(text("command.not.found.error"), "");
                return false;
            }
        } else {
            parser.setBg(false);
            parser.setTokens(cmdLine);
            parser.setCmd(cmdLine[0]);
            if (cmdLine.length > 1) {
                String[] args = Arrays.copyOfRange(cmdLine, 1, cmdLine.length);
                parser.setArgs(args);
            }
        }
        boolean handled = false;

        // handle command line entry.  NOTE: value can be null
        if (commands != null && !commands.isEmpty()) {
            CliCommand cmd = commands.get(parser.getCmd());
            if (cmd != null) {
                if (parser.isBg()) {
                    jobs.add(new BgRunner(cmd, parser.getArgs()));
                } else {
                    try {
                        Object execute = cmd.execute(ctx, parser.getArgs());
                        if (execute != null) {
                            console.printf("%s%n", execute);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(console.writer());
                        console
                                .printf(text("command.exec.error"), Arrays.asList(cmdLine),
                                        ex.getMessage());
                    }
                }


            } else {
                console.printf(text("command.not.found.error"), cmdLine[0]);
            }
            handled = true;
        }

        return handled;
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean flag) {
        enabled = flag;
    }

    public void addCommand(String name, CliCommand com, boolean replace) {
        Objects.requireNonNull(name, "Name of command should be empty.");
        Objects.requireNonNull(com, "Command should not be empty.");
        try {
            List<CliCommand> commands = ctx.getCommands();
            if (commands == null) {
                commands = new ArrayList<>();
                ctx.setCommands(commands);
            }
            if (replace) {
                this.commands.put(name, com);
                com.plug(ctx);
                commands.add(com);
                //if(ctx.isDebug()) {
                ctx.getCliConsole().printf("load Command: %s -> %s%n", name, com.getClass());
                //}
            } else {
                CliCommand command = this.commands.putIfAbsent(name, com);
                if (command == null) {
                    com.plug(ctx);
                    commands.add(com);
                    if (ctx.isDebug()) {
                        ctx.getCliConsole().printf("load Command: %s -> %s%n", name, com.getClass());
                    }
                }
            }
        } catch (Exception ex) {
            ctx.getCliConsole().printf(text("command.plug.error"), com.getClass(), ex.getMessage());
        }
    }


    /**
     * Entry point for the plugin.  It builds class path from 'command' directory. Then loads each
     * Command found.
     */
    @Override
    public void plug(CliContext plug) {
        this.ctx = plug;
        PluginManager pluginManager = CliRuntime.getPluginManager();

        List<CliCommand> commands = pluginManager.getPlugin(COMMAND_TYPE);

        CliConsole console = plug.getCliConsole();
        if (commands == null || commands.isEmpty()) {
            console.printf(text("controller.no.command.definition.error"), this.getClass().getName());
            System.exit(1);
        } else {
            List<CliCommand> list = plug.getCommands();
            if (list != null) {
                commands.addAll(list);
            }
            plug.setCommands(commands);
            this.commands = plug.mapCommands(commands);
            for (CliCommand cmd : commands.toArray(new CliCommand[0])) {
                if (ctx.isDebug()) {
                    console.printf("Load Command: %s%n", cmd.getClass());
                }
                try {
                    cmd.plug(plug);
                } catch (Exception ex) {
                    console.printf(text("command.plug.error"), cmd.getClass(), ex.getMessage());
                }
            }
        }

        if (commands == null || commands.isEmpty()) {
            console.printf(text("controller.no.command.definition.error"), this.getClass().getName());
            System.exit(1);
        }
    }

    @Override
    public void unplug(CliContext plug) {
        List<CliCommand> cmds = plug.getCommands();
        for (CliCommand cmd : cmds) {
            try {
                cmd.unplug(plug);
            } catch (Exception ex) {
                plug.getCliConsole()
                        .printf(text("command.unplug.error"), cmd.getClass(), ex.getMessage());
            }
        }

    }


}
package me.asu.tui.builtin;

import me.asu.log.Log;
import me.asu.tui.*;
import me.asu.tui.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static me.asu.tui.I18nUtils.text;

/**
 * @author suk
 */
public class DefaultCmdCliController implements CmdCliController {

    private static Class<CliCommand> COMMAND_TYPE = CliCommand.class;
    private static String DEFAULT_NAMESPACE = "syscmd";
    private CliContext ctx;
    private Jobs jobs = new Jobs();
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
//                console.printf(text("command.not.found.error"), cmdLine[0]);
                // try wrap to system command.
                execute((ctx == null ? this.ctx : ctx), cmdLine);
            }
            handled = true;
        } else {
            // try wrap to system command.
            execute((ctx == null ? this.ctx : ctx), cmdLine);
            handled = true;
        }

        return handled;
    }

    Object execute(CliContext ctx, String[] args) {
        ProcessBuilder builder = new ProcessBuilder();
        addPath(builder, ctx);
        //  add shell
        boolean isWindows = ShellWrapCmd.isWindows();
        List<String> commands = new ArrayList<String>();
        if (isWindows) {
            commands.add("cmd");
            commands.add("/c");
        } else {
            commands.add("bash");
            commands.add("-c");
        }

        // add parameters
        if (args != null && args.length > 0) {
            commands.addAll(Arrays.asList(args));
        }
        builder.directory(new File(System.getProperty("user.dir")));
        builder.command(commands);
        try {
            Process p = builder.start();
            new ShellWrapCmd.PipeThread(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()),
                    ctx.getCliConsole().writer()).start();
            new ShellWrapCmd.PipeThread(new InputStreamReader(p.getErrorStream(), Charset.defaultCharset()),
                    ctx.getCliConsole().writer()).start();
            int i = p.waitFor();
            if (i != 0) {
                ctx.getCliConsole().printf("process return: %d%n", i);
            }
            return i;
        } catch (IOException e) {
            ctx.getCliConsole().printf("%s%n", e.getMessage());
        } catch (InterruptedException e) {
            ctx.getCliConsole().printf("%s%n", e.getMessage());
        }
        return 1;
    }

    /**
     * 把自身的执行目录添加到环境变量PATH中
     *
     * @param builder {@link ProcessBuilder}
     */
    void addPath(ProcessBuilder builder, CliContext ctx) {
        String appHomeDir = ctx.getConfigurator().getAppHomeDir();
        String userDir = CliConfigurator.VALUE_USER_DIR;
        String userHome = CliConfigurator.VALUE_USER_HOME;
        String pathSeparator = CliConfigurator.VALUE_PATH_SEPARATOR;
        Map<String, String> environment = builder.environment();
        String path = System.getenv("PATH");
        if (appHomeDir != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + appHomeDir;
            } else {
                path = path + pathSeparator + appHomeDir;
            }
        }
        if (userDir != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + userDir;
            } else {
                path = path + pathSeparator + userDir;
            }
        }
        if (userHome != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + userHome;
            } else {
                path = path + pathSeparator + userHome;
            }
        }
        environment.put("PATH", path);
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
                if (ctx.isDebug()) Log.info("load Command: %s -> %s", name, com.getClass());
            } else {
                CliCommand command = this.commands.putIfAbsent(name, com);
                if (command == null) {
                    com.plug(ctx);
                    commands.add(com);
                    if (ctx.isDebug()) Log.info("load Command: %s -> %s", name, com.getClass());
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
        PluginManagerImp pluginManager = CliRuntime.getPluginManager();
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
                Log.debug(" %s", cmd.getClass());
                try {
                    cmd.plug(plug);
                } catch (Exception ex) {
                    Log.error(text("command.plug.error"), cmd.getClass(), ex.getMessage());
                }
            }
        }

        if (commands == null || commands.isEmpty()) {
            Log.warn(text("controller.no.command.definition.error"), this.getClass().getName());
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
                Log.error(text("command.unplug.error"), cmd.getClass(), ex.getMessage());
            }
        }

    }


}
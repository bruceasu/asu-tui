package me.asu.tui.framework.core;

import static me.asu.tui.framework.core.util.ResourceUtils.text;

import java.util.*;
import lombok.Data;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConsole;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliController;


/**
 * @author suk
 */
public class CmdCliController implements CliController
{

    private static Class<CliCommand>       COMMAND_TYPE      = CliCommand.class;
    private static String                  DEFAULT_NAMESPACE = "syscmd";
    private        Map<String, CliCommand> commands;
    private        Boolean                 enabled           = Boolean.TRUE;
    CliContext ctx;
    Jobs jobs = new Jobs();

    /**
     * Handles incoming command-line input.  CmdController first splits the input into token[N]
     * tokens.  It uses token[0] as the action name mapped to the Command.
     *
     * @param ctx the shell context.
     */
    @Override
    public boolean handle(CliContext ctx)
    {
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
               String[] args =  Arrays.copyOfRange(cmdLine, 1, cmdLine.length);
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
                        if (execute!=null) {
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
    public Boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean flag)
    {
        enabled = flag;
    }

    public void addCommand(String name, CliCommand com, boolean replace)
    {
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
                    if(ctx.isDebug()) {
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
    public void plug(CliContext plug)
    {
        this.ctx = plug;
        List<CliCommand> commands = CliRuntime.loadServicePlugins(
                COMMAND_TYPE, plug.getClassLoader());

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
                if(ctx.isDebug()) {
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
    public void unplug(CliContext plug)
    {
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

    @Data
    class CommandLineParser
    {

        boolean  bg = false;
        String   cmd;
        String[] args;
        String[] tokens;


        public void parse(String cl)
        {

            if (cl == null || cl.trim().length() == 0) {
                return;
            }

            cl = cl.trim();
            if (cl.charAt(cl.length() - 1) == '&') {
                bg = true;
                cl = cl.substring(0, cl.length() - 1).trim();
            }
            //String[] tokens = cl.split("\\s+");
            tokens = token(cl);
            if (tokens.length == 1) {
                this.cmd = tokens[0];
            } else {
                this.cmd = tokens[0];
                this.args = new String[tokens.length - 1];
                System.arraycopy(tokens, 1, this.args, 0, this.args.length);
            }
        }

        /**
         * 支持 double quote 做为单字分界 支持 转义符号 \ 转义： \\ \" \t \n \[SPC]
         *
         * @param line 命令行
         * @return 单词数组
         */
        String[] token(String line)
        {
            if (line == null || line.trim().isEmpty()) {
                return null;
            }

            List<String> tokens = new LinkedList<>();
            char[] chars = line.toCharArray();
            StringBuilder buffer = new StringBuilder();
            boolean quoteStart = false;
            boolean escapeStart = false;
            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (ch == '\"') {
                    if (escapeStart) {
                        buffer.append('\"');
                        escapeStart = false;
                    } else if (quoteStart) {
                        // finished
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                        quoteStart = false;
                    } else {
                        quoteStart = true;
                        if (buffer.length() > 0) {
                            tokens.add(buffer.toString());
                            buffer.setLength(0);
                        }
                    }
                } else if (ch == '\\') {
                    if (escapeStart) {
                        buffer.append('\\');
                        escapeStart = false;
                    } else {
                        escapeStart = true;
                    }
                } else if (ch == 't') {
                    if (escapeStart) {
                        buffer.append('\t');
                        escapeStart = false;
                    } else {
                        buffer.append(ch);
                    }
                } else if (ch == 'n') {
                    if (escapeStart) {
                        buffer.append('\n');
                        escapeStart = false;
                    } else {
                        buffer.append(ch);
                    }
                } else if (ch == ' ') {
                    if (quoteStart) {
                        buffer.append(' ');
                    } else if (escapeStart) {
                        buffer.append(' ');
                        escapeStart = false;
                    } else {
                        // finish
                        if (buffer.length() > 0) {
                            tokens.add(buffer.toString());
                            buffer.setLength(0);
                        } else {
                            // skip
                        }
                    }
                } else if (ch == '\t') {
                    if (quoteStart) {
                        buffer.append('\t');
                    } else if (escapeStart) {
                        buffer.append('\t');
                        escapeStart = false;
                    } else {
                        // finish
                        if (buffer.length() > 0) {
                            tokens.add(buffer.toString());
                            buffer.setLength(0);
                        } else {
                            // skip
                        }
                    }
                } else {
                    buffer.append(ch);
                }
            }
            if (buffer.length() > 0) {
                tokens.add(buffer.toString());
                buffer.setLength(0);
            }

            return tokens.toArray(new String[tokens.size()]);
        }
    }


}

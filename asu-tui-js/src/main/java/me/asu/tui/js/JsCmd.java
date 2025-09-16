package me.asu.tui.js;

import me.asu.tui.CliCmdLineParser;
import me.asu.tui.CommandLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliConsole;
import me.asu.tui.api.CliContext;
import me.asu.tui.api.CmdInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suk
 * @since 2018/8/17
 */
@CmdInfo(ns = "js", value = "js", desc = "Execute Javascript", usage = "js <script-name> [args,...]")
public class JsCmd implements CliCommand {
    CliCmdLineParser parser = new CliCmdLineParser();
    private CliContext ctx;
    private Path dir;
    private Map<String, JsWrapperCmd> subCommands = new HashMap<>();

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public Map<String, JsWrapperCmd> subCommands() {
        return subCommands;
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        CliConsole console = ctx.getCliConsole();
        CommandLineParser parser = new CommandLineParser();
        String line = (String) ctx.getValue(CliContext.KEY_COMMAND_LINE_INPUT);
        if (line != null) {
            parser.parse(line);
        } else {
            return false;
        }
        String cmd = parser.getCmd();
        JsWrapperCmd jsWrapperCmd = subCommands().get(cmd);
        if (jsWrapperCmd == null) {
            console.println("No such command: " + cmd);
        }
        return jsWrapperCmd.execute(ctx, parser.getArgs());
    }

    @Override
    public void plug(CliContext plug) {
        ctx = plug;
        try {
            String appHomeDir = ctx.getConfigurator().getAppHomeDir();
            Path dir = Paths.get(appHomeDir, "js");
            if (Files.isDirectory(dir)) {
                Files.list(dir).forEach(p -> {
                    Path fileName = p.getFileName();
                    String s = fileName.toString();
                    String baseName = s;
                    int i = s.lastIndexOf(".");
                    if (i != -1) {
                        baseName = s.substring(0, i);
                    }

                    JsWrapperCmd cmd = new JsWrapperCmd(p.toAbsolutePath());
                    cmd.setDescription(baseName);
                    cmd.setUsage(baseName);
                    cmd.plug(plug);
                    subCommands.put(baseName, cmd);
                });

            } else {
                plug.getCliConsole().printf("%s is not found. Do not load js.%n", dir);
            }
        } catch (IOException e) {
            plug.getCliConsole().printf("%s%n", e.getMessage());
        }
    }


}
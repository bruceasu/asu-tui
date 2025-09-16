package me.asu.tui.sh;

import me.asu.log.Log;
import me.asu.tui.ShellWrapCmd;
import me.asu.tui.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author suk
 * @since 2018/8/17
 */
public class ScriptWrapperPlugin implements CliPlugin {

    private static final String CMD_NAME = "script-wrapper";

    @Override
    public String getName() {
        return CMD_NAME;
    }

    public Object execute(CliContext ctx, String[] args) {
        if (args==null||args.length == 0 ) {
            return null;
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        ctx.putValue(CliContext.KEY_COMMAND_LINE_ARGS, newArgs);
        List<CliController> controllers = ctx.getControllers();
        if (controllers == null || controllers.isEmpty()) {
            return null;
        }
        CmdCliController container = null;
        for (CliController c : controllers) {
            if (c instanceof CmdCliController) {
                container = (CmdCliController) c;
                break;
            }
        }
        if (container == null) {
            return null;
        }
        container.handle(ctx);
        return null;
    }

    @Override
    public void plug(CliContext plug) {
        try {
            String appHomeDir = plug.getConfigurator().getAppHomeDir();
            Path dir = Paths.get(appHomeDir, "script");
            if (Files.isDirectory(dir)) {
                Files.list(dir).forEach(p -> {
                    Path fileName = p.getFileName();
                    String s = fileName.toString();
                    String baseName = s;
                    int i = s.lastIndexOf(".");
                    if (i != -1) {
                        baseName = s.substring(0, i);
                    }

                    ShellWrapCmd cmd = new ShellWrapCmd();
                    cmd.setName(baseName);
                    cmd.addCommand(p.toAbsolutePath().toString());
                    cmd.setDescription(baseName);
                    cmd.setUsage(baseName);
                    cmd.plug(plug);
                    plug.getPluginManager().addPlugin(cmd);
                });

            } else {
                Log.warn(String.format("%s is not found. Do not load scripts.%n", dir));
            }
        } catch (IOException e) {
            Log.error(String.format("%s%n", e.getMessage()), e);
        } finally {
            plug.getPluginManager().unloadPlugin(getName());
        }
    }

}
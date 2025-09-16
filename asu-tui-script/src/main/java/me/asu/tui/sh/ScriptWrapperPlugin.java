package me.asu.tui.sh;

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
    private CmdCliController container = null;
    private CliContext ctx;

    @Override
    public String getName() {
        return CMD_NAME;
    }


    @Override
    public void plug(CliContext plug) {
        ctx = plug;
        // register to container
        List<CliController> controllers = plug.getControllers();
        if (controllers == null || controllers.isEmpty()) {
            return;
        }

        for (CliController c : controllers) {
            if (c instanceof CmdCliController) {
                container = (CmdCliController) c;
                break;
            }
        }
        if (container == null) {
            return;
        }

        try {
            String appHomeDir = ctx.getConfigurator().getAppHomeDir();
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
                    cmd.addCommand(p.toAbsolutePath().toString());
                    cmd.setDescription(baseName);
                    cmd.setUsage(baseName);
                    container.addCommand(baseName, cmd, false);
                });

            } else {
                plug.getCliConsole().printf("%s is not found. Do not load scripts.%n", dir);
            }
        } catch (IOException e) {
            plug.getCliConsole().printf("%s%n", e.getMessage());
        }
    }

}
package me.asu.tui.framework.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliController;
import me.asu.tui.framework.core.command.ShellWrapCmd;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * @author suk
 * @since 2018/8/17
 */
public class ScriptWrapController implements CliCommand
{

    private static final String           NAMESPACE = "syscmd";
    private static final String           CMD_NAME  = "script-wrap";
    private              InnerDescriptor  descriptor;
    private              Path             tmpDir;
    private              CmdCliController container = null;
    private              CliContext       ctx;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        ctx.putValue(CliContext.KEY_COMMAND_LINE_ARGS, args);
        container.handle(ctx);
        return null;
    }

    @Override
    public void plug(CliContext plug)
    {
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
                    {
                        {
                            cmd.addCommand(p.toAbsolutePath().toString());
                            cmd.setDescription(baseName);
                            cmd.setUsage(baseName);
                        }
                    }
                    ;
                    container.addCommand(baseName, cmd, false);
                });

            } else {
                plug.getCliConsole().printf("%s is not found. Do not load scripts.%n", dir);
            }
        } catch (IOException e) {
            plug.getCliConsole().printf("%s%n", e.getMessage());
        }
    }


    @Override
    public void unplug(CliContext plug)
    {
        try {
            Files.list(tmpDir).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    plug.getCliConsole().printf("%s%n", e.getMessage());
                }
            });
            Files.deleteIfExists(tmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class InnerDescriptor implements Descriptor
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
            return "Wrap script";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }


}

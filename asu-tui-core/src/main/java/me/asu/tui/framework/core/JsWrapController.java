package me.asu.tui.framework.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.asu.tui.framework.api.CliConsole;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliController;
import me.asu.tui.framework.core.command.JsWrapCmd;
import me.asu.tui.framework.core.util.CommandLineParser;

/**
 * @author suk
 * @since 2018/8/17
 */
public class JsWrapController implements CliController
{

    private Boolean           enabled  = Boolean.TRUE;
    private CliContext        ctx;
    private Path              dir;

    @Override
    public boolean handle(CliContext ctx)
    {
        CliConsole console = ctx.getCliConsole();
        CommandLineParser parser = new CommandLineParser();
        String line = (String) ctx.getValue(CliContext.KEY_COMMAND_LINE_INPUT);
        if (line != null) {
            parser.parse(line);
        } else {
            return false;
        }
        String cmd = parser.getCmd();
        Path file;
        if (!cmd.endsWith(".js")) {
            return false;
        }
        if (cmd.startsWith("/") || cmd.charAt(1) == ':') {
            file = Paths.get(cmd);
        } else {
            String appHomeDir = ctx.getConfigurator().getAppHomeDir();
            file = Paths.get(appHomeDir, "js", cmd);
        }

        if (!Files.isReadable(file)) {
            console.printf("%s is not a javascript file.%n", cmd);
        }

        JsWrapCmd jsWrapCmd = new JsWrapCmd(file);
        jsWrapCmd.plug(ctx);
        jsWrapCmd.execute(ctx, parser.getArgs());

        return true;
    }

    @Override
    public Boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean flag)
    {
        if (flag != null) {
            enabled = flag;
        }
    }

    @Override
    public void plug(CliContext plug)
    {
        ctx = plug;

    }

    @Override
    public void unplug(CliContext plug)
    {

    }


}

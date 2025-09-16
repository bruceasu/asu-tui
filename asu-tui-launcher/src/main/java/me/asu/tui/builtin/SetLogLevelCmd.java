package me.asu.tui.builtin;

import me.asu.log.Log;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author suk
 * @since 2018/8/17
 */
public class SetLogLevelCmd extends CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "set-log-level";

    @Override
    public Object execute(CliContext ctx, String[] args) {
        if (args == null || args.length == 0) {
            ctx.getCliConsole().println("Usage: " + getDescription());
            return null;
        }
        String level  =args[0];
        Log.set(level);
        return null;
    }

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
        return CMD_NAME + " <level>";
    }

}
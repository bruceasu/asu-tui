package me.asu.tui.builtin;

import me.asu.tui.CliCmdLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

/**
 * @author suk
 */
public class GcCmd implements CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "gc";
    CliCmdLineParser parser = new CliCmdLineParser();

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
        return "type 'gc' to run the garbage collector.";
    }

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        ctx.getCliConsole().printf("call System.gc() ... %n");
        System.gc();
        ctx.getCliConsole().printf("done! %n");
        return null;
    }

    @Override
    public void plug(CliContext plug) {

    }

    @Override
    public void unplug(CliContext plug) {

    }

}
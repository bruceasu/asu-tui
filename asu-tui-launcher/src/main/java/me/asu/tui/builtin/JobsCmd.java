package me.asu.tui.builtin;

import me.asu.tui.CliCmdLineParser;
import me.asu.tui.Jobs;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

public class JobsCmd extends CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "jobs";
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
        return "Prints currently running background jobs.";
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        Jobs.getInstance().print(ctx.getCliConsole());
        return null;
    }


}
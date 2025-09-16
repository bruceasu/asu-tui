package me.asu.tui.builtin;

import me.asu.tui.CliCmdLineParser;
import me.asu.tui.Jobs;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

public class JobsCmd implements CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "jobs";
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
        return "Prints currently running background jobs.";
    }

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        Jobs.getInstance().print(ctx.getCliConsole());
        return null;
    }

    @Override
    public void plug(CliContext plug) {

    }

    @Override
    public void unplug(CliContext plug) {

    }

}
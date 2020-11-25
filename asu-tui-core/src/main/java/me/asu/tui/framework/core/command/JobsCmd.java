package me.asu.tui.framework.core.command;

import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.Jobs;
import me.asu.tui.framework.util.CliCmdLineParser;

public class JobsCmd implements CliCommand
{

    private static final String          NAMESPACE = "syscmd";
    private static final String          CMD_NAME   = "jobs";
    private static final InnerDescriptor DESCRIPTOR = new InnerDescriptor();

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        Jobs.getInstance().print(ctx.getCliConsole());
        return null;
    }

    @Override
    public void plug(CliContext plug)
    {

    }

    @Override
    public void unplug(CliContext plug)
    {

    }

    private static class InnerDescriptor implements CliCommand.Descriptor
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
            return "Prints currently running background jobs.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }


}

package me.asu.tui.framework.core.command;

import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.Jobs;
import me.asu.tui.framework.util.CliCmdLineParser;

public class KillCmd implements CliCommand
{

    private static final String          NAMESPACE  = "syscmd";
    private static final String          CMD_NAME   = "killjob";
    private static final InnerDescriptor DESCRIPTOR = new InnerDescriptor();

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }


    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        int[] jobIds = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            jobIds[i] = Integer.parseInt(args[i]);
        }
        Jobs.getInstance().kill(jobIds);
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
            return getName() + " <jobId> to terminates execution of the specified jobs.\n"
                    + "\t\tThe job numbers are obtained by running the jobs Cmd.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }
}

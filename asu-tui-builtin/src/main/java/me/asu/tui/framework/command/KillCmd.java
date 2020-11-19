package me.asu.tui.framework.command;

import java.util.HashMap;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.Jobs;

public class KillCmd implements CliCommand
{

    private static final String          NAMESPACE = "syscmd";
    private static final String          CMD_NAME  = "killjob";
    private              InnerDescriptor descriptor;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
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

    private class InnerDescriptor implements CliCommand.Descriptor
    {

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
            return "Terminates execution of the specified jobs.\n"
                    + "\t\tThe job numbers are obtained by running the jobs Cmd.";
        }

        @Override
        public String getUsage()
        {
            return CliConfigurator.VALUE_LINE_SEP + "killjob <jobId>"
                    + CliConfigurator.VALUE_LINE_SEP;
        }

        @Override
        public Map<String, String> getArguments()
        {
            Map<String, String> result = new HashMap<>();
            return result;
        }

    }
}

package me.asu.tui.framework.command;

import java.util.HashMap;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.Jobs;

public class JobsCmd implements CliCommand
{
    private static final String                NAMESPACE = "syscmd";
    private static final String                CMD_NAME  = "jobs";
    private              InnerDescriptor descriptor;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
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

    private class InnerDescriptor implements CliCommand.Descriptor {

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
        public String getUsage() {
            return CliConfigurator.VALUE_LINE_SEP + "jobs" + CliConfigurator.VALUE_LINE_SEP;
        }

        @Override
        public Map<String, String> getArguments() {
            Map<String, String> result = new HashMap<>();
            return result;
        }

    }



}

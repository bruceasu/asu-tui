package me.asu.tui.framework.core.command;

import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * @author suk
 */
public class GcCmd implements CliCommand
{

    private static final String          NAMESPACE  = "syscmd";
    private static final String          CMD_NAME   = "gc";
    private static final InnerDescriptor DESCRIPTOR = new InnerDescriptor();

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        ctx.getCliConsole().printf("call System.gc() ... %n");
        System.gc();
        ctx.getCliConsole().printf("done! %n");
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
            return "type 'gc' to run the garbage collector.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }
}

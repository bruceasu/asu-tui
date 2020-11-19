package me.asu.tui.framework.command;

import java.util.HashMap;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;

/**
 * @author suk
 */
public class GcCmd implements CliCommand
{
    private static final String       NAMESPACE = "syscmd";
    private static final String          CMD_NAME  = "gc";
    private              InnerDescriptor descriptor;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
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
            return "Run the garbage collector.";
        }

        @Override
        public String getUsage() {
            return CliConfigurator.VALUE_LINE_SEP + "gc" + CliConfigurator.VALUE_LINE_SEP;
        }

        @Override
        public Map<String, String> getArguments() {
            Map<String, String> result = new HashMap<>();
            return result;
        }

    }
}

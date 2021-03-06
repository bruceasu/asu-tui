package me.asu.tui.framework.core.command;

import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConsole;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.History;
import me.asu.tui.framework.util.CliCmdLineParser;

public class HistoryCmd implements CliCommand
{

    private static final String          NAMESPACE = "syscmd";
    private static final String                       CMD_NAME   = "history";
    private  static final             InnerDescriptor DESCRIPTOR = new InnerDescriptor();


    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        CliConsole console = ctx.getCliConsole();
        console.printf("==================================================%n");
        console.printf("History：%n");
        console.printf("--------------------------------------------------%n");
        if (args == null || args.length == 0) {
            History.printLast(0, console);
        } else {
            History.printLast(Integer.parseInt(args[0]), console);
        }
        console.printf("--------------------------------------------------%n");
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
            return "history [n] prints the last n commands. If n is omitted,\n"
                    + "\t\tall recorded commands are printed. In both cases,\n"
                    + "\t\tthe number of commands printed is limited by the\n"
                    + "\t\tvalue of asu.shell.history_size.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }
}

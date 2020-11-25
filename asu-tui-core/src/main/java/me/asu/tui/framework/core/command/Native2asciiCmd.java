package me.asu.tui.framework.core.command;

import java.util.HashMap;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliConsole;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliArguments;
import me.asu.tui.framework.util.CliCmdLineOption;
import me.asu.tui.framework.util.CliCmdLineParser;

public class Native2asciiCmd implements CliCommand
{
    private static final String       NAMESPACE = "syscmd";
    private static final String                        CMD_NAME   = "native2ascii";
    private    static final            InnerDescriptor DESCRIPTOR = new InnerDescriptor();

    CliCmdLineParser parser = new CliCmdLineParser();

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {

        CliConsole console = ctx.getCliConsole();
        try {
            if (args == null || args.length == 0) {
                DESCRIPTOR.printUsage(console);
                return null;
            }
            CliArguments arguments = parser.parse(args);
            if (arguments.hasParam("h") ) {
                DESCRIPTOR.printUsage(console);
            } else if (arguments.hasParam("r") && arguments.hasParam("s")) {
                unescape(console, arguments);
            } else if (arguments.hasParam("s")) {
                escape(console, arguments);
            } else {
                DESCRIPTOR.printUsage(console);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void escape(CliConsole console, CliArguments arguments)
    {
        String s = arguments.getParam("s");
        if (s== null || s.trim().isEmpty()) {
            console.printf("%n");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, j = s.length(); i < j; i++) {
            int ch = s.charAt(i);
            if (ch < 127) {
                builder.append((char) ch);
            } else {
                String x = Integer.toHexString(ch);
                if (x.length() == 1) {
                    x = "000" + x;
                } else if (x.length() == 2) {
                    x = "00" + x;
                } else if (x.length() == 3) {
                    x = "0" + x;
                }
                builder.append("\\u").append(x);
            }
        }
        console.printf("%s%n", builder.toString());
    }

    private void unescape(CliConsole console, CliArguments arguments)
    {
        String s = arguments.getParam("s");
        int len = s.length();
        int start = 0;
        StringBuilder builder = new StringBuilder();
        while (start < len) {
            int i = s.indexOf("\\u", start);
            if (i != -1 && i != start) {
                String x = s.substring(start, i);
                builder.append(x);
                String h = s.substring(i + 2, i + 6);
                int ch = Integer.parseInt(h, 16);
                builder.append((char) ch);
                start = i + 6;
            } else if (i != -1 && i == start) {
                String h = s.substring(i + 2, i + 6);
                int ch = Integer.parseInt(h, 16);
                builder.append((char) ch);
                start = i + 6;
            } else {
                String x = s.substring(start);
                builder.append(x);
                start = len;
            }
        }
        console.printf("%s%n", builder.toString());
    }

    @Override
    public void plug(CliContext plug)
    {

    }

    @Override
    public void unplug(CliContext plug)
    {

    }

    static class InnerDescriptor implements CliCommand.Descriptor {
        private CliCmdLineParser parser = new CliCmdLineParser();

        public InnerDescriptor()
        {
            CliCmdLineOption opt1 = CliCmdLineOption.builder().shortName("h").longName("help").description("Print the help").build();
            CliCmdLineOption opt2 = CliCmdLineOption.builder().shortName("s").longName("source").description("Source Text").hasArg(true).build();
            CliCmdLineOption opt3 = CliCmdLineOption.builder().shortName("r").longName("revert").description("Reversal").build();
            this.parser.addOption(opt1, opt2, opt3);
        }

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
            return "Escape or unescape unicode character to ascii";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }

}

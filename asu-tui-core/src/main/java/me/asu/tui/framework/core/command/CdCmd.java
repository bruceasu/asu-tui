package me.asu.tui.framework.core.command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;

/**
 * @author suk
 * @since 2018/8/17
 */
public class CdCmd implements CliCommand
{
    private static final   String          NAMESPACE = "syscmd";
    private static final String          CMD_NAME  = "cd";
    private              InnerDescriptor descriptor;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
    }


    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        if (args == null || args.length == 0 || "~".equalsIgnoreCase(args[0])) {
            String home = System.getProperty("user.home");
            System.setProperty("user.dir", home);
        } else if ("..".equalsIgnoreCase(args[0])) {
            Path path = Paths.get(".").toAbsolutePath();
            String s = path.getParent().toString();
            System.setProperty("user.dir", s);
        } else {
            Path path = Paths.get(args[0]).toAbsolutePath();
            if (Files.isDirectory(path)) {
                System.setProperty("user.dir", path.toString());
            } else {
                ctx.getCliConsole().printf("%s is not a directory.%n", args[0]);
            }
        }
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
            return "Change directory.";
        }

        @Override
        public String getUsage() {
            return CliConfigurator.VALUE_LINE_SEP + "cd <folder>" + CliConfigurator.VALUE_LINE_SEP;
        }

        @Override
        public Map<String, String> getArguments() {
            Map<String, String> result = new HashMap<>();
            return result;
        }

    }
}

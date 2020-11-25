package me.asu.tui.framework.core.command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * @author suk
 * @since 2018/8/17
 */
public class CdCmd implements CliCommand
{

    private static final String          NAMESPACE = "syscmd";
    private static final String                       CMD_NAME  = "cd";
    private static final              InnerDescriptor DESCRIPTOR = new InnerDescriptor();

    @Override
    public Descriptor getDescriptor()
    {
        return DESCRIPTOR;
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
            return "cd <dir> Change directory.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }
}

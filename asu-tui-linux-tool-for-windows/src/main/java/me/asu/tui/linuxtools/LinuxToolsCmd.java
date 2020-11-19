package me.asu.tui.linuxtools;

import io.spring.IResource;
import io.spring.PathMatchingResourcePatternResolver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliController;
import me.asu.tui.framework.command.ShellWrapCmd;
import me.asu.tui.framework.core.CmdCliController;

/**
 * @author suk
 * @since 2018/8/17
 */
public class LinuxToolsCmd implements CliCommand
{

    private static final String           NAMESPACE = "syscmd";
    private static final String           CMD_NAME  = "wraplinuxtool";
    private              InnerDescriptor  descriptor;
    private              Path             tmpDir;
    private              CmdCliController container = null;
    private              CliContext       ctx;

    @Override
    public Descriptor getDescriptor()
    {
        return (descriptor != null) ? descriptor : (descriptor = new InnerDescriptor());
    }

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        ctx.putValue(CliContext.KEY_COMMAND_LINE_ARGS, args);
        container.handle(ctx);
        return null;
    }

    @Override
    public void plug(CliContext plug)
    {
        ctx = plug;
        // register to container
        List<CliController> controllers = plug.getControllers();
        if (controllers == null || controllers.isEmpty()) {
            return;
        }

        for (CliController c : controllers) {
            if (c instanceof CmdCliController) {
                container = (CmdCliController) c;
                break;
            }
        }
        if (container == null) {
            return;
        }

        try {
            // extract exe
            boolean extracted = extractExecutablePrograms();
            if (extracted) {
                Files.list(tmpDir).forEach(p -> {
                    Path fileName = p.getFileName();
                    String s = fileName.toString();
                    String baseName = s.substring(0, s.length() - 4);
                    String ext = s.substring(s.length() - 4);
                    if (!".exe".equalsIgnoreCase(ext)) {
                        return;
                    }
                    ShellWrapCmd cmd = new ShellWrapCmd()
                    {{
                        setName(baseName);
                        if ("vim".equalsIgnoreCase(baseName)) {
                            addCommand("start", p.toAbsolutePath().toString());
                        } else {
                            addCommand(p.toAbsolutePath().toString());
                        }

                        setDescription(baseName);
                        setUsage(baseName);
                    }};
                    container.addCommand(baseName, cmd, false);
                });

            }
        } catch (IOException e) {
            plug.getCliConsole().printf("%s%n", e.getMessage());
        }
    }

    private boolean extractExecutablePrograms() throws IOException
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                LinuxToolsCmd.class.getClassLoader());
        IResource[] resources = resolver.getResources("classpath*:/linux/*");
        if (resources != null) {
            tmpDir = Files.createTempDirectory("linuxtools");
            for (IResource resource : resources) {
                //ctx.getCliConsole().printf("Found %s.%n", resource);
                try (InputStream inputStream = resource.getInputStream()) {
                    String filename = resource.getFilename();
                    Path path = Paths.get(tmpDir.toAbsolutePath().toString(), filename);
                    byte[] bytes = readBytes(inputStream);
                    Files.write(path, bytes);
                }
            }
            return true;
        } else {
            //ctx.getCliConsole().printf("Not found linux programs. %n");
            return false;
        }
    }

    private byte[] readBytes(InputStream ins) throws IOException
    {
        ByteArrayOutputStream b=  new ByteArrayOutputStream();
        byte[] buffer = new byte[8096];
        int read = 0;
        while((read = ins.read(buffer)) >0 ) {
            b.write(buffer, 0, read);
        }

        return b.toByteArray();
    }

    @Override
    public void unplug(CliContext plug)
    {
        try {
            Files.list(tmpDir).forEach(p-> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    plug.getCliConsole().printf("%s%n", e.getMessage());
                }
            });
            Files.deleteIfExists(tmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            return "Wrap linux tools for windows";
        }

        @Override
        public String getUsage()
        {
            return "";
        }

        @Override
        public Map<String, String> getArguments()
        {
            Map<String, String> result = new HashMap<>();
            result.put("-h", "Print the help");
            result.put("-s <source>", "Source Text");
            result.put("-r", "Reversal");
            return result;
        }

    }


}

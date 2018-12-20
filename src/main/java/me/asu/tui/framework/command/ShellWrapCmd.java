package me.asu.tui.framework.command;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import lombok.Data;
import me.asu.tui.framework.api.*;
import me.asu.tui.framework.core.Shell.Runtime;

/**
 * @author suk
 * @since 2018/8/17
 */
@Data
public class ShellWrapCmd implements Command {

    private static final String              NAMESPACE  = "shcmd";
    private final        ShellCmdDescriptor  descriptor = new ShellCmdDescriptor();
    private final        Map<String, String> arguments  = new HashMap<String, String>();
    private String name;
    private List<String> commands = new ArrayList<String>();
    private String description;
    private String usage;
    private String charset = Charset.defaultCharset().name();

    @Override
    public void plug(Context plug) {

    }

    @Override
    public void unplug(Context plug) {

    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void addCommand(String... cmd) {
        if (cmd.length > 0) {
            commands.addAll(Arrays.asList(cmd));
        }
    }

    @Override
    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);

        ProcessBuilder builder = new ProcessBuilder();
        addPath(builder, ctx);
        //  add shell
        boolean isWindows = Runtime.isWindows();
        List<String> commands = new ArrayList<String>();
        if (isWindows) {
            commands.add("cmd");
            commands.add("/c");
        } else {
            // commands.add("bash");
            // commands.add("-c");
        }

        // add real command
        if (!getCommands().isEmpty()) {
            commands.addAll(getCommands());
        }
        // add parameters
        if (args != null && args.length > 0) {
            commands.addAll(Arrays.asList(args));
        }

        builder.command(commands);
        try {
            Process p = builder.start();
            new PipeThread(new InputStreamReader(p.getInputStream(), getCharset()),
                    ctx.getConsole().writer()).start();
            new PipeThread(new InputStreamReader(p.getErrorStream(), getCharset()),
                    ctx.getConsole().writer()).start();
            int i = p.waitFor();
            ctx.getConsole().printf("process return: %d%n", i);
            return i;
        } catch (IOException e) {
            ctx.getConsole().printf("%s%n", e.getMessage());
        } catch (InterruptedException e) {
            ctx.getConsole().printf("%s%n", e.getMessage());
        }
        return 1;
    }

    /**
     * 把自身的执行目录添加到环境变量PATH中
     *
     * @param builder {@link ProcessBuilder}
     */
    protected void addPath(ProcessBuilder builder, Context ctx) {
        String appHomeDir = ctx.getConfigurator().getAppHomeDir();
        String userDir = Configurator.VALUE_USER_DIR;
        String userHome = Configurator.VALUE_USER_HOME;
        String pathSeparator = Configurator.VALUE_PATH_SEPARATOR;
        Map<String, String> environment = builder.environment();
        String path = System.getenv("PATH");
        if (appHomeDir != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + appHomeDir;
            } else {
                path = path + pathSeparator + appHomeDir;
            }
        }
        if (userDir != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + userDir;
            } else {
                path = path + pathSeparator + userDir;
            }
        }
        if (userHome != null) {
            if (path.endsWith(pathSeparator)) {
                path = path + userHome;
            } else {
                path = path + pathSeparator + userHome;
            }
        }
        environment.put("PATH", path);
    }

    protected class PipeThread extends Thread {

        Reader in;
        Writer out;
        char[] buf = new char[1024];

        public PipeThread(Reader i, Writer o) {
            if (i == null) {
                throw new IllegalArgumentException("不能为空。");
            }
            if (o == null) {
                throw new IllegalArgumentException("不能为空。");
            }
            this.in = i;
            this.out = o;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    int read = in.read(buf);
                    if (read == -1) {
                        break;
                    }
                    out.write(buf, 0, read);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class ShellCmdDescriptor implements Command.Descriptor {

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getUsage() {
            return usage;
        }

        @Override
        public Map<String, String> getArguments() {
            return arguments;
        }
    }
}

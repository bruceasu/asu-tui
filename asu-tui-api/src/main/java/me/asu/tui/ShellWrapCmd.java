package me.asu.tui;

import lombok.Data;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliConfigurator;
import me.asu.tui.api.CliContext;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author suk
 * @since 2018/8/17
 */
@Data
public class ShellWrapCmd implements CliCommand {

    private static final String NAMESPACE = "shcmd";
    private final Map<String, String> arguments = new HashMap<String, String>();
    CliCmdLineParser parser = new CliCmdLineParser();
    private String name;
    private List<String> commands = new ArrayList<String>();
    private String description;
    private String usage;
    private String charset = Charset.defaultCharset().name();

    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return (osName.startsWith("Windows"));
    }

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
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public void plug(CliContext plug) {

    }

    @Override
    public void unplug(CliContext plug) {

    }

    public void addCommand(String... cmd) {
        if (cmd.length > 0) {
            commands.addAll(Arrays.asList(cmd));
        }
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {

        ProcessBuilder builder = new ProcessBuilder();
        addPath(builder, ctx);
        //  add shell
        boolean isWindows = isWindows();
        List<String> commands = new ArrayList<String>();
        if (isWindows) {
            commands.add("cmd");
            commands.add("/c");
        } else {
            commands.add("bash");
            commands.add("-c");
        }

        // add real command
        if (!getCommands().isEmpty()) {
            commands.addAll(getCommands());
        }
        // add parameters
        if (args != null && args.length > 0) {
            commands.addAll(Arrays.asList(args));
        }
        builder.directory(new File(System.getProperty("user.dir")));
        builder.command(commands);
        try {
            Process p = builder.start();
            new PipeThread(new InputStreamReader(p.getInputStream(), getCharset()),
                    ctx.getCliConsole().writer()).start();
            new PipeThread(new InputStreamReader(p.getErrorStream(), getCharset()),
                    ctx.getCliConsole().writer()).start();
            int i = p.waitFor();
            if (i != 0) {
                ctx.getCliConsole().printf("process return: %d%n", i);
            }
            return i;
        } catch (IOException e) {
            ctx.getCliConsole().printf("%s%n", e.getMessage());
        } catch (InterruptedException e) {
            ctx.getCliConsole().printf("%s%n", e.getMessage());
        }
        return 1;
    }

    /**
     * 把自身的执行目录添加到环境变量PATH中
     *
     * @param builder {@link ProcessBuilder}
     */
    protected void addPath(ProcessBuilder builder, CliContext ctx) {
        String appHomeDir = ctx.getConfigurator().getAppHomeDir();
        String userDir = CliConfigurator.VALUE_USER_DIR;
        String userHome = CliConfigurator.VALUE_USER_HOME;
        String pathSeparator = CliConfigurator.VALUE_PATH_SEPARATOR;
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


}
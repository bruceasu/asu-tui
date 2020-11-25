package me.asu.tui;


import static me.asu.tui.framework.api.CliContext.KEY_COMMAND_LINE_ARGS;

import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.core.CliRuntime;
import me.asu.tui.framework.core.TextCliShell;
import me.asu.tui.framework.core.api.CliShell;

/**
 * @author suk
 * @since 2018/8/16
 */
public class Tool
{

    private static String startPath = "";

    //private static GuiConsole guiConsole;

    private static boolean useGUI = false;


    public static void main(String[] args)
    {
        CliRuntime.init();
        CliContext context = CliRuntime.getContext();
        CliShell shell;
        try {
            String debug = (String)context.getConfigurator().getProperty("debug", "false");
            context.setDebug(Boolean.parseBoolean(debug));
            shell = new TextCliShell();
            context.setShell(shell);
            if (args != null && args.length > 0) {
                context.putValue(KEY_COMMAND_LINE_ARGS, args);
                shell.exec(context);
            } else {
                shell.plug(context);
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong while bootstrapping the Shell:");
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        // before launch, register shutdown handler
        java.lang.Runtime.getRuntime().addShutdownHook(new ShutdownHook(context));


    }

    //TODO careful, Cotext is not thread-safe.
    private static class ShutdownHook extends Thread
    {

        private final CliContext context;

        public ShutdownHook(final CliContext ctx)
        {
            context = ctx;
        }

        @Override
        public void run()
        {
            CliShell s = (CliShell) context.getShell();
            if (s != null) {
                s.unplug(context);
            }
        }
    }

}
package me.asu.tui;


import me.asu.tui.api.CliContext;

import static me.asu.tui.api.CliContext.KEY_COMMAND_LINE_ARGS;

/**
 * @author suk
 * @since 2018/8/16
 */
public class Launcher {
    public static void main(String[] args) {
        CliRuntime.init();
        CliContext context = CliRuntime.getContext();
        CliShell shell;
        try {
            String debug = (String) context.getConfigurator().getProperty("debug", "false");
            context.setDebug(Boolean.parseBoolean(debug));
            shell = new CliShell();
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
    private static class ShutdownHook extends Thread {

        private final CliContext context;

        public ShutdownHook(final CliContext ctx) {
            context = ctx;
        }

        @Override
        public void run() {
            CliShell s = (CliShell) context.getShell();
            if (s != null) {
                s.unplug(context);
            }
        }
    }

}
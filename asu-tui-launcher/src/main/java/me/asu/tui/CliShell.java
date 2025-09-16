package me.asu.tui;

import me.asu.tui.api.*;
import me.asu.tui.builtin.DefaultCliPrompt;
import me.asu.tui.builtin.DefaultCliSplashScreen;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.asu.tui.I18nUtils.text;
import static me.asu.tui.api.CliContext.*;

/**
 * @author suk
 */
public final class CliShell {
    private CliContext ctx;
    private CliPrompt prompt;
    private CliConsole console;
    private Thread consoleThread;
    private AtomicBoolean loopRunning;
    private List<CliController> controllers;

    /**
     * This method will be called when the shell is invoked to handle commands
     * from the OS passed in as arguments.  This is used to allow the shell to
     * work in silence (pass-through non-interactive) mode.
     *
     * @param plug instance of Context
     */
    public void exec(CliContext plug) {
        ctx = plug;
        loadComponents(false);
        //throw new UnsupportedOperationException("Not supported yet.");
        String[] args = (String[]) ctx.getValue(CliContext.KEY_COMMAND_LINE_ARGS);
        if (controllers == null || controllers.isEmpty()) {
            console.printf("No Controller found.");
        } else {
            //ctx.putValue(KEY_COMMAND_LINE_ARGS, args);
            for (CliController controller : controllers) {
                boolean handle = controller.handle(ctx);
                if (handle) {
                    break;
                }
            }
        }
    }

    /**
     * Implements initialization logic when the shell is launched.
     * It works by loading the following components from the classpath
     * in the following order:
     * <ul>
     * <li> Plugin in a SplashScreen component.
     * <li> looks for a Console plugin and plugs it in
     * <li> Plug in the first instance of Controllers
     *
     * @param plug instance of Context
     */
    public void plug(CliContext plug) {
        ctx = plug;
        loadComponents(true);
        loopRunning = new AtomicBoolean(true);
        startConsoleThread();
    }

    public void unplug(CliContext plug) {
        unloadComponent();
        if (loopRunning != null) {
            loopRunning.set(false);
        }
        if (consoleThread != null) {
            consoleThread.interrupt();
        }
        try {
            plug.getCliConsole().unplug(plug);
        } catch (Exception ex) {
            System.out.println("WARNING: unable to properly unplug the Console instance.");
        }

    }

    /**
     * Load components.
     * Create default where possible if none found on classpath.
     */
    private void loadComponents(boolean interactive) {
        ctx.putValue(CliContext.KEY_INPUT_STREAM, System.in);
        ctx.putValue(CliContext.KEY_OUTPUT_STREAM, System.out);
        ctx.putValue(CliContext.KEY_ERROR_STREAM, System.err);

        activateConsole();
        activateControllers();
        if (interactive) {
            activePrompt();
            activeSplashScreen();
        }
    }

    private void activeSplashScreen() {
        // activate/show splash screens
        List<CliSplashScreen> screens = ctx.getPluginsByType(CliSplashScreen.class);
        if (screens == null || screens.isEmpty()) {
            screens = Arrays.asList(new DefaultCliSplashScreen());
        }
        ctx.putValue(KEY_SPLASH_SCREENS, screens);

        for (CliSplashScreen sc : screens) {
            try {
                sc.plug(ctx);
                sc.render(ctx);
            } catch (Exception ex) {
                console.printf("WARNING: unable to load/render SplashScreen instance %s%n%s%n",
                        sc.getClass(), ex.getMessage());
            }
        }
    }

    private void activePrompt() {
        List<CliPrompt> prompts = ctx.getPluginsByType(CliPrompt.class);
        prompt = (prompts.size() > 0) ? prompts.get(0) : new DefaultCliPrompt();
        try {
            prompt.plug(ctx);
        } catch (Exception ex) {
            console.printf("WARNING: Unable to load specied prompt instance.%n"
                    + "Will use a generic instance: %s%n ", ex.getMessage());
            prompt = new DefaultCliPrompt();
        } finally {
            ctx.putValue(KEY_PROMPT_COMPONENT, prompt); // save for later use.
        }
    }

    private void activateControllers() {
        controllers = ctx.getPluginsByType(CliController.class);
        if (controllers.size() > 0) {
            ctx.putValue(KEY_CONTROLLERS, controllers);
            boolean sucOne = false;
            for (CliController ctrl : controllers) {
                try {
                    ctrl.plug(ctx);
                    sucOne = true;
                } catch (Exception ex) {
                    System.out.printf(text("controller.plug.error"), ctrl.getClass(),
                            ex.getMessage());
                    ex.printStackTrace();
                    ctrl.setEnabled(false);
                }
            }
            if (!sucOne) {
                System.exit(1);
            }
        } else {
            System.out.printf(text("controller.plug.not.found.error"));
            System.exit(1);
        }
    }

    private void activateConsole() {
        try {
            console = new JlineCliConsole(); // new TextCliConsole();
            console.plug(ctx);
            ctx.setCliConsole(console);
        } catch (Exception ex) {
            System.out.println(text("console.plug.not.found.error"));
            System.exit(1);
        }
    }

    private void unloadComponent() {
        // unplug controllers
        for (CliController ctrl : ctx.getControllers()) {
            try {
                ctrl.unplug(ctx);
            } catch (Exception ex) {
                System.out.printf(text("controller.unplug.error"), ctrl.getClass(),
                        ex.getMessage());
            }
        }
        // unplug splash screens
        List<CliSplashScreen> sp = (List<CliSplashScreen>) ctx.getValue(KEY_SPLASH_SCREENS);
        if (sp != null && !sp.isEmpty()) {
            for (CliSplashScreen screen : sp) {
                try {
                    screen.unplug(ctx);
                } catch (Exception ex) {
                    console.printf("WARNING: unable to unplug SplashScreen instance %s%n%s%n",
                            screen.getClass(), ex.getMessage());
                }
            }
        }

        // unplug the prompt
        CliPrompt p = (CliPrompt) ctx.getValue(KEY_PROMPT_COMPONENT);
        if (p != null) {
            try {
                p.unplug(ctx);
            } catch (Exception ex) {
                console.printf("WARNING: unable to unplug Prompt instance %s%n%s%n", prompt.getClass(), ex.getMessage());
            }
        }
    }

    public void startConsoleThread() {
        consoleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (loopRunning.get()) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    // reset command line arguments from previous command
                    ctx.putValue(CliContext.KEY_COMMAND_LINE_ARGS, null);

                    boolean handled = false;
                    String line = console.readLine(prompt.getValue(ctx));

                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }

                    ctx.putValue(KEY_COMMAND_LINE_INPUT, line);
                    if (controllersExist()) {
                        for (CliController controller : controllers) {
                            Boolean enabled = controller.isEnabled();
                            // let controller handle input line if enabled
                            if (enabled) {
                                try {
                                    boolean ctrlResult = controller.handle(CliShell.this.ctx);
                                    handled = handled || ctrlResult;
                                } catch (Exception ex) {
                                    console.printf("Unable to complete command:%n%s%n", ex.getMessage());
                                }
                            }
                        }
                        // was command line handled.
                        if (!handled) {
                            console.printf("%nCommand unhandled." +
                                    "%nNo controllers found to handle [%s].%n", line
                            );
                        }
                    } else {
                        console.printf("%nWarning: no controllers(s) found.%n");
                    }
                }
            }
        });
        consoleThread.start();
    }

    /**
     * Are there any controllers installed?
     */
    private boolean controllersExist() {
        return (controllers != null && controllers.size() > 0);
    }


}
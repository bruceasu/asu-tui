package me.asu.tui.builtin;


import me.asu.tui.api.CliConfigurator;
import me.asu.tui.api.CliConsole;
import me.asu.tui.api.CliContext;
import me.asu.tui.api.CliSplashScreen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is a SplashScreen plugin implementation to display a default ClamShell-Cli
 * splash screen.
 *
 * @author vladimir.vivien
 */
public class DefaultCliSplashScreen implements CliSplashScreen {
    private StringBuilder screen;

    @Override
    public void render(CliContext ctx) {
        CliConsole console = ctx.getCliConsole();
        console.printf("%s%n", screen.toString());
    }

    @Override
    public void plug(CliContext plug) {
        screen = new StringBuilder();
        String appHomeDir = plug.getConfigurator().getAppHomeDir();
        Path p = Paths.get(appHomeDir, "banner.txt");
        screen.append(CliConfigurator.VALUE_LINE_SEP)
                .append(CliConfigurator.VALUE_LINE_SEP);
        if (Files.exists(p)) {
            try {
                byte[] bytes = Files.readAllBytes(p);
                screen.append(new String(bytes));
            } catch (IOException e) {
                // ignore
            }
        } else {
            // http://www.patorjk.com/software/taag/#p=display&c=c%2B%2B&f=Basic&t=AsuShell
            screen.append(" .d8b.  .d8888. db    db .d8888. db   db d88888b db      db     ").append(CliConfigurator.VALUE_LINE_SEP)
                    .append("d8' `8b 88'  YP 88    88 88'  YP 88   88 88'     88      88     ").append(CliConfigurator.VALUE_LINE_SEP)
                    .append("88ooo88 `8bo.   88    88 `8bo.   88ooo88 88ooooo 88      88     ").append(CliConfigurator.VALUE_LINE_SEP)
                    .append("88~~~88   `Y8b. 88    88   `Y8b. 88~~~88 88~~~~~ 88      88     ").append(CliConfigurator.VALUE_LINE_SEP)
                    .append("88   88 db   8D 88b  d88 db   8D 88   88 88.     88booo. 88booo.").append(CliConfigurator.VALUE_LINE_SEP)
                    .append("YP   YP `8888Y' ~Y8888P' `8888Y' YP   YP Y88888P Y88888P Y88888P").append(CliConfigurator.VALUE_LINE_SEP)
                    .append(CliConfigurator.VALUE_LINE_SEP)
                    .append("                                                  Command-Line Interpreter")
                    .append(CliConfigurator.VALUE_LINE_SEP);
        }

        screen.append(CliConfigurator.VALUE_LINE_SEP);
        screen.append("Java version: ").append(System.getProperty("java.version")).append(CliConfigurator.VALUE_LINE_SEP);
        screen.append("Java Home: ").append(System.getProperty("java.home")).append(CliConfigurator.VALUE_LINE_SEP);
        screen.append("OS: ").append(System.getProperty("os.name")).append(", Version: ").append(System.getProperty("os.version"));
        screen.append(CliConfigurator.VALUE_LINE_SEP);
        screen.append(CliConfigurator.VALUE_LINE_SEP);
    }

    @Override
    public void unplug(CliContext plug) {
    }

}
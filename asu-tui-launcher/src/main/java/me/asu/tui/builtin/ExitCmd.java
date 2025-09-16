package me.asu.tui.builtin;

import me.asu.tui.CliCmdLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

public class ExitCmd extends CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "exit";


    CliCmdLineParser parser = new CliCmdLineParser();

    @Override
    public Object execute(CliContext ctx, String[] args) {
        System.exit(0);
        return null;
    }


    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public String getDescription() {
        return "Type 'exit' to exits ClamShell.";
    }

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }
}
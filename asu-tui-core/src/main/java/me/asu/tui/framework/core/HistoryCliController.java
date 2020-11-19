package me.asu.tui.framework.core;

import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliController;


/**
 * @author suk
 */
public class HistoryCliController implements CliController
{

    CliContext ctx;
    boolean    enabled = true;

    /**
     * Handles incoming command-line input.  CmdController first splits the input into token[N]
     * tokens.  It uses token[0] as the action name mapped to the Command.
     *
     * @param ctx the shell context.
     */
    @Override
    public boolean handle(CliContext ctx)
    {
        String line = (String) ctx.getValue(CliContext.KEY_COMMAND_LINE_INPUT);
        History.add(line);
        return true;
    }

    @Override
    public Boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean flag)
    {
        enabled = flag;
    }

    /**
     * Entry point for the plugin.  It builds class path from 'command' directory. Then loads each
     * Command found.
     */
    @Override
    public void plug(CliContext plug)
    {
        this.ctx = plug;
        History.load();
    }

    @Override
    public void unplug(CliContext plug)
    {
        History.store();
    }

}

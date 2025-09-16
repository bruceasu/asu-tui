package me.asu.tui.builtin;


import me.asu.tui.api.CliContext;
import me.asu.tui.api.CliPrompt;

/**
 * Default Prompt implementation.
 * Will be used if none is found on classpath.
 *
 * @author suk
 */
public class DefaultCliPrompt implements CliPrompt {
    @Override
    public String getValue(CliContext ctx) {
        return "cli>";
    }

    @Override
    public void plug(CliContext plug) {
    }

    @Override
    public void unplug(CliContext plug) {
    }
}
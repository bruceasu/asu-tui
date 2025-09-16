package me.asu.tui;


import me.asu.tui.api.CliContext;
import me.asu.tui.api.CliPrompt;

/**
 * Default Prompt implementation.
 * Will be used if none is found on classpath.
 *
 * @author suk
 */
public class JlineCliPrompt implements CliPrompt {
    protected String prompt = "\u001B[32mmcli>\u001B[0m ";

    @Override
    public String getValue(CliContext ctx) {
        return prompt;
    }

    @Override
    public void plug(CliContext plug) {
    }

    @Override
    public void unplug(CliContext plug) {
    }
}
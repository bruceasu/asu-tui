package me.asu.tui.framework.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliPlugin;

/**
 * A Prompt is responsible for generating the prompt in that appears
 * in the console.  Every time prompt is displayed, the loaded prompt will be
 * displayed using the getValue() method.
 * @author vladimir.vivien
 */
public interface CliPrompt extends CliPlugin
{
    /**
     * Implementation of this method should return the current prompt value.
     * Keep in mind that this is called every time the console displays a 
     * prompt.  So, you may choose to provide a cached value for performance if
     * your prompt takes a while to calculate.
     * @param ctx Instance of Context
     * @return the value for the prompt
     */
    default String getValue(CliContext ctx){
        String home = System.getProperty("user.home");
        Path path = Paths.get(".").toAbsolutePath();
        return String.format("ASU-Shell [%s]> ", path.getFileName());
    }

    @Override
    default void plug(CliContext plug) {
    }

    @Override
    default void unplug(CliContext plug) {
    }
}

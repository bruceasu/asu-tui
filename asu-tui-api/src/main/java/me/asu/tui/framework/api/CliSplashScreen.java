package me.asu.tui.framework.api;

/**
 * The SplashScreen plugin lets you build a textual screen that is displayed
 * at the start of the shell.
 * 
 * @author suk
 */
public interface CliSplashScreen extends CliPlugin
{
    /**
     * This method is called when the Shell is ready to display the SplashScreen.
     * @param ctx 
     */
    public void render(CliContext ctx);
}

package me.asu.tui.framework.command;

/**
 * @author suk
 * @since 2018/8/17
 */
public class EchoCmd extends ShellWrapCmd {
    public EchoCmd() {
        setName("echo");
        addCommand("echo");
        setDescription("Echo the input");
        setUsage("echo [strings]");
    }
}

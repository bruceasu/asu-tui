package me.asu.tui.framework.command;

/**
 * @author suk
 * @since 2018/8/17
 */
public class DirCmd extends ShellWrapCmd {
    public DirCmd() {
        setName("dir");
        addCommand("dir");
        setDescription("dir folder");
        setUsage("dir [folder]");
    }
}

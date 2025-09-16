package me.asu.tui.api;

public interface CmdCliController extends CliController {
    void addCommand(String name, CliCommand com, boolean replace);
}
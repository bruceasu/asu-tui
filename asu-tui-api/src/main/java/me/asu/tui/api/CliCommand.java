package me.asu.tui.api;

import me.asu.tui.CliArguments;
import me.asu.tui.CliCmdLineParser;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * The Command component is there to allow Controllers to delegate tasks.
 * Each command exposes a textual id.  This can be used to identify the action
 * (request) that will invoke the execute() method on that command.
 *
 * @author vladimir.vivien
 */
public interface CliCommand extends CliPlugin {
    default boolean hasSubCommands() {return subCommands().isEmpty();}

    default Map<String, ? extends CliCommand> subCommands() {return Collections.emptyMap();}

    /**
     * This method will be called as the starting point to execute the logic
     * for the action mapped to this command.
     *
     * @param ctx
     * @return Object
     */
    Object execute(CliContext ctx, String[] args);

    /**
     * The purpose of the namespace is to provide an identifier to group
     * command without relying on class name or other convoluted approaches
     * to group command.
     *
     * @return the command's namespace
     */
    default String getNamespace() {
        final Optional<CmdInfo> o = getCmdInfo();
        if (o.isPresent()) {
            final CmdInfo cmdInfo = o.get();
            final String v = cmdInfo.ns();
            if (v == null || v.trim().isEmpty()) return "";
            return v;
        } else {
            return "";
        }
    }

    /**
     * Implementation of this method should return a simple string (with no spaces)
     * that identifies the action mapped to this command.
     *
     * @return the name of the action mapped to this command.
     */
    default String getName() {
        final Optional<CmdInfo> o = getCmdInfo();
        if (o.isPresent()) {
            final CmdInfo cmdInfo = o.get();
            final String v = cmdInfo.value();
            if (v == null || v.trim().isEmpty()) return "";
            return v;
        } else {
            return "";
        }
    }

    /**
     * This method should return a descriptive text about the command
     * it is attached to.
     *
     * @return description
     */
    default String getDescription() {

        final Optional<CmdInfo> o = getCmdInfo();
        if (o.isPresent()) {
            final CmdInfo cmdInfo = o.get();
            final String v = cmdInfo.desc();
            if (v == null || v.trim().isEmpty()) return "";
            return v;
        } else {
            return "";
        }
    }

    CliCmdLineParser getCliCmdLineParser();

    default CliArguments parse(String[] args) {
        CliCmdLineParser cliCmdLineParser = getCliCmdLineParser();
        if (cliCmdLineParser != null) {
            return cliCmdLineParser.parse(args);
        } else {
            return new CliArguments();
        }
    }

    default void printUsage(CliConsole c) {
        CliCmdLineParser cliCmdLineParser = getCliCmdLineParser();
        if (cliCmdLineParser != null) {
            String usage = cliCmdLineParser.usage(getName());
            c.printf("%s%n%s%n", getDescription(), usage);
            c.printf("%n");
        } else {
            final Optional<CmdInfo> o = getCmdInfo();
            if (o.isPresent()) {
                final CmdInfo cmdInfo = o.get();
                final String v = cmdInfo.ns();
                if (v == null || v.trim().isEmpty()) return;
                c.printf("%s%n", v);
            } else {
                return;
            }
        }
    }

    default Optional<CmdInfo> getCmdInfo() {
        // 使用 this.getClass() 获取实现类的 Class 对象
        return Optional.ofNullable(this.getClass().getAnnotation(CmdInfo.class));
    }
}
/*
 * Copyright 2012 ClamShell-Cli.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.asu.tui.framework.core;

import static me.asu.tui.framework.core.ResourceUtils.text;

import java.util.*;
import me.asu.tui.framework.api.*;
import me.asu.tui.framework.core.Shell.Runtime;

/**
 * <p>
 * This implementation of the InputController works by delegating its input to
 * Command instances to be handled.  The Command classes are assumed to be in
 * a directory called 'command'.  The controller loads the classes from that
 * location and add them to its classloader.
 * <p/>
 * <p>
 * The controller then maps each Command instance to value Command.Descriptor.name.
 * When the controller receives an input line value, pulled from the context with
 * Context.KEY_INPUT_LINE, it parses and splits it. The controller then matches
 * the first command it finds that matches input line.startsWith() value.
 * </p>
 * <p>
 * <b>ClassLoading</p>
 * This controller will load the Command instances found in the 'command'
 * directory.  It will load any file with name ending in *.jar.  It will also
 * load any class files found in directory command/classes.
 * </p>
 *
 * @author vladimir.vivien
 */
public class CmdController implements InputController {

    private static Class<Command> COMMAND_TYPE      = Command.class;
    private static String         DEFAULT_NAMESPACE = "syscmd";
    private Map<String, Command> commands;
    private Boolean enabled = Boolean.TRUE;

    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input into token[N] tokens.  It uses token[0] as the action name mapped
     * to the Command.
     *
     * @param ctx the shell context.
     */
    @Override
    public boolean handle(Context ctx) {
        String[] cmdLine = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        boolean handled = false;

        // handle command line entry.  NOTE: value can be null
        if (cmdLine != null && cmdLine.length > 0) {
            if (commands != null && !commands.isEmpty()) {
                Command cmd = commands.get(cmdLine[0]);
                if (cmd != null) {
                    if (cmdLine.length > 1) {
                        String[] args = Arrays.copyOfRange(cmdLine, 1, cmdLine.length);
                        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, args);
                    } else {
                        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, new String[0]);
                    }

                    try {
                        cmd.execute(ctx);
                    } catch (Exception ex) {
                        ex.printStackTrace(ctx.getConsole().writer());
                        ctx.getConsole().printf(text("command.exec.error"), Arrays.asList(cmdLine),
                                ex.getMessage());
                    }
                } else {
                    ctx.getConsole().printf(text("command.not.found.error"), cmdLine[0]);
                }
                handled = true;
            }
        } else {
            ctx.getConsole().printf(text("command.not.found.error"), "");
        }

        return handled;
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean flag) {
        enabled = flag;
    }

    /**
     * Entry point for the plugin.  It builds class path from 'command' directory.
     * Then loads each Command found.
     */
    @Override
    public void plug(Context plug) {
        List<Command> commands = Runtime.loadServicePlugins(COMMAND_TYPE, plug.getClassLoader());

        if (commands == null || commands.isEmpty()) {
            plug.getConsole()
                .printf(text("controller.no.command.definition.error"), this.getClass().getName());
            System.exit(1);
        } else {
            plug.putValue(Context.KEY_COMMANDS, commands);
            this.commands = plug.mapCommands(commands);
            for (Command cmd : commands) {
                try {
                    cmd.plug(plug);
                } catch (Exception ex) {
                    plug.getConsole()
                        .printf(text("command.plug.error"), cmd.getClass(), ex.getMessage());
                }
            }
        }

    }

    @Override
    public void unplug(Context plug) {
        List<Command> cmds = plug.getCommands();
        for (Command cmd : cmds) {
            try {
                cmd.unplug(plug);
            } catch (Exception ex) {
                plug.getConsole()
                    .printf(text("command.unplug.error"), cmd.getClass(), ex.getMessage());
            }
        }

    }

}

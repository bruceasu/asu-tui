/*
 * Copyright 2014 ClamShell-Cli.
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

package me.asu.tui.framework.core.command;

import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * This class responds to the the "exit" command action
 *
 * @author vvivien
 */
public class ExitCmd implements CliCommand
{

    private static final String                NAMESPACE   = "syscmd";
    private static final String                ACTION_NAME = "exit";
    private static final CliCommand.Descriptor DESCRIPTOR  = new InnerDescriptor();

    @Override
    public Object execute(CliContext ctx, String[] args)
    {
        System.exit(0);
        return null;
    }

    @Override
    public void plug(CliContext plug)
    {
        // nothing to setup
    }

    @Override
    public void unplug(CliContext plug)
    {
        // nothing to tear down
    }

    @Override
    public CliCommand.Descriptor getDescriptor()
    {
        return DESCRIPTOR;
    }

    static class InnerDescriptor implements CliCommand.Descriptor
    {

        CliCmdLineParser parser = new CliCmdLineParser();

        @Override
        public String getNamespace()
        {
            return NAMESPACE;
        }

        @Override
        public String getName()
        {
            return ACTION_NAME;
        }

        @Override
        public String getDescription()
        {
            return "Type 'exit' to exits ClamShell.";
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }

    ;
}

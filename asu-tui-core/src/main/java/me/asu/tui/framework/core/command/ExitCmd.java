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

import java.util.Collections;
import java.util.Map;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;

/**
 * This class responds to the the "exit" command
 * action
 * @author vvivien
 */
public class ExitCmd implements CliCommand
{
    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "exit";

    @Override
    public Object execute(CliContext ctx, String[] args) {
        System.exit(0);
        return null;
    }

    @Override
    public void plug(CliContext plug) {
        // nothing to setup
    }
    
    @Override
    public void unplug(CliContext plug) {
        // nothing to tear down
    }
    
    @Override
    public CliCommand.Descriptor getDescriptor(){
        return new CliCommand.Descriptor() {
            @Override public String getNamespace() {return NAMESPACE;}
            
            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
               return "Exits ClamShell.";
            }

            @Override
            public String getUsage() {
                return "Type 'exit'";
            }

            @Override
            public Map<String, String> getArguments() {
                return Collections.emptyMap();
            }
        };
    }
    
}

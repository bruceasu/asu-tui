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
package me.asu.tui.framework.command;

import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.asu.tui.framework.api.*;

/**
 * This is a Command implementation that returns runtime system information.
 * The implemented command has the command-line format of:
 * <pre>
 * sysinfo [options] [option params]
 * </pre>
 *
 * <b>Options</b><br/>
 * <ul>
 * <li>-props: returns a list of all system properties</li>
 * </ul>
 *
 * @author vvivien
 */
public class SysInfoCmd implements Command {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME  = "sysinfo";
    private SysInfoDescriptor descriptor;

    @Override
    public Descriptor getDescriptor() {
        return (descriptor != null) ? descriptor : (descriptor = new SysInfoDescriptor());
    }

    @Override
    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IoConsole c = ctx.getConsole();
        if (args != null && args.length > 0 && accept(args[0])) {

            // decipher args

            // >sysinfo -props
            props(args[0], c);

            // >sysinfo -cp [or -classpath]
            classpath(args[0], c);

            // >sysinfo -mem
            mem(args[0], c);


        } else {
            String usage = descriptor.getUsage();
            c.printf("%s%n",usage);
            Map<String, String> arguments = descriptor.getArguments();
            if (arguments != null && arguments.size() > 0) {
                c.printf("%nOptions:");
                c.printf("%n--------");
                for (Map.Entry<String, String> entry : arguments.entrySet()) {
                    c.printf("%n%1$-10s\t%2$s", entry.getKey(), entry.getValue());
                }
            }
            c.printf("%n");
        }

        return null;
    }


    private boolean accept(String arg) {
        return descriptor.getArguments().keySet().contains(arg);
    }

    private void mem(String arg, IoConsole c) {
        if ("-mem".equals(arg)) {
            MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
            c.printf("%nHeap Memory Usage:%n");
            c.printf("\t-Initial: %d%n", bean.getHeapMemoryUsage().getInit());
            c.printf("\t-Max: %d%n", bean.getHeapMemoryUsage().getMax());
            c.printf("\t-Committed: %d%n", bean.getHeapMemoryUsage().getCommitted());
            c.printf("\t-Used: %d", bean.getHeapMemoryUsage().getUsed());
            c.printf("%n%n");
        }
    }

    private void classpath(String arg, IoConsole c) {
        if ("-cp".equals(arg)) {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            c.printf("%nClasspath: %s", bean.getClassPath());
            c.printf("%nBoot Classpath: %s%n%n", bean.getBootClassPath());
        }
    }

    private void props(String arg, IoConsole c) {
        if ("-props".equals(arg)) {
            c.printf("%nSystem Properties");
            c.printf("%n-----------------");
            for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
                displaySystemProperty(c, (String) entry.getKey());
            }
            c.printf("%n%n");
        }
    }

    private void displaySystemProperty(IoConsole c, String propName) {
        if (propName == null || propName.isEmpty()) {
            c.printf("%n Property name is missing. Provide a property name.%n%n");
            return;
        }
        String propVal = System.getProperty(propName);
        if (propVal != null) {
            c.printf("%n%1$30s %2$5s %3$s", propName, " ", propVal);
        }
    }

    @Override
    public void plug(Context plug) {
        //descriptor = new SysInfoDescriptor();
    }

    @Override
    public void unplug(Context plug) {
        // nothing to do
    }

    private class SysInfoDescriptor implements Command.Descriptor {

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getName() {
            return CMD_NAME;
        }

        @Override
        public String getDescription() {
            return "Displays current JVM runtime information.";
        }

        @Override
        public String getUsage() {
            StringBuilder result = new StringBuilder();
            result.append(Configurator.VALUE_LINE_SEP).append("sysinfo [options]")
                  .append(Configurator.VALUE_LINE_SEP);

            return result.toString();
        }

        @Override
        public Map<String, String> getArguments() {
            Map<String, String> result = new HashMap<String, String>();
            result.put("-props", "Displays the JVM's system properties.");
            result.put("-cp", "Displays JVM classpath information.");
            result.put("-mem", "Displays memory inforamtion about current JVM.");
            return result;
        }

    }

}

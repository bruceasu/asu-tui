package me.asu.tui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CliCmdLineParser {
    final List<CliCmdLineOption> options = new ArrayList<>();

    public void addOption(CliCmdLineOption... opts) {
        if (opts == null || opts.length == 0) {
            return;
        }

        options.addAll(Arrays.asList(opts));
    }

    public void remove(CliCmdLineOption opt) {
        options.remove(opt);
    }

    public CliArguments parse(String[] args) {
        CliArguments arguments = new CliArguments();
        arguments.setOptions(options.toArray(new CliCmdLineOption[0]));

        if (args != null && args.length > 0 && !options.isEmpty()) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                boolean found = false;
                for (int j = 0; j < options.size(); j++) {
                    CliCmdLineOption opt = options.get(j);
                    String shortName = opt.getShortName();
                    String longName = opt.getLongName();
                    boolean matchLongName =
                            longName != null && !longName.isEmpty() && arg.equals("--" + longName);
                    boolean matchShortName = shortName != null && !shortName.isEmpty() && arg.equals(
                            "-" + shortName);
                    if (matchShortName || matchLongName) {
                        found = true;
                        if (opt.isHasArg()) {
                            i++;
                            arguments.addParam(opt, args[i]);
                        } else {
                            arguments.addParam(opt, "true");
                        }

                        break;
                    }
                }
                if (!found) {
                    arguments.addPositionArg(args[i]);
                }
            }
        }
        return arguments;
    }

    public String usage(String program) {
        StringBuilder builder = new StringBuilder();
        if (program != null) {
            builder.append(program).append("\n");
        }

        if (!options.isEmpty()) {
            options.forEach(opt -> {
                String shortName = opt.getShortName();
                if (shortName != null && !shortName.trim().isEmpty()) {
                    builder.append("\t").append("-").append(shortName);

                }
                String longName = opt.getLongName();
                if (longName != null && !longName.trim().isEmpty()) {
                    builder.append("\t").append("--").append(longName);
                }
                if (opt.hasArg) {
                    builder.append("\t<arg>");
                }
                String desc = opt.getDescription();
                if (desc != null && !desc.isEmpty()) {
                    builder.append("\t").append(desc);
                }
                builder.append("\n");
            });

        }
        return builder.toString();
    }


}
package me.asu.tui;

import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class CliArguments {

    final static CliCmdLineOption UNKNOWN_OPT = CliCmdLineOption.builder().build();
    final List<String> positionals = new ArrayList<>();
    final Map<CliCmdLineOption, List<String>> params = new HashMap<>();
    final List<CliCmdLineOption> options = new ArrayList<>();

    public void setOptions(CliCmdLineOption... opts) {
        if (opts == null || opts.length == 0) {
            return;
        }
        options.addAll(Arrays.asList(opts));
    }

    public CliArguments addPositionArg(String arg) {
        positionals.add(arg);
        return this;
    }

    public CliArguments addParam(CliCmdLineOption opt, String value) {
        if (hasParam(opt)) {
            params.get(opt).add(value);
        } else {
            List<String> values = new ArrayList<>();
            values.add(value);
            params.put(opt, values);
        }
        return this;
    }

    private CliCmdLineOption getOption(String key) {
        if (key == null || key.trim().isEmpty()) {
            return UNKNOWN_OPT;
        }

        key = key.trim();
        for (int i = 0; i < options.size(); i++) {
            CliCmdLineOption opt = options.get(i);
            if (key.equals(opt.getShortName()) || key.equals(opt.getLongName())) {
                return opt;
            }
        }
        return UNKNOWN_OPT;
    }

    public String getParam(String key) {
        CliCmdLineOption opt = getOption(key);
        List<String> strings = params.get(opt);
        if (strings != null) {
            return strings.get(0);
        } else {
            return null;
        }
    }

    public String[] getParams(String key) {
        CliCmdLineOption opt = getOption(key);
        List<String> strings = params.get(opt);
        if (strings != null) {
            return strings.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public boolean hasParam(String key) {
        CliCmdLineOption opt = getOption(key);
        return params.containsKey(opt);
    }

    public boolean hasParam(CliCmdLineOption opt) {
        return params.containsKey(opt);
    }

    public boolean hasRemain() {
        return !positionals.isEmpty();
    }

}
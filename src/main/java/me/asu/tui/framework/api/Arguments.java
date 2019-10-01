package me.asu.tui.framework.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class Arguments {

    List<String> remain = new ArrayList<>();
    Map<String, String> params = new HashMap<>();

    public Arguments addRemain(String arg) {
        remain.add(arg);
        return this;
    }

    public Arguments setParam(String param, String value) {
        params.put(param, value);
        return this;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public boolean hasParam(String key) {
        return params.containsKey(key);
    }

    public boolean hasRemain() {
        return !remain.isEmpty();
    }

}
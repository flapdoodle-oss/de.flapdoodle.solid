package com.mitchellbosecke.pebble.extension;

import org.junit.jupiter.api.Disabled;

import java.util.HashMap;
import java.util.Map;

@Disabled
public class TestingExtension extends AbstractExtension {

    private InvocationCountingFunction invocationCountingFunction = new InvocationCountingFunction();

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("invocationCountingFunction", invocationCountingFunction);
        return functions;
    }

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("mapToString", new MapToStringFilter());
        filters.put("listToString", new ListToStringFilter());
        filters.put("arrayToString", new ArrayToStringFilter());
        return filters;
    }

    public InvocationCountingFunction getInvocationCountingFunction() {
        return invocationCountingFunction;
    }

}

package com.tenjava.entries.ewized.t3.module;

public class ModuleManager {
    private ModuleBuilder builder;

    public ModuleManager() {
        builder = new ModuleBuilder();
    }

    public ModuleBuilder builder() {
        return builder;
    }
}

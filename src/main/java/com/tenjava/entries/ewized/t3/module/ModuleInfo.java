package com.tenjava.entries.ewized.t3.module;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    /** Name */
    public String name();

    /** The listeners */
    public Class<? extends Listener>[] listeners() default {};
}

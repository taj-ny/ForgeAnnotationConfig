package com.github.taj_ny.forgeannotationconfig;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface ConfigCategory {
    /**
     * The name of this category. Trimmed value cannot be empty.
     */
    String name();
    /**
     * The comment of this category. Shown in a tooltip when the mouse hovers over the category button.
     */
    String comment() default "";
    /**
     * A language key for localization of config GUI entry names. If the same key is specified with .tooltip
     * appended to the end, that key will return a localized tooltip when the mouse hovers over the category button.
     */
    String languageKey() default "";

    /**
     * Whether to show the category element in the configuration GUI.
     */
    boolean showInGui() default true;

    /**
     * Whether all properties of this category require Minecraft to be restarted when changed.
     */
    boolean requiresMcRestart() default false;
    /**
     * Whether all properties of this category require the world to be restarted when changed.
     */
    boolean requiresWorldRestart() default false;
}
package com.github.taj_ny.forgeannotationconfig;

import com.github.taj_ny.forgeannotationconfig.validator.IValidator;
import net.minecraftforge.fml.client.config.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
    // Placeholder value.
    String UNSPECIFIED_VALIDATION_PATTERN = "$FAC.UNSPECIFIED-VALIDATION-PATTERN";

    /**
     * The name of this property. Trimmed value cannot be empty. Leave default to use the name
     * of the field.
     */
    String name() default "";
    /**
     * The comment of this category. Shown in a tooltip when the mouse hovers over the property label.
     */
    String comment() default "";
    /**
     * A language key for localization of config GUI entry names. If the same key is specified with .tooltip
     * appended to the end, that key will return a localized tooltip when the mouse hovers over the property label.
     */
    String languageKey() default "";

    /**
     * Whether this property requires the world to be restarted when changed.
     */
    boolean requireMcRestart() default false;
    /**
     * Whether this property requires Minecraft to be restarted when changed.
     */
    boolean requireWorldRestart() default false;

    /**
     * A regular expression for validating the value of a string. Applies to array/list elements too. Leave default
     * in order not to use a validation pattern.
     */
    String validationPattern() default UNSPECIFIED_VALIDATION_PATTERN;

    /**
     * Whether the input should be validated. Affects both {@link #validationPattern()} and {@link #validator()}.
     */
    boolean validateInput() default true;

    /**
     * If left default, the library will attempt to find a global validator.
     */
    Class<? extends IValidator> validator() default IValidator.class;

    /**
     * Whether this property should be shown in the GUI.
     */
    boolean showInGui() default true;

    /**
     * Whether to allow user to add and remove elements of this list property in the GUI.
     */
    boolean isListLengthFixed() default false;

    /**
     * The maximum length of this list property. Leave default for no maximum length.
     */
    int maxListLength() default -1;

    /**
     * The minimum value of this integer/double property.
     */
    double minValue() default Double.MIN_VALUE;
    /**
     * The maximum value of this integer/double property.
     */
    double maxValue() default Double.MAX_VALUE;

    /**
     * The only values of this string property that can be selected by the user.
     */
    String[] validValues() default {};

    /**
     * The element used for taking user input for non-list properties. The class must provide a constructor with the
     * following parameter types: {@link GuiConfig}, {@link GuiConfigEntries}, {@link IConfigElement}. Leave default
     * to let the library decide which entry to use.
     */
    Class<? extends GuiConfigEntries.IConfigEntry> configEntryClass() default GuiConfigEntries.IConfigEntry.class;
    /**
     * The element used for taking user input for list properties. The class must provide a constructor with the
     * following parameter types: {@link GuiEditArray}, {@link GuiEditArrayEntries}, {@link IConfigElement}, {@link
     * Object} Leave default to let the library decide which entry to use.
     */
    Class<? extends GuiEditArrayEntries.IArrayEntry> arrayEntryClass() default GuiEditArrayEntries.IArrayEntry.class;
}
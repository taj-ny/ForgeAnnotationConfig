package com.github.taj_ny.forgeannotationconfig.element;

import com.github.taj_ny.forgeannotationconfig.Config;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraftforge.fml.client.config.IConfigElement;

public abstract class ConfigElement implements IConfigElement {
    protected final Config config;

    @Getter
    private final String name;
    @Getter
    private final String languageKey;
    @Getter
    private final String comment;

    @Accessors(fluent = true)
    @Getter
    private final boolean requiresWorldRestart;
    @Accessors(fluent = true)
    @Getter
    private final boolean requiresMcRestart;

    @Accessors(fluent = true)
    @Getter
    private final boolean showInGui;

    public ConfigElement(Config config, String name, String languageKey, String comment,
                         boolean requiresWorldRestart, boolean requiresMcRestart, boolean showInGui) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        this.config = config;
        this.name = name;
        this.languageKey = languageKey;
        this.comment = comment;
        this.requiresWorldRestart = requiresWorldRestart;
        this.requiresMcRestart = requiresMcRestart;
        this.showInGui = showInGui;
    }
}

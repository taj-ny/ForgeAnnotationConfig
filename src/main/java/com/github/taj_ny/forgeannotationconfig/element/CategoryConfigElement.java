package com.github.taj_ny.forgeannotationconfig.element;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigCategory;
import lombok.Getter;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.regex.Pattern;

public class CategoryConfigElement extends ConfigElement {
    @Getter
    private final String qualifiedName;

    private final List<ConfigElement> childElements;

    public CategoryConfigElement(Config config, List<ConfigElement> childElements, ConfigCategory category) {
        super(config, category.name(), category.languageKey(), category.comment(),
                category.requiresWorldRestart(), category.requiresMcRestart(), category.showInGui());
        qualifiedName = category.name();
        this.childElements = childElements;
    }

    @Override
    public List<IConfigElement> getChildElements() {
        return (List) childElements;
    }

    @Override
    public ConfigGuiType getType() {
        return ConfigGuiType.CONFIG_CATEGORY;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() {
        return null;
    }

    @Override
    public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() {
        return null;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isListLengthFixed() {
        return false;
    }

    @Override
    public int getMaxListLength() {
        return 0;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    @Override
    public Object[] getDefaults() {
        return new Object[0];
    }

    @Override
    public void setToDefault() {

    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public Object[] getList() {
        return new Object[0];
    }

    @Override
    public void set(Object value) {

    }

    @Override
    public void set(Object[] aVal) {

    }

    @Override
    public String[] getValidValues() {
        return new String[0];
    }

    @Override
    public Object getMinValue() {
        return null;
    }

    @Override
    public Object getMaxValue() {
        return null;
    }

    @Override
    public Pattern getValidationPattern() {
        return null;
    }
}

package com.github.taj_ny.forgeannotationconfig.element.property;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigCategory;
import com.github.taj_ny.forgeannotationconfig.ConfigProperty;
import com.github.taj_ny.forgeannotationconfig.entry.DoubleEntry;
import com.github.taj_ny.forgeannotationconfig.entry.IntegerEntry;
import com.github.taj_ny.forgeannotationconfig.entry.StringEntry;
import com.github.taj_ny.forgeannotationconfig.typeadapter.TypeAdapter;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A property where the supported type is not an array.
 */
public class SingleConfigElement<T, S> extends PropertyConfigElement<T, S> {
    /**
     * Default GUI config entries for supported types.
     */
    private static final Map<Class<?>, Class<? extends GuiConfigEntries.IConfigEntry>> entries
            = new HashMap<Class<?>, Class<? extends GuiConfigEntries.IConfigEntry>>() {{
        put(Double.class, DoubleEntry.class);
        put(Integer.class, IntegerEntry.class);
        put(String.class, StringEntry.class);
    }};


    public SingleConfigElement(Config config, Field field, ConfigProperty fieldAnnotation,
                               ConfigCategory categoryAnnotation, Object instance, TypeAdapter typeAdapter,
                               Class<S> supportedType) {
        super(config, field, fieldAnnotation, categoryAnnotation, instance, typeAdapter, supportedType);

        if (configEntryClass == null && validValues.length == 0) {
            configEntryClass = entries.get(supportedType);
        }
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
    public Object[] getDefaults() {
        return new Object[0];
    }

    @Override
    public Object[] getList() {
        return new Object[0];
    }

    @Override
    public void set(Object[] aVal) {
        throw new UnsupportedOperationException();
    }
}

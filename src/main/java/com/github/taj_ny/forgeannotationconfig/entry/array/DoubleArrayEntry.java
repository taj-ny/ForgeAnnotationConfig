package com.github.taj_ny.forgeannotationconfig.entry.array;

import com.github.taj_ny.forgeannotationconfig.Utils;
import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class DoubleArrayEntry extends IntegerArrayEntry {
    public DoubleArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
                             IConfigElement configElement, Object value) {
        super(owningScreen, owningEntryList, configElement, Integer.parseInt(value.toString()));
    }

    @Override
    public Double getValue() {
        return Double.parseDouble(textFieldValue.getText());
    }
}

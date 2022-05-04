package com.github.taj_ny.forgeannotationconfig.entry.array;

import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class IntegerArrayEntry extends StringArrayEntry {
    public IntegerArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
                            IConfigElement configElement, Object value) {
        super(owningScreen, owningEntryList, configElement, Integer.parseInt(value.toString()));
    }

    @Override
    public Number getValue() {
        // Unlike StringEntry, this method is called by Forge during drawing and can't throw an exception.
        try {
            return Integer.parseInt(textFieldValue.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected boolean validate() {
        if (getValue() == null) {
            return false;
        }

        if (getValue().doubleValue() < property.getMinValue().doubleValue()
                || getValue().doubleValue() > property.getMaxValue().doubleValue()) {
            return false;
        }

        return super.validate();
    }
}

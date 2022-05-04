package com.github.taj_ny.forgeannotationconfig.entry;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class IntegerEntry extends StringEntry {
    public IntegerEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
        super(owningScreen, owningEntryList, configElement);
        validate();
    }

    @Override
    public void keyTyped(char eventChar, int eventKey) {
        super.keyTyped(eventChar, eventKey);
        if (enabled()) {
            validate();
        }
    }

    @Override
    public Number getCurrentValue() {
        return Integer.parseInt(textFieldValue.getText());
    }

    @Override
    protected boolean validate() {
        try {
            getCurrentValue();
        } catch (NumberFormatException e) {
            return false;
        }

        if (getCurrentValue().doubleValue() < property.getMinValue().doubleValue()
                || getCurrentValue().doubleValue() > property.getMaxValue().doubleValue()) {
            return false;
        }

        return super.validate();
    }
}

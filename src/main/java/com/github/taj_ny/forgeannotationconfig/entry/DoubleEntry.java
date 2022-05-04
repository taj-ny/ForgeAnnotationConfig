package com.github.taj_ny.forgeannotationconfig.entry;

import com.github.taj_ny.forgeannotationconfig.Utils;
import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class DoubleEntry extends IntegerEntry {
    public DoubleEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
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
    public Double getCurrentValue() {
        return Double.parseDouble(textFieldValue.getText());
    }
}

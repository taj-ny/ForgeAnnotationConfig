package com.github.taj_ny.forgeannotationconfig.entry.array;

import com.github.taj_ny.forgeannotationconfig.element.property.ArrayConfigElement;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class StringArrayEntry extends GuiEditArrayEntries.StringEntry {
    protected ArrayConfigElement property;

    public StringArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
                            IConfigElement configElement, Object value) {
        super(owningScreen, owningEntryList, configElement, value);
        property = (ArrayConfigElement) configElement;
        isValidated = isValidated || this.property.getValidator() != null;
        isValidValue = validate();
    }

    @Override
    public void keyTyped(char eventChar, int eventKey) {
        super.keyTyped(eventChar, eventKey);
        isValidValue = validate();
    }

    @Override
    public Object getValue() {
        return textFieldValue.getText();
    }

    protected boolean validate() {
        return property.tryValidateInput(textFieldValue);
    }
}

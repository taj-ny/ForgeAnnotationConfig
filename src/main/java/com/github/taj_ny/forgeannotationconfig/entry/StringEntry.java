package com.github.taj_ny.forgeannotationconfig.entry;

import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.lwjgl.input.Keyboard;

public class StringEntry extends GuiConfigEntries.StringEntry {
    protected final PropertyConfigElement property;

    public StringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
        super(owningScreen, owningEntryList, configElement);
        property = (PropertyConfigElement) configElement;
        isValidValue = validate();
    }

    @Override
    public void keyTyped(char eventChar, int eventKey) {
        if (enabled() || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END) {
            textFieldValue.textboxKeyTyped((enabled() ? eventChar : Keyboard.CHAR_NONE), eventKey);

            isValidValue = validate();
        }
    }

    protected boolean validate() {
        return property.tryValidateInput(textFieldValue);
    }

    @Override
    public boolean saveConfigElement() {
        if (enabled()) {
            if (isChanged() && isValidValue) {
                property.set(getCurrentValue());
                return property.requiresMcRestart();
            }
            else if (isChanged() && !isValidValue)
            {
                property.setToDefault();
                return property.requiresMcRestart()
                        && beforeValue != null ? beforeValue.equals(property.getDefault()) : property.getDefault() == null;
            }
        }
        return false;
    }
}

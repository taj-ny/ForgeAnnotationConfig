package com.github.taj_ny.forgeannotationconfig.validator;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigProperty;
import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;

/**
 * Validates user string input inside the GUI. Validators can be registered globally with
 * {@link Config#registerValidator(Class, IValidator)} or specified only for one field with
 * {@link ConfigProperty#validator()}.
 * <br><br>
 * Example:
 * <pre><code>class UUIDValidator implements IValidator {
 *    {@literal @}Override
 *     public boolean validate(PropertyConfigElement element, String value) {
 *         try {
 *             UUID.fromString(value);
 *             return true;
 *         } catch (IllegalArgumentException e) {
 *             return false;
 *         }
 *     }
 * }</code></pre>
 */
public interface IValidator {
    /**
     * Validates user input. Thrown exceptions will be caught and
     * @param value User input. If the default config entries for integers and doubles are used, this parameter will
     *              always have a valid number value.
     * @return Whether the value is valid.
     */
    boolean validate(PropertyConfigElement element, String value);
}

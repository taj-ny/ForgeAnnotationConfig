package com.github.taj_ny.forgeannotationconfig.typeadapter;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.util.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Converts a value from type A to B. Used for adding support for types other than the types supported by Forge. All
 * registered type adapters need to have a constructor that takes {@link Config} and {@link TypeToken} as parameters.
 * <br><br>
 * Example:
 * <pre><code>class UUIDTypeAdapter extends TypeAdapter{@code <UUID, String>} {
 *     public UUIDTypeAdapter(Config config, TypeToken type) {
 *         super(config, type, UUID.class, String.class);
 *     }
 *
 *    {@literal @}Override
 *     public UUID toA(String s) {
 *         return UUID.fromString(s);
 *     }
 *
 *    {@literal @}Override
 *     public String toB(UUID uuid) {
 *         return uuid.toString();
 *     }
 * }</code></pre>
 */
public abstract class TypeAdapter<A, B> {
    protected final Config config;
    protected final TypeToken type;

    @Getter
    protected final Class typeA;
    @Getter
    protected final Class typeB;

    public TypeAdapter(Config config, TypeToken type, Class typeA, Class typeB) {
        this.config = config;
        this.type = type;
        this.typeA = typeA;
        this.typeB = typeB;
    }

    @SneakyThrows
    public static TypeAdapter createInstance(Class<? extends TypeAdapter> clazz, Config config, TypeToken typeToken) {
        return clazz.getDeclaredConstructor(Config.class, TypeToken.class).newInstance(config, typeToken);
    }

    /**
     * Converts a value from type &lt;B&gt; to &lt;A&gt;. Type adapters for mutable types should never return the
     * same instance as the argument.
     */
    public abstract A toA(B b);

    /**
     * Converts a value from type &lt;A&gt; to &lt;B&gt;. Type adapters for mutable types should never return the
     * same instance as the argument.
     */
    public abstract B toB(A a);

    /**
     * @return A type that will be used to find the validator. For example, a list type adapter must return its
     * component type.
     */
    public Class getTypeForValidator() {
        return typeA;
    }
}
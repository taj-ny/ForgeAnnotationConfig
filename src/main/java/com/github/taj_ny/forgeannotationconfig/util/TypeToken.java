/*
Copyright 2008 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.taj_ny.forgeannotationconfig.util;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/*
 This library uses the TypeToken.getParameterized method, which is not available in the 2.2.4 version of gson provided
 by Forge. I was able to shadow the 2.8.0 version but even then, upon launching minecraft a NoSuchMethodException
 would be thrown. The only solution I could find was to decompile the entire class and include it in the project.
 */
public class TypeToken<T> {
    final Class<? super T> rawType;
    final Type type;
    final int hashCode;

    protected TypeToken() {
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    TypeToken(Type type) {
        this.type = Types.canonicalize((Type)Preconditions.checkNotNull(type));
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        } else {
            ParameterizedType parameterized = (ParameterizedType)superclass;
            return Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }
    }

    public final Class<? super T> getRawType() {
        return this.rawType;
    }

    public final Type getType() {
        return this.type;
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(Class<?> cls) {
        return this.isAssignableFrom((Type)cls);
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(Type from) {
        if (from == null) {
            return false;
        } else if (this.type.equals(from)) {
            return true;
        } else if (this.type instanceof Class) {
            return this.rawType.isAssignableFrom(Types.getRawType(from));
        } else if (this.type instanceof ParameterizedType) {
            return isAssignableFrom(from, (ParameterizedType)this.type, new HashMap());
        } else if (!(this.type instanceof GenericArrayType)) {
            throw buildUnexpectedTypeError(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
        } else {
            return this.rawType.isAssignableFrom(Types.getRawType(from)) && isAssignableFrom(from, (GenericArrayType)this.type);
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(TypeToken<?> token) {
        return this.isAssignableFrom(token.getType());
    }

    private static boolean isAssignableFrom(Type from, GenericArrayType to) {
        Type toGenericComponentType = to.getGenericComponentType();
        if (!(toGenericComponentType instanceof ParameterizedType)) {
            return true;
        } else {
            Type t = from;
            if (from instanceof GenericArrayType) {
                t = ((GenericArrayType)from).getGenericComponentType();
            } else if (from instanceof Class) {
                Class classType;
                for(classType = (Class)from; classType.isArray(); classType = classType.getComponentType()) {
                }

                t = classType;
            }

            return isAssignableFrom((Type)t, (ParameterizedType)toGenericComponentType, new HashMap());
        }
    }

    private static boolean isAssignableFrom(Type from, ParameterizedType to, Map<String, Type> typeVarMap) {
        if (from == null) {
            return false;
        } else if (to.equals(from)) {
            return true;
        } else {
            Class<?> clazz = Types.getRawType(from);
            ParameterizedType ptype = null;
            if (from instanceof ParameterizedType) {
                ptype = (ParameterizedType)from;
            }

            Type[] tArgs;
            int i;
            Type arg;
            if (ptype != null) {
                tArgs = ptype.getActualTypeArguments();
                TypeVariable<?>[] tParams = clazz.getTypeParameters();

                for(i = 0; i < tArgs.length; ++i) {
                    arg = tArgs[i];

                    TypeVariable var;
                    TypeVariable v;
                    for(var = tParams[i]; arg instanceof TypeVariable; arg = (Type)typeVarMap.get(v.getName())) {
                        v = (TypeVariable)arg;
                    }

                    typeVarMap.put(var.getName(), arg);
                }

                if (typeEquals(ptype, to, typeVarMap)) {
                    return true;
                }
            }

            tArgs = clazz.getGenericInterfaces();
            int var12 = tArgs.length;

            for(i = 0; i < var12; ++i) {
                arg = tArgs[i];
                if (isAssignableFrom(arg, to, new HashMap(typeVarMap))) {
                    return true;
                }
            }

            Type sType = clazz.getGenericSuperclass();
            return isAssignableFrom(sType, to, new HashMap(typeVarMap));
        }
    }

    private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map<String, Type> typeVarMap) {
        if (from.getRawType().equals(to.getRawType())) {
            Type[] fromArgs = from.getActualTypeArguments();
            Type[] toArgs = to.getActualTypeArguments();

            for(int i = 0; i < fromArgs.length; ++i) {
                if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static AssertionError buildUnexpectedTypeError(Type token, Class<?>... expected) {
        StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");
        Class[] var3 = expected;
        int var4 = expected.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Class<?> clazz = var3[var5];
            exceptionMessage.append(clazz.getName()).append(", ");
        }

        exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ").append(token.toString()).append('.');
        return new AssertionError(exceptionMessage.toString());
    }

    private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
        return to.equals(from) || from instanceof TypeVariable && to.equals(typeMap.get(((TypeVariable)from).getName()));
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final boolean equals(Object o) {
        return o instanceof TypeToken && Types.equals(this.type, ((TypeToken)o).type);
    }

    public final String toString() {
        return Types.typeToString(this.type);
    }

    public static TypeToken<?> get(Type type) {
        return new TypeToken(type);
    }

    public static <T> TypeToken<T> get(Class<T> type) {
        return new TypeToken(type);
    }

    public static TypeToken<?> getParameterized(Type rawType, Type... typeArguments) {
        return new TypeToken(Types.newParameterizedTypeWithOwner((Type)null, rawType, typeArguments));
    }

    public static TypeToken<?> getArray(Type componentType) {
        return new TypeToken(Types.arrayOf(componentType));
    }

    private static final class Preconditions {
        private Preconditions() {
            throw new UnsupportedOperationException();
        }

        public static <T> T checkNotNull(T obj) {
            if (obj == null) {
                throw new NullPointerException();
            } else {
                return obj;
            }
        }

        public static void checkArgument(boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException();
            }
        }
    }

    public static final class Types {
        static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

        private Types() {
            throw new UnsupportedOperationException();
        }

        public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type... typeArguments) {
            return new Types.ParameterizedTypeImpl(ownerType, rawType, typeArguments);
        }

        public static GenericArrayType arrayOf(Type componentType) {
            return new Types.GenericArrayTypeImpl(componentType);
        }

        public static WildcardType subtypeOf(Type bound) {
            return new Types.WildcardTypeImpl(new Type[]{bound}, EMPTY_TYPE_ARRAY);
        }

        public static WildcardType supertypeOf(Type bound) {
            return new Types.WildcardTypeImpl(new Type[]{Object.class}, new Type[]{bound});
        }

        public static Type canonicalize(Type type) {
            if (type instanceof Class) {
                Class<?> c = (Class)type;
                return (Type)(c.isArray() ? new Types.GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c);
            } else if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType)type;
                return new Types.ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
            } else if (type instanceof GenericArrayType) {
                GenericArrayType g = (GenericArrayType)type;
                return new Types.GenericArrayTypeImpl(g.getGenericComponentType());
            } else if (type instanceof WildcardType) {
                WildcardType w = (WildcardType)type;
                return new Types.WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
            } else {
                return type;
            }
        }

        public static Class<?> getRawType(Type type) {
            if (type instanceof Class) {
                return (Class)type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Type rawType = parameterizedType.getRawType();
                Preconditions.checkArgument(rawType instanceof Class);
                return (Class)rawType;
            } else if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType)type).getGenericComponentType();
                return Array.newInstance(getRawType(componentType), 0).getClass();
            } else if (type instanceof TypeVariable) {
                return Object.class;
            } else if (type instanceof WildcardType) {
                return getRawType(((WildcardType)type).getUpperBounds()[0]);
            } else {
                String className = type == null ? "null" : type.getClass().getName();
                throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
            }
        }

        static boolean equal(Object a, Object b) {
            return a == b || a != null && a.equals(b);
        }

        public static boolean equals(Type a, Type b) {
            if (a == b) {
                return true;
            } else if (a instanceof Class) {
                return a.equals(b);
            } else if (a instanceof ParameterizedType) {
                if (!(b instanceof ParameterizedType)) {
                    return false;
                } else {
                    ParameterizedType pa = (ParameterizedType)a;
                    ParameterizedType pb = (ParameterizedType)b;
                    return equal(pa.getOwnerType(), pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType()) && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
                }
            } else if (a instanceof GenericArrayType) {
                if (!(b instanceof GenericArrayType)) {
                    return false;
                } else {
                    GenericArrayType ga = (GenericArrayType)a;
                    GenericArrayType gb = (GenericArrayType)b;
                    return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
                }
            } else if (a instanceof WildcardType) {
                if (!(b instanceof WildcardType)) {
                    return false;
                } else {
                    WildcardType wa = (WildcardType)a;
                    WildcardType wb = (WildcardType)b;
                    return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());
                }
            } else if (a instanceof TypeVariable) {
                if (!(b instanceof TypeVariable)) {
                    return false;
                } else {
                    TypeVariable<?> va = (TypeVariable)a;
                    TypeVariable<?> vb = (TypeVariable)b;
                    return va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName());
                }
            } else {
                return false;
            }
        }

        static int hashCodeOrZero(Object o) {
            return o != null ? o.hashCode() : 0;
        }

        public static String typeToString(Type type) {
            return type instanceof Class ? ((Class)type).getName() : type.toString();
        }

        static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
            if (toResolve == rawType) {
                return context;
            } else {
                if (toResolve.isInterface()) {
                    Class<?>[] interfaces = rawType.getInterfaces();
                    int i = 0;

                    for(int length = interfaces.length; i < length; ++i) {
                        if (interfaces[i] == toResolve) {
                            return rawType.getGenericInterfaces()[i];
                        }

                        if (toResolve.isAssignableFrom(interfaces[i])) {
                            return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                        }
                    }
                }

                if (!rawType.isInterface()) {
                    while(rawType != Object.class) {
                        Class<?> rawSupertype = rawType.getSuperclass();
                        if (rawSupertype == toResolve) {
                            return rawType.getGenericSuperclass();
                        }

                        if (toResolve.isAssignableFrom(rawSupertype)) {
                            return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                        }

                        rawType = rawSupertype;
                    }
                }

                return toResolve;
            }
        }

        static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
            Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
            return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
        }

        public static Type getArrayComponentType(Type array) {
            return (Type)(array instanceof GenericArrayType ? ((GenericArrayType)array).getGenericComponentType() : ((Class)array).getComponentType());
        }

        public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
            Type collectionType = getSupertype(context, contextRawType, Collection.class);
            if (collectionType instanceof WildcardType) {
                collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
            }

            return (Type)(collectionType instanceof ParameterizedType ? ((ParameterizedType)collectionType).getActualTypeArguments()[0] : Object.class);
        }

        public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
            if (context == Properties.class) {
                return new Type[]{String.class, String.class};
            } else {
                Type mapType = getSupertype(context, contextRawType, Map.class);
                if (mapType instanceof ParameterizedType) {
                    ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
                    return mapParameterizedType.getActualTypeArguments();
                } else {
                    return new Type[]{Object.class, Object.class};
                }
            }
        }

        public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
            while(true) {
                if (toResolve instanceof TypeVariable) {
                    TypeVariable<?> typeVariable = (TypeVariable)toResolve;
                    toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
                    if (toResolve != typeVariable) {
                        continue;
                    }

                    return toResolve;
                }

                Type newOwnerType;
                if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
                    Class<?> original = (Class)toResolve;
                    Type componentType = original.getComponentType();
                    newOwnerType = resolve(context, contextRawType, componentType);
                    return (Type)(componentType == newOwnerType ? original : arrayOf(newOwnerType));
                }

                Type ownerType;
                if (toResolve instanceof GenericArrayType) {
                    GenericArrayType original = (GenericArrayType)toResolve;
                    ownerType = original.getGenericComponentType();
                    newOwnerType = resolve(context, contextRawType, ownerType);
                    return ownerType == newOwnerType ? original : arrayOf(newOwnerType);
                }

                if (toResolve instanceof ParameterizedType) {
                    ParameterizedType original = (ParameterizedType)toResolve;
                    ownerType = original.getOwnerType();
                    newOwnerType = resolve(context, contextRawType, ownerType);
                    boolean changed = newOwnerType != ownerType;
                    Type[] args = original.getActualTypeArguments();
                    int t = 0;

                    for(int length = args.length; t < length; ++t) {
                        Type resolvedTypeArgument = resolve(context, contextRawType, args[t]);
                        if (resolvedTypeArgument != args[t]) {
                            if (!changed) {
                                args = (Type[])args.clone();
                                changed = true;
                            }

                            args[t] = resolvedTypeArgument;
                        }
                    }

                    return changed ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
                }

                if (toResolve instanceof WildcardType) {
                    WildcardType original = (WildcardType)toResolve;
                    Type[] originalLowerBound = original.getLowerBounds();
                    Type[] originalUpperBound = original.getUpperBounds();
                    Type upperBound;
                    if (originalLowerBound.length == 1) {
                        upperBound = resolve(context, contextRawType, originalLowerBound[0]);
                        if (upperBound != originalLowerBound[0]) {
                            return supertypeOf(upperBound);
                        }
                    } else if (originalUpperBound.length == 1) {
                        upperBound = resolve(context, contextRawType, originalUpperBound[0]);
                        if (upperBound != originalUpperBound[0]) {
                            return subtypeOf(upperBound);
                        }
                    }

                    return original;
                }

                return toResolve;
            }
        }

        static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
            Class<?> declaredByRaw = declaringClassOf(unknown);
            if (declaredByRaw == null) {
                return unknown;
            } else {
                Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
                if (declaredBy instanceof ParameterizedType) {
                    int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
                    return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
                } else {
                    return unknown;
                }
            }
        }

        private static int indexOf(Object[] array, Object toFind) {
            for(int i = 0; i < array.length; ++i) {
                if (toFind.equals(array[i])) {
                    return i;
                }
            }

            throw new NoSuchElementException();
        }

        private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
            GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
            return genericDeclaration instanceof Class ? (Class)genericDeclaration : null;
        }

        static void checkNotPrimitive(Type type) {
            Preconditions.checkArgument(!(type instanceof Class) || !((Class)type).isPrimitive());
        }

        private static final class WildcardTypeImpl implements WildcardType, Serializable {
            private final Type upperBound;
            private final Type lowerBound;
            private static final long serialVersionUID = 0L;

            public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
                Preconditions.checkArgument(lowerBounds.length <= 1);
                Preconditions.checkArgument(upperBounds.length == 1);
                if (lowerBounds.length == 1) {
                    Preconditions.checkNotNull(lowerBounds[0]);
                    Types.checkNotPrimitive(lowerBounds[0]);
                    Preconditions.checkArgument(upperBounds[0] == Object.class);
                    this.lowerBound = Types.canonicalize(lowerBounds[0]);
                    this.upperBound = Object.class;
                } else {
                    Preconditions.checkNotNull(upperBounds[0]);
                    Types.checkNotPrimitive(upperBounds[0]);
                    this.lowerBound = null;
                    this.upperBound = Types.canonicalize(upperBounds[0]);
                }

            }

            public Type[] getUpperBounds() {
                return new Type[]{this.upperBound};
            }

            public Type[] getLowerBounds() {
                return this.lowerBound != null ? new Type[]{this.lowerBound} : Types.EMPTY_TYPE_ARRAY;
            }

            public boolean equals(Object other) {
                return other instanceof WildcardType && Types.equals(this, (WildcardType)other);
            }

            public int hashCode() {
                return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
            }

            public String toString() {
                if (this.lowerBound != null) {
                    return "? super " + Types.typeToString(this.lowerBound);
                } else {
                    return this.upperBound == Object.class ? "?" : "? extends " + Types.typeToString(this.upperBound);
                }
            }
        }

        private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
            private final Type componentType;
            private static final long serialVersionUID = 0L;

            public GenericArrayTypeImpl(Type componentType) {
                this.componentType = Types.canonicalize(componentType);
            }

            public Type getGenericComponentType() {
                return this.componentType;
            }

            public boolean equals(Object o) {
                return o instanceof GenericArrayType && Types.equals(this, (GenericArrayType)o);
            }

            public int hashCode() {
                return this.componentType.hashCode();
            }

            public String toString() {
                return Types.typeToString(this.componentType) + "[]";
            }
        }

        private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
            private final Type ownerType;
            private final Type rawType;
            private final Type[] typeArguments;
            private static final long serialVersionUID = 0L;

            public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
                if (rawType instanceof Class) {
                    Class<?> rawTypeAsClass = (Class)rawType;
                    boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null;
                    Preconditions.checkArgument(ownerType != null || isStaticOrTopLevelClass);
                }

                this.ownerType = ownerType == null ? null : Types.canonicalize(ownerType);
                this.rawType = Types.canonicalize(rawType);
                this.typeArguments = (Type[])typeArguments.clone();

                for(int t = 0; t < this.typeArguments.length; ++t) {
                    Preconditions.checkNotNull(this.typeArguments[t]);
                    Types.checkNotPrimitive(this.typeArguments[t]);
                    this.typeArguments[t] = Types.canonicalize(this.typeArguments[t]);
                }

            }

            public Type[] getActualTypeArguments() {
                return (Type[])this.typeArguments.clone();
            }

            public Type getRawType() {
                return this.rawType;
            }

            public Type getOwnerType() {
                return this.ownerType;
            }

            public boolean equals(Object other) {
                return other instanceof ParameterizedType && Types.equals(this, (ParameterizedType)other);
            }

            public int hashCode() {
                return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ Types.hashCodeOrZero(this.ownerType);
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
                stringBuilder.append(Types.typeToString(this.rawType));
                if (this.typeArguments.length == 0) {
                    return stringBuilder.toString();
                } else {
                    stringBuilder.append("<").append(Types.typeToString(this.typeArguments[0]));

                    for(int i = 1; i < this.typeArguments.length; ++i) {
                        stringBuilder.append(", ").append(Types.typeToString(this.typeArguments[i]));
                    }

                    return stringBuilder.append(">").toString();
                }
            }
        }
    }
}

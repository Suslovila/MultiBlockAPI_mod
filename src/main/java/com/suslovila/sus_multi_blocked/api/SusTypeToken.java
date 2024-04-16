package com.suslovila.sus_multi_blocked.api;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;


// taken from newer GSON libs, provides correct operations with generic types
public class SusTypeToken {

    public static TypeToken<?> getParameterized(Type rawType, Type... typeArguments) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(rawType);
        Objects.requireNonNull(typeArguments);

        // Perform basic validation here because this is the only public API where users
        // can create malformed parameterized types
        if (!(rawType instanceof Class)) {
            // See also https://bugs.openjdk.org/browse/JDK-8250659
            throw new IllegalArgumentException("rawType must be of type Class, but was " + rawType);
        }
        Class<?> rawClass = (Class<?>) rawType;
        TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();

        int expectedArgsCount = typeVariables.length;
        int actualArgsCount = typeArguments.length;
        if (actualArgsCount != expectedArgsCount) {
            throw new IllegalArgumentException(rawClass.getName() + " requires " + expectedArgsCount +
                    " type arguments, but got " + actualArgsCount);
        }

        for (int i = 0; i < expectedArgsCount; i++) {
            Type typeArgument = typeArguments[i];
            Class<?> rawTypeArgument = $Gson$Types.getRawType(typeArgument);
            TypeVariable<?> typeVariable = typeVariables[i];

            for (Type bound : typeVariable.getBounds()) {
                Class<?> rawBound = $Gson$Types.getRawType(bound);

                if (!rawBound.isAssignableFrom(rawTypeArgument)) {
                    throw new IllegalArgumentException("Type argument " + typeArgument + " does not satisfy bounds "
                            + "for type variable " + typeVariable + " declared by " + rawType);
                }
            }
        }

        // sorry for reflection, there was no other way :( , all the way was harder

        Constructor constructor = TypeToken.class.getDeclaredConstructor(Type.class);
        constructor.setAccessible(true);
        Object[] args = { $Gson$Types.newParameterizedTypeWithOwner(null, rawType, typeArguments) };
         return (TypeToken<?>) constructor.newInstance(args);
    }
}

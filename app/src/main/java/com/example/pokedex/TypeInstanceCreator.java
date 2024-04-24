package com.example.pokedex;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

public class TypeInstanceCreator implements InstanceCreator<Type> {

    @Override
    public Type createInstance(Type type) {
        // Aquí proporciona tu implementación para crear instancias de Type
        // Puedes devolver null si no necesitas crear instancias de esta interfaz
        return null;
    }
}
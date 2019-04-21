package com.gaufoo.bbs.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonUtils {
    public static Gson interfaceGson(Type type) {
        return new GsonBuilder().registerTypeAdapter(type, new GsonUtils.InterfaceAdapter()).create();
    }

    public static class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

        private static final String CLASSNAME = "CLASSNAME";
        private static final String DATA = "DATA";

        public T deserialize(JsonElement jsonElement, Type type,
                             JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
            String className = prim.getAsString();
            Class klass = getObjectClass(className);
            return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
        }

        public JsonElement serialize(T jsonElement, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
            jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
            return jsonObject;
        }

        /****** Helper method to get the className of the object to be deserialized *****/
        private Class getObjectClass(String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
        }
    }
}

package com.company;
import com.google.gson.*;
import java.lang.reflect.Type;

final class PieceTypeSerializer<T>
        implements JsonSerializer<T>, JsonDeserializer<T> {

    private PieceTypeSerializer() {
    }

    static <T> PieceTypeSerializer<T> pieceTypeSerializer() {
        return new PieceTypeSerializer<>();
    }

    @Override
    public JsonElement serialize(final T value, final Type type, final JsonSerializationContext context) {
        final Type targetType = value != null
                ? value.getClass() // `type` can be an interface so Gson would not even try to traverse the fields, just pick the implementation class 
                : type;            // if not, then delegate further
        JsonElement je = context.serialize(value, targetType);
        if(type.getTypeName() == "com.company.PieceType") {
            je.getAsJsonObject().addProperty("pieceType", targetType.getTypeName());
        }
        return je;
    }

    @Override
    public T deserialize(final JsonElement jsonElement, final Type typeOfT, final JsonDeserializationContext context) {
        if(jsonElement.isJsonObject()) {
            JsonObject jo = jsonElement.getAsJsonObject();
            Class c = Object.class;
            if(jo.has("pieceType")) {
                try {
                    c = Class.forName(jo.get("pieceType").getAsString());
                } catch (Exception e) {};
                return context.deserialize(jsonElement, c);
            }
        }
        return null;
    }

}
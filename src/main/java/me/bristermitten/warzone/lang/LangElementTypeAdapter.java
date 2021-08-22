package me.bristermitten.warzone.lang;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LangElementTypeAdapter extends TypeAdapter<LangElement> {
    @Override
    public void write(JsonWriter out, LangElement value) throws IOException {
        if (value.title() == null && value.subtitle() == null) {
            out.value(value.message());
            return;
        }
        out.beginObject();
        out.name("message").value(value.message());
        out.name("title").value(value.title());
        out.name("subtitle").value(value.subtitle());
    }

    @Override
    public LangElement read(JsonReader in) throws IOException {
        return switch (in.peek()) {
            case STRING -> new LangElement.LangElementBuilder().setMessage(in.nextString()).setTitle(null).setSubtitle(null).build();
            case BEGIN_OBJECT -> {
                var builder = new LangElement.LangElementBuilder();
                in.beginObject();

                while (in.hasNext()) {
                    final String s = in.nextName();
                    switch (s) {
                        case "message" -> builder.setMessage(in.nextString());
                        case "title" -> builder.setTitle(in.nextString());
                        case "subtitle" -> builder.setSubtitle(in.nextString());
                        default -> throw new IllegalArgumentException("Unexpected value in LangElement");
                    }
                }
                in.endObject();
                yield builder.build();
            }
            default -> throw new IllegalArgumentException("Invalid LangElement " + in.peek());
        };
    }
}

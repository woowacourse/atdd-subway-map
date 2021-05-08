package wooteco.subway.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonComponent
public class ErrorSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeStartArray();
        errors.getFieldErrors().forEach(error -> {
            try {
                gen.writeStartObject();
                fieldError(gen, error);
                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        errors.getGlobalErrors().forEach(error -> {
            try {
                gen.writeStartObject();
                globalError(gen, error);
                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gen.writeEndArray();
    }

    private void fieldError(JsonGenerator gen, FieldError error) throws IOException {
        gen.writeStringField("field", error.getField());
        globalError(gen, error);
    }

    private void globalError(JsonGenerator gen, ObjectError error) throws IOException {
        gen.writeStringField("objectName", error.getObjectName());
        gen.writeStringField("code", error.getCode());
        gen.writeStringField("defaultMessage", error.getDefaultMessage());
    }
}

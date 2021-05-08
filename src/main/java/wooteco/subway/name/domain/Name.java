package wooteco.subway.name.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = StationName.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StationName.class, name = "stationName"),
        @JsonSubTypes.Type(value = LineName.class, name = "lineName")
})
public interface Name {
    String name();

    boolean sameName(final String name);

    Name changeName(String name);
}

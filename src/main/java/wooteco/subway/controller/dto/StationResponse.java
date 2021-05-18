package wooteco.subway.controller.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wooteco.subway.domain.Station;

public class StationResponse {
    private final Long id;
    private final String name;

    public StationResponse(final Station station) {
        this(station.getId(), station.getName());
    }

    @JsonCreator
    public StationResponse(
            @JsonProperty(value = "id") Long id,
            @JsonProperty(value = "name") String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

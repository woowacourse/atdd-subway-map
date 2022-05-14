package wooteco.subway.ui.response;

import java.util.Objects;

import wooteco.subway.domain.Station;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public static StationResponse from(Station stationEntity) {
        return new StationResponse(stationEntity.getId(), stationEntity.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StationResponse that = (StationResponse)o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "StationResponse{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

package wooteco.subway.service.dto.station;

import wooteco.subway.domain.Station;

import java.util.Objects;

public class StationResponseDto {
    private Long id;
    private String name;

    public StationResponseDto() {
    }

    public StationResponseDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public StationResponseDto(Long id, String name) {
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
    public String toString() {
        return "StationResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponseDto that = (StationResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

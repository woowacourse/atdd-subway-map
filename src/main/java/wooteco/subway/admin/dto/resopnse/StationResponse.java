package wooteco.subway.admin.dto.resopnse;

import java.time.LocalDateTime;
import java.util.Objects;

import wooteco.subway.admin.domain.Station;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    private StationResponse() {
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedAt());
    }

    public StationResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StationResponse that = (StationResponse)o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

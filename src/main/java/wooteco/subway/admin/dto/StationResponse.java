package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedAt());
    }

    public static Set<StationResponse> listOf(Set<Station> stations) {
        return stations.stream().map(StationResponse::of).collect(Collectors.toSet());
    }

    public StationResponse() {
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
}

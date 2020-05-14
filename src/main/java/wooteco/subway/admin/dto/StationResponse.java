package wooteco.subway.admin.dto;

import java.time.LocalDateTime;

import wooteco.subway.admin.domain.Station;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    private StationResponse() {
    }

    private StationResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedAt());
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

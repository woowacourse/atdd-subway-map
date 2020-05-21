package wooteco.subway.admin.dto.service.response;

import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;

public class StationServiceResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public StationServiceResponse() {
    }

    private StationServiceResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static StationServiceResponse of(Station station) {
        return new StationServiceResponse(station.getId(), station.getName(), station.getCreatedAt());
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

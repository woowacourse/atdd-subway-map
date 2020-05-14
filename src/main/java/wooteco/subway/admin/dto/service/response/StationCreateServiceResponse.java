package wooteco.subway.admin.dto.service.response;

import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;

public class StationCreateServiceResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public StationCreateServiceResponse() {
    }

    public StationCreateServiceResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static StationCreateServiceResponse of(Station station) {
        return new StationCreateServiceResponse(station.getId(), station.getName(), station.getCreatedAt());
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

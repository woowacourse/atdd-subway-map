package wooteco.subway.dto;

import wooteco.subway.entity.StationEntity;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(StationEntity stationEntity) {
        this(stationEntity.getId(), stationEntity.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

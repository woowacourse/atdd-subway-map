package wooteco.subway.dto.request;

import wooteco.subway.entity.StationEntity;

public class StationRequest {

    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StationEntity toEntity() {
        return new StationEntity.Builder(name)
                .build();
    }
}

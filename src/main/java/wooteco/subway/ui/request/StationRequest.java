package wooteco.subway.ui.request;

import wooteco.subway.domain.StationEntity;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public StationEntity toEntity() {
        return new StationEntity(name);
    }

    public String getName() {
        return name;
    }
}

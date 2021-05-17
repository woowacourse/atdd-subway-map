package wooteco.subway.presentation.station.dto;

import java.beans.ConstructorProperties;

public class StationResponse {

    private final Long id;
    private final String name;

    @ConstructorProperties({"id", "name"})
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}

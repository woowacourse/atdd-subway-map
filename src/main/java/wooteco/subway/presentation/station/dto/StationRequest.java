package wooteco.subway.presentation.station.dto;

import wooteco.subway.presentation.valid.RightStringInput;

import java.beans.ConstructorProperties;

public class StationRequest {

    @RightStringInput
    private final String name;

    @ConstructorProperties({"name"})
    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

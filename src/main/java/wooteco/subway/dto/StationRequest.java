package wooteco.subway.dto;

import wooteco.subway.util.NullChecker;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        NullChecker.validateInputsNotNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

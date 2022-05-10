package wooteco.subway.dto;

import wooteco.subway.util.NullChecker;

public class LineEditRequest {
    private String name;
    private String color;

    public LineEditRequest() {
    }

    public LineEditRequest(String name, String color) {
        NullChecker.validateInputsNotNull(name, color);
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}

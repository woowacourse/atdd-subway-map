package wooteco.subway.dao.dto;

import wooteco.subway.dto.LineRequest;

public class LineUpdateDto {

    private final Long id;
    private final String name;
    private final String color;

    private LineUpdateDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineUpdateDto of(final Long id, final LineRequest lineRequest) {
        return new LineUpdateDto(id, lineRequest.getName(), lineRequest.getColor());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

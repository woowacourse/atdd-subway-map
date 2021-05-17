package wooteco.subway.controller.response;

import wooteco.subway.service.dto.LineDto;

public class LineCreateResponse {
    private Long id;
    private String color;
    private String name;

    public LineCreateResponse() {
    }

    public LineCreateResponse(LineDto lineDto) {
        this(lineDto.getId(), lineDto.getColor(), lineDto.getName());
    }

    public LineCreateResponse(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
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

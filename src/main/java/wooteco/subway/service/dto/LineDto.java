package wooteco.subway.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LineDto {

    private final String name;
    private final String color;

    public LineDto(String name, String color) {
        this.name = name;
        this.color = color;
    }
}

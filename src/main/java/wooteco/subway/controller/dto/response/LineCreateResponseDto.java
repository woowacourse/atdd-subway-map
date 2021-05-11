package wooteco.subway.controller.dto.response;

import java.util.List;
import wooteco.subway.controller.dto.SectionDto;

public class LineCreateResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<SectionDto> sectionDtos;

    public LineCreateResponseDto(Long id, String name, String color, List<SectionDto> sectionDtos) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionDtos = sectionDtos;
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

    public List<SectionDto> getSectionDtos() {
        return sectionDtos;
    }
}

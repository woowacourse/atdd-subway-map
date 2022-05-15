package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.ExceptionMessage;

public class Line {

    private final Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(Long id, String name, String color, List<Section> sections) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_LINE_NAME.getContent());
        }
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections(sections);
    }

    public Line(String name, String color, List<Section> sections) {
        this(null, name, color, sections);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Long> getSortedStationId() {
        return sections.getSortedStationId();
    }

    public void deleteSectionsByStationId(Long stationId) {
        sections.deleteSectionsByStationId(stationId);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
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

    public List<Section> getSections() {
        return sections.getValue();
    }
}

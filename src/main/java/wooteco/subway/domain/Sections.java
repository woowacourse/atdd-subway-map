package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        sections.stream()
                .filter(s -> s.isContainStation(section.getUpStationId(), section.getDownStationId()))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("구간 등록이 불가능합니다."));
        
        sections.add(section);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}

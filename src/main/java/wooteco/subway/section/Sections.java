package wooteco.subway.section;

import wooteco.subway.exception.InvalidAddSectionException;

import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section newSection) {
        // up or down 하나도 안 걸리는 경우
        validateConnected(newSection);
        sections.add(newSection);
    }

    private void validateConnected(Section newSection) {
        sections.stream()
                .filter(section -> isConnected(newSection, section))
                .findAny()
                .orElseThrow(InvalidAddSectionException::new);
    }

    private boolean isConnected(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) || section.isDownStation(newSection.getDownStationId());
    }
}

package wooteco.subway.domain;

import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        validateSection(section);
    }

    private void validateSection(final Section section) {
        if (hasUpStation(section) && hasDownStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 지하철 노선에 존재합니다.");
        }
        if (!hasUpStation(section) && !hasDownStation(section)) {
            throw new IllegalArgumentException("추가하려는 구간이 노선에 포함되어 있지 않습니다.");
        }
    }

    private boolean hasUpStation(final Section section) {
        return sections.stream()
                .map(Section::getUpStationId)
                .anyMatch(section::existStation);
    }

    private boolean hasDownStation(final Section section) {
        return sections.stream()
                .map(Section::getDownStationId)
                .anyMatch(section::existStation);
    }
}

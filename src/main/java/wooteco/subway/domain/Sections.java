package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(final Section section) {
        validateSection(section);

        Section existSection = getExistSection(section);
        if (existSection.isAddingEndSection(section)) {
            sections.add(section);
        }
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

    private Section getExistSection(Section section) {
        return sections.stream()
                .filter(exist -> exist.existStation(section.getUpStationId())
                        || exist.existStation(section.getDownStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 정보를 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}

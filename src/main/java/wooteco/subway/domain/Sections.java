package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(final Section section) {
        validateAddableSection(section);
    }

    private void validateAddableSection(final Section section) {
        if (hasNoConnectableSection(section)) {
            throw new IllegalArgumentException("연결 할 수 있는 상행역 또는 하행역이 없습니다.");
        }
        if (isAlreadyConnectedSection(section)) {
            throw new IllegalArgumentException("입력한 구간의 상행역과 하행역이 이미 모두 연결되어 있습니다.");
        }
    }

    private boolean hasNoConnectableSection(final Section section) {
        return sections.stream()
                .noneMatch(existingSection -> existingSection.isConnectedSection(section));
    }

    private boolean isAlreadyConnectedSection(final Section section) {
        return hasEqualUpStationWith(section) && hasEqualDownStationWith(section);
    }

    private boolean hasEqualUpStationWith(final Section section) {
        return sections.stream()
                .anyMatch(existingSection -> existingSection.equalsWithUpStation(section));
    }

    private boolean hasEqualDownStationWith(final Section section) {
        return sections.stream()
                .anyMatch(existingSection -> existingSection.equalsWithDownStation(section));
    }
}

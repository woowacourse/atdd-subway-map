package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        validateCanConnect(section);
        validateAlreadyConnected(section);
        validateSectionCanDividedWithSameUpStation(section);
        validateSectionCanDividedWithSameDownStation(section);
        sections.add(section);
    }

    private void validateCanConnect(Section section) {
        boolean canConnect = sections.stream()
            .anyMatch(section::canConnect);

        if (!canConnect) {
            throw new IllegalArgumentException("해당 구간은 연결 지점이 없습니다");
        }
    }

    private void validateAlreadyConnected(Section section) {
        boolean isExistUpStation = sections.stream()
            .anyMatch(section::isSameUpStation);
        boolean isExistDownStation = sections.stream()
            .anyMatch(section::isSameDownStation);

        if (isExistUpStation && isExistDownStation) {
            throw new IllegalArgumentException("해당 구간은 이미 이동 가능합니다.");
        }
    }

    private void validateSectionCanDividedWithSameUpStation(Section section) {
        Section sectionWithSameUpStation = sections.stream()
            .filter(section1 -> section1.isSameUpStation(section))
            .findFirst()
            .orElse(null);

        if (sectionWithSameUpStation == null) {
            return;
        }

        if (!sectionWithSameUpStation.canInsert(section)) {
            throw new IllegalArgumentException("해당 구간은 추가될 수 없습니다.");
        }

        sectionWithSameUpStation.changeDownStation(section);
    }

    private void validateSectionCanDividedWithSameDownStation(Section section) {
        Section sectionWithSameDownStation = sections.stream()
            .filter(section1 -> section1.isSameDownStation(section))
            .findFirst()
            .orElse(null);

        if (sectionWithSameDownStation == null) {
            return;
        }

        if (!sectionWithSameDownStation.canInsert(section)) {
            throw new IllegalArgumentException("해당 구간은 추가될 수 없습니다.");
        }

        sectionWithSameDownStation.changeUpStation(section);
    }

    public int size() {
        return sections.size();
    }
}

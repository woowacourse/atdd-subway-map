package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        validateSectionsSize();
    }

    private void validateSectionsSize() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException("sections는 크기가 0으로는 생성할 수 없습니다.");
        }
    }

    public Stations calculateSortedStations() {
        Section section = calculateFirstSection(findAnySection());
        return new Stations(createSortedStations(section));
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    private Section calculateFirstSection(final Section section) {
        if (!hasUpSection(section)) {
            return section;
        }
        return calculateFirstSection(calculateUpSection(section));
    }

    private boolean hasUpSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isUpSection);
    }

    private Section calculateUpSection(final Section section) {
        return sections.stream()
                .filter(section::isUpSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    private List<Station> createSortedStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        while (hasDownSection(section)) {
            stations.add(section.getDownStation());
            section = nextSection(section);
        }
        stations.add(section.getDownStation());
        return stations;
    }

    private boolean hasDownSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isDownSection);
    }

    private Section nextSection(final Section section) {
        return sections.stream()
                .filter(section::isDownSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    public void addSection(final Section section) {
        if (hasNotUpStationOrDownStation(section)) {
            throw new IllegalStateException("구간 추가는 기존의 상행역 하행역 중 하나를 포함해야합니다.");
        }
        if (existUpStationToDownStation(section)) {
            throw new IllegalStateException("이미 상행에서 하행으로 갈 수 있는 구간이 존재합니다.");
        }
    }

    private boolean hasNotUpStationOrDownStation(final Section section) {
        return sections.stream()
                .noneMatch(section::isUpSectionOrDownSection);
    }

    private boolean existUpStationToDownStation(final Section section) {
        return hasUpStation(section) && hasDownStation(section);
    }

    private boolean hasUpStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsUpStation);
    }

    private boolean hasDownStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsDownStation);
    }
}

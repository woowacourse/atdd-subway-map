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

    public List<Station> calculateSortedStations() {
        Section startSection = sections.stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));

        Section firstSection = calculateFirstSection(startSection);
        List<Station> stations = new ArrayList<>();
        stations.add(firstSection.getUpStation());

        while (sections.stream().anyMatch(firstSection::isDownSection)) {
            stations.add(firstSection.getDownStation());
            firstSection = nextSection(firstSection);
        }
        stations.add(firstSection.getDownStation());
        return stations;
    }

    private Section calculateFirstSection(final Section section) {
        if (sections.stream()
                .noneMatch(section::isUpSection)) {
            return section;
        }
        return calculateFirstSection(sections.stream()
                .filter(section::isUpSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다.")));
    }

    private Section nextSection(final Section section) {
        return sections.stream()
                .filter(section::isDownSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }
}

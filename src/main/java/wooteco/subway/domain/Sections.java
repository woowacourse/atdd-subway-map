package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private final List<SectionWithStation> sections;

    public Sections(List<SectionWithStation> sections) {
        this.sections = sections;
    }

    public List<Station> calculateStations() {
        final List<Station> stations = new ArrayList<>();
        final SectionWithStation firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());
        final SectionWithStation lastSection = executeToLastSection(stations, firstSection);
        stations.add(lastSection.getDownStation());
        return stations;
    }

    private SectionWithStation getFirstSection() {
        return sections.stream()
                .filter(section -> isFirstUpStation(section.getUpStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("모든 상행과 하행이 연결됩니다."));
    }

    private boolean isFirstUpStation(Station upStation) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(upStation));
    }

    private SectionWithStation executeToLastSection(List<Station> stations, SectionWithStation nowSection) {
        while (isAnyLink(nowSection.getDownStation())) {
            stations.add(nowSection.getDownStation());
            nowSection = getNextSection(nowSection);
        }
        return nowSection;
    }

    private boolean isAnyLink(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(downStation));
    }

    private SectionWithStation getNextSection(SectionWithStation nowSection) {
        return sections.stream()
                .filter(section -> nowSection.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 하행과 상행으로 연결되는 구간이 없습니다."));
    }
}

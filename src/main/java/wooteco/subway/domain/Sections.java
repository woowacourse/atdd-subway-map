package wooteco.subway.domain;

import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private List<SectionWithStation> sections;

    public Sections(List<SectionWithStation> sectionWithStations) {
        this.sections = sectionWithStations;
    }

    public boolean isLastStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(station));
    }

    public List<StationResponse> sortStations() {
        List<Station> stations = new ArrayList<>();
        SectionWithStation firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());

        Station station = firstSection.getDownStation();
        while (!isLastStation(station)) {
            stations.add(station);
            station = findNextSection(station).getDownStation();
        }

        stations.add(findLastSection().getDownStation());
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private SectionWithStation findLastSection() {
        return sections.stream()
                .filter(section -> isLastStation(section.getDownStation()))
                .findFirst()
                .orElse(sections.get(0));
    }

    private SectionWithStation findNextSection(Station station) {
        return sections.stream()
                .filter(sectionWithStation -> sectionWithStation.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    private SectionWithStation findFirstSection() {
        return sections.stream()
                .filter(section -> isFirstStation(section.getUpStation()))
                .findFirst()
                .orElse(sections.get(0));
    }

    public boolean isFirstStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(station));
    }

    public boolean isLessThanOneSection() {
        return sections.size() <= 1;
    }
}

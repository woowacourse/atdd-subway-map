package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wooteco.subway.domain.station.Station;

public class Sections {

    private final List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections orderSections(Station upStation, List<Section> sections) {
        List<Section> orderedSections = new LinkedList<>();
        while (orderedSections.size() < sections.size()) {
            Section section = findSectionByUpStation(upStation, sections);
            orderedSections.add(section);
            upStation = section.getDownStation();
        }
        return new Sections(orderedSections);
    }

    private static Section findSectionByUpStation(Station station, List<Section> sections) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(station))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("다음 구간 역을 찾을 수 없습니다."));
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>(List.of(sections.get(0).getUpStation()));
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }
}

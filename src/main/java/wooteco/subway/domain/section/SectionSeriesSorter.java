package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.UnexpectedException;

public class SectionSeriesSorter {
    public List<Section> sort(List<Section> sections) {
        if (sections.isEmpty()) {
            return sections;
        }
        final Station upStation = findUpTerminal(sections);
        return upToDown(sections, upStation);
    }

    private Station findUpTerminal(List<Section> sections) {
        final Map<Station, Station> stationGraph = getAsGraph(sections);
        return stationGraph.keySet()
            .stream()
            .filter(station -> !stationGraph.containsValue(station))
            .findAny()
            .orElseThrow(() -> new UnexpectedException("상행 종점을 찾을 수 없습니다."));
    }

    private Map<Station, Station> getAsGraph(List<Section> sections) {
        return sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Section::getDownStation));
    }

    private List<Section> upToDown(List<Section> sections, Station upTerminal) {
        List<Section> sorted = new ArrayList<>();

        Optional<Section> findSection = findSectionWithUpStation(sections, upTerminal);
        while (findSection.isPresent()) {
            final Section section = findSection.get();
            sorted.add(section);
            findSection = findSectionWithUpStation(sections, section.getDownStation());
        }
        return sorted;
    }

    private Optional<Section> findSectionWithUpStation(List<Section> sections, Station upStation) {
        return sections.stream()
            .filter(section -> section.isUpStationSame(upStation))
            .findAny();
    }

}

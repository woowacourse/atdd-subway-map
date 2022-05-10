package wooteco.subway.domain;

import wooteco.subway.utils.exception.NoTerminalStationException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Station> getStations() {
        Map<Station, Station> hash = sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStation,
                        Section::getDownStation,
                        (key, value) -> key,
                        HashMap::new));
        Section terminalSection = getTerminalUpStationSection();

        Station upStation = terminalSection.getUpStation();
        return getStations(hash, upStation);
    }

    private List<Station> getStations(Map<Station, Station> hash, Station upStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        while (hash.containsKey(upStation)){
            Station nextStation = hash.get(upStation);
            stations.add(nextStation);
            upStation = nextStation;
        }
        return stations;
    }

    private Section getTerminalUpStationSection() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new NoTerminalStationException("[ERROR] 종점이 없습니다."));
    }

    public boolean isDuplicateSection(Long upStationId, Long downStationId) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpDownStation(upStationId, downStationId));

    }

    public boolean isNonMatchStations(Long upStationId, Long downStationId) {
        return sections.stream()
                .noneMatch(section -> section.haveStationId(upStationId, downStationId));
    }

    public Section getSectionByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId().equals(upStationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("[ERROR] 상행 역을 찾을 수 없습니다."));
    }

    public Section getSectionByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId().equals(downStationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("[ERROR] 하행 역을 찾을 수 없습니다."));
    }
}

package wooteco.subway.domain;

import wooteco.subway.utils.exception.NoTerminalStationException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public Station getTerminalDownStation() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        Section terminalUpStation = sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new NoTerminalStationException("[ERROR] 종점이 없습니다."));
        return terminalUpStation.getDownStation();
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

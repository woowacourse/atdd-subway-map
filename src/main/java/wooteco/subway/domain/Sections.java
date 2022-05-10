package wooteco.subway.domain;

import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NoTerminalStationException;

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

    public void checkDuplicateSection(Long upStationId, Long downStationId) {
        sections.stream()
                .filter(section -> section.getDownStation().getId().equals(downStationId) &&
                        section.getUpStation().getId().equals(upStationId))
                .forEach(section -> {
                    throw new DuplicatedException("[ERROR] 이미 노선에 존재하는 구간입니다.");
                });
    }
}

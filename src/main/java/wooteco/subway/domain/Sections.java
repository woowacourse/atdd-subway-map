package wooteco.subway.domain;

import wooteco.subway.utils.exception.NoTerminalStationException;
import wooteco.subway.utils.exception.NotDeleteException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
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
        while (hash.containsKey(upStation)) {
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

    public List<Section> delete(Station station) {
        validateMinSize();
        List<Section> bucket = new LinkedList<>();
        sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .ifPresent(bucket::add);
        sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .ifPresent(bucket::add);

        validateNoneStation(bucket);
        return new LinkedList<>(bucket);

    }

    private void validateMinSize() {
        if (sections.size() == MINIMUM_SIZE) {
            throw new NotDeleteException("[ERROR] 구간이 한개있을때는 삭제할수 없습니다.");
        }
    }

    private void validateNoneStation(List<Section> bucket) {
        if (bucket.isEmpty()) {
            throw new NotFoundException("[ERROR] 구간들중에 해당 역을 찾을수 없습니다.");
        }
    }

    public Optional<Section> findTargetWithNotTerminal(Long upStationId, Long downStationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId().equals(upStationId) ||
                        section.getDownStation().getId().equals(downStationId))
                .findFirst();
    }
}

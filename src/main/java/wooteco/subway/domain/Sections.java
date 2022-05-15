package wooteco.subway.domain;

import wooteco.subway.utils.exception.EmptyException;
import wooteco.subway.utils.exception.NoTerminalStationException;
import wooteco.subway.utils.exception.NotDeleteException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final int BETWEEN_SECTION_COUNT = 2;
    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateEmptySections(sections);
        this.sections = new ArrayList<>(sections);
    }

    private void validateEmptySections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new EmptyException("[ERROR] 구간이 비었습니다.");
        }
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

    public Sections delete(Long lineId, Station station) {
        validateMinSize();
        long count = countMatchSection(station);
        validateNoneMatch(count);
        if (isBetweenSection(count)) {
            Section leftSection = sections.stream()
                    .filter(section -> section.isSameDownStation(station))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("[ERROR] 하행 역과 일치하는 해당 구간을 찾을수 없습니다."));
            Section rightSection = sections.stream()
                    .filter(section -> section.isSameUpStation(station))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("[ERROR] 상행 역과 일치하는 해당 구간을 찾을수 없습니다."));

            sections.add(Section.merge(lineId, leftSection, rightSection));
            sections.remove(leftSection);
            sections.remove(rightSection);
            return new Sections(sections);
        }
        deleteTerminalSection(station);

        return new Sections(sections);

    }

    private void deleteTerminalSection(Station station) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station) || section.isSameUpStation(station))
                .findFirst()
                .ifPresent(sections::remove);
    }

    private boolean isBetweenSection(long count) {
        return count == BETWEEN_SECTION_COUNT;
    }

    private long countMatchSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station) || section.isSameDownStation(station))
                .count();
    }

    private void validateNoneMatch(long count) {
        if (count == 0) {
            throw new NotFoundException("[ERROR] 구간들중에 해당 역을 찾을수 없습니다.");
        }
    }

    private void validateMinSize() {
        if (sections.size() == MINIMUM_SIZE) {
            throw new NotDeleteException("[ERROR] 구간이 한개있을때는 삭제할수 없습니다.");
        }
    }

//    private void validateNoneStation(List<Section> bucket) {
//        if (bucket.isEmpty()) {
//            throw new NotFoundException("[ERROR] 구간들중에 해당 역을 찾을수 없습니다.");
//        }
//    }

    public Optional<Section> findTargetWithNotTerminal(Station upStation, Station downStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation) || section.isSameDownStation(downStation))
                .findFirst();
    }

    public List<Section> getSections() {
        return sections;

    }
}

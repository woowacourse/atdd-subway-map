package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import wooteco.subway.exception.DuplicateSectionException;
import wooteco.subway.exception.InvalidSectionCreateRequestException;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = new ArrayList<>(values);
    }

    public List<Station> getSortedStations() {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Station, Station> goesDownStations = new HashMap<>();
        Map<Station, Station> goesUpStations = new HashMap<>();
        fillStations(goesDownStations, goesUpStations);

        return Collections.unmodifiableList(sortStations(goesDownStations, goesUpStations));
    }

    private LinkedList<Station> sortStations(Map<Station, Station> goesDownStations,
                                             Map<Station, Station> goesUpStations) {
        LinkedList<Station> stations = new LinkedList<>();
        Station station = values.get(0).getUpStation();
        stations.add(station);

        addGoesDownStations(goesDownStations, stations, station);
        addGoesUpStations(goesUpStations, stations, station);
        return stations;
    }

    private void addGoesDownStations(Map<Station, Station> goesDownStations, LinkedList<Station> stations,
                                     Station station) {
        Station tempStation = station;
        while (goesDownStations.containsKey(tempStation)) {
            tempStation = goesDownStations.get(tempStation);
            stations.add(tempStation);
        }
    }

    private void addGoesUpStations(Map<Station, Station> goesUpStations, LinkedList<Station> stations,
                                   Station station) {
        Station tempStation;
        tempStation = station;
        while (goesUpStations.containsKey(tempStation)) {
            tempStation = goesUpStations.get(tempStation);
            stations.addFirst(tempStation);
        }
    }

    private void fillStations(Map<Station, Station> goesDownStations, Map<Station, Station> goesUpStations) {
        for (Section section : values) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            goesDownStations.put(upStation, downStation);
            goesUpStations.put(downStation, upStation);
        }
    }

    public void addSection(Section newSection) {
        validateDuplicate(newSection);
        if (canBeStartSection(newSection) || canBeEndSection(newSection)) {
            values.add(newSection);
            return;
        }
        Section includingSection = findIncludingSection(newSection);
        cutSection(includingSection, newSection);
    }

    private void validateDuplicate(Section newSection) {
        if (values.stream()
                .anyMatch(savedSection -> savedSection.hasSameStations(newSection))) {
            throw new DuplicateSectionException("이미 존재하는 구간입니다.");
        }
    }

    private boolean canBeStartSection(Section newSection) {
        return values.stream()
                .anyMatch(savedSection -> savedSection.getUpStation().equals(newSection.getDownStation()))
                && values.stream()
                .noneMatch(savedSection -> savedSection.getDownStation().equals(newSection.getDownStation()));
    }

    private boolean canBeEndSection(Section newSection) {
        return values.stream()
                .anyMatch(savedSection -> savedSection.getDownStation().equals(newSection.getUpStation()))
                && values.stream()
                .noneMatch(savedSection -> savedSection.getUpStation().equals(newSection.getUpStation()));
    }

    private Section findIncludingSection(Section section) {
        return values.stream()
                .filter(savedSection -> savedSection.getUpStation().equals(section.getUpStation())
                        || savedSection.getDownStation().equals(section.getDownStation()))
                .findAny()
                .orElseThrow(
                        () -> new InvalidSectionCreateRequestException("구간 시작 역과 끝 역이 모두 노선에 존재하지 않아 추가할 수 없습니다."));
    }

    private void cutSection(Section includingSection, Section newSection) {
        validateDistance(includingSection, newSection);
        if (includingSection.hasSameUpStation(newSection)) {
            cutUpSection(includingSection, newSection);
            return;
        }
        if (includingSection.hasSameDownStation(newSection)) {
            cutDownSection(includingSection, newSection);
        }
    }

    private void validateDistance(Section includingSection, Section newSection) {
        if (!includingSection.canInclude(newSection)) {
            throw new InvalidSectionCreateRequestException("새로 추가하려는 중간 구간은 해당 구간을 포함하는 기존 구간보다 길거나 같은 길이일 수 없습니다.");
        }
    }

    private void cutUpSection(Section includingSection, Section newSection) {
        Section anotherNewSection = new Section(newSection.getDownStation(), includingSection.getDownStation(),
                includingSection.getDistance() - newSection.getDistance());
        values.remove(includingSection);
        values.add(newSection);
        values.add(anotherNewSection);
    }

    private void cutDownSection(Section includingSection, Section newSection) {
        Section anotherNewSection = new Section(includingSection.getUpStation(), newSection.getUpStation(),
                includingSection.getDistance() - newSection.getDistance());
        values.remove(includingSection);
        values.add(newSection);
        values.add(anotherNewSection);
    }

    public List<Section> getValues() {
        return values;
    }

}

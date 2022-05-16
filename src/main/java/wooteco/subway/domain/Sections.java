package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.exception.duplicate.DuplicateSectionException;
import wooteco.subway.exception.invalidrequest.InvalidSectionCreateRequestException;
import wooteco.subway.exception.notfound.SectionNotFoundException;

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
                .anyMatch(savedSection -> savedSection.getUpStation().hasSameName(newSection.getDownStation()))
                && values.stream()
                .noneMatch(savedSection -> savedSection.getDownStation().hasSameName(newSection.getDownStation()));
    }

    private boolean canBeEndSection(Section newSection) {
        return values.stream()
                .anyMatch(savedSection -> savedSection.getDownStation().hasSameName(newSection.getUpStation()))
                && values.stream()
                .noneMatch(savedSection -> savedSection.getUpStation().hasSameName(newSection.getUpStation()));
    }

    private Section findIncludingSection(Section section) {
        return values.stream()
                .filter(savedSection -> savedSection.getUpStation().hasSameName(section.getUpStation())
                        || savedSection.getDownStation().hasSameName(section.getDownStation()))
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
        cutSection(includingSection, newSection, anotherNewSection);
    }

    private void cutDownSection(Section includingSection, Section newSection) {
        Section anotherNewSection = new Section(includingSection.getUpStation(), newSection.getUpStation(),
                includingSection.getDistance() - newSection.getDistance());
        cutSection(includingSection, newSection, anotherNewSection);
    }

    private void cutSection(Section includingSection, Section newSection, Section anotherNewSection) {
        values.remove(includingSection);
        values.add(newSection);
        values.add(anotherNewSection);
    }

    public void removeSectionByStation(Station station) {
        validateContainsTargetStation(station);
        if (isEndSection(station)) {
            removeEndSection(station);
            return;
        }
        removeMiddleSection(station);
    }

    private boolean isEndSection(Station station) {
        List<Station> sortedStations = getSortedStations();
        int index = sortedStations.indexOf(station);
        return index == 0 || index == sortedStations.size() - 1;
    }

    private void validateContainsTargetStation(Station station) {
        if (values.stream()
                .noneMatch(section -> section.containStation(station))) {
            throw new SectionNotFoundException("요청하는 역을 포함하는 구간이 없습니다.");
        }
    }

    private void removeEndSection(Station station) {
        Section targetSection = values.stream()
                .filter(savedSection -> savedSection.containStation(station))
                .findFirst()
                .orElseThrow(() -> new SectionNotFoundException("요청하는 역을 포함하는 구간이 없습니다."));
        values.remove(targetSection);
    }

    private void removeMiddleSection(Station station) {
        Section upSection = findUpSection(station);
        Section downSection = findDownSection(station);
        Section newSection = new Section(upSection.getUpStation(), downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance());
        values.remove(upSection);
        values.remove(downSection);
        values.add(newSection);
    }

    private Section findUpSection(Station station) {
        return values.stream()
                .filter(savedSection -> savedSection.getDownStation().hasSameName(station))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("요청하는 역을 포함하는 구간이 없습니다."));
    }

    private Section findDownSection(Station station) {
        return values.stream()
                .filter(savedSection -> savedSection.getUpStation().hasSameName(station))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("요청하는 역을 포함하는 구간이 없습니다."));
    }

    public List<Section> getNotContainSections(Sections sections) {
        return values.stream()
                .filter(sections::doesNotContain)
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean doesNotContain(Section target) {
        return values.stream()
                .noneMatch(section -> section.containStation(target.getUpStation()) && section.containStation(
                        target.getDownStation()));
    }

    public List<Section> getValues() {
        return values;
    }

}

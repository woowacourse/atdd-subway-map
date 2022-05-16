package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.NoSuchStationException;

public class Sections {
    private final Set<Section> values = new HashSet<>();

    public void add(final Section section) {
        if (isFirstAdd()) {
            values.add(section);
            return;
        }
        addSection(section);
    }

    public Long removeStation(final Station station) {
        checkRemovable(station);
        if (isLastStation(station)) {
            return removeLastSection(station);
        }
        return removeInterStation(station);
    }

    public List<Station> sortedStations() {
        Map<Station, Station> map = mappingSectionsToTable();
        Station topStation = findTopStation();
        return calculateSortedStations(map, topStation);
    }

    public Set<Section> values() {
        return Collections.unmodifiableSet(values);
    }

    private List<Station> calculateSortedStations(final Map<Station, Station> map, final Station topStation) {
        List<Station> result = new ArrayList<>();
        result.add(topStation);
        Station nextStation = map.get(topStation);
        while (nextStation != null) {
            result.add(nextStation);
            nextStation = map.get(nextStation);
        }
        return result;
    }

    private Map<Station, Station> mappingSectionsToTable() {
        Map<Station, Station> map = new HashMap<>();
        for (final Section section : values) {
            map.put(section.getUpStation(), section.getDownStation());
        }
        return map;
    }

    private void addSection(final Section section) {
        checkAddable(section);
        rearrangeSectionIfNeed(section);
        values.add(section);
    }

    private void checkRemovable(final Station station) {
        if (values.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 경우 역을 제거할 수 없습니다.");
        }
        if (!stations().contains(station)) {
            throw new IllegalArgumentException("해당 역이 존재하지 않습니다.");
        }
    }

    private void rearrangeSectionIfNeed(final Section section) {
        for (Section each : values) {
            rearrangeSectionIfForkRoadCase(each, section);
        }
    }

    private void checkAddable(final Section section) {
        if (containsSameSection(section) || isNotAddable(section)) {
            throw new IllegalArgumentException("연결 가능한 구간이 아닙니다.");
        }
    }

    private Long removeInterStation(final Station station) {
        Section targetToUpdate = findTargetToUpdate(station);
        Section targetToRemove = findTargetToRemove(station);
        updateAndRemove(targetToUpdate, targetToRemove);
        return targetToRemove.getId();
    }

    private Section findTargetToUpdate(final Station station) {
        return values.stream()
                .filter(it -> it.isDownStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchStationException(station.getId()));
    }

    private Section findTargetToRemove(final Station station) {
        return values.stream()
                .filter(it -> it.isUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchStationException(station.getId()));
    }

    private void updateAndRemove(final Section targetToUpdate, final Section targetToRemove) {
        Station downStation = targetToRemove.getDownStation();
        targetToUpdate.changeDownStation(downStation);
        targetToUpdate.changeDistance(targetToUpdate.getDistance() + targetToRemove.getDistance());
        values.remove(targetToRemove);
    }

    private boolean isLastStation(final Station station) {
        return lastStations().contains(station);
    }

    private Long removeLastSection(final Station station) {
        Section lastSection = values.stream()
                .filter(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        values.remove(lastSection);
        return lastSection.getId();
    }

    private void rearrangeSectionIfForkRoadCase(final Section existed, final Section added) {
        if (isUpsideForkRoadCase(existed, added)) {
            checkValidDistance(existed, added);
            rearrangeUpside(existed, added);
        }
        if (isDownsideForkRoadCase(existed, added)) {
            checkValidDistance(existed, added);
            rearrangeDownside(existed, added);
        }
    }

    private void checkValidDistance(final Section existed, final Section added) {
        if (existed.getDistance() <= added.getDistance()) {
            throw new IllegalArgumentException("구간의 길이가 올바르지 않습니다.");
        }
    }

    private boolean isDownsideForkRoadCase(final Section existed, final Section added) {
        return existed.getDownStation().equals(added.getDownStation());
    }

    private boolean isUpsideForkRoadCase(final Section existed, final Section added) {
        return added.getUpStation().equals(existed.getUpStation());
    }

    private void rearrangeUpside(final Section existed, final Section added) {
        Station currentDownStation = existed.getDownStation();
        int currentDistance = existed.getDistance();

        existed.changeDownStation(added.getDownStation());
        existed.changeDistance(added.getDistance());

        added.changeUpStation(added.getDownStation());
        added.changeDownStation(currentDownStation);
        added.changeDistance(currentDistance - added.getDistance());
    }

    private void rearrangeDownside(final Section existed, final Section added) {
        existed.changeDownStation(added.getUpStation());
        existed.changeDistance(existed.getDistance() - added.getDistance());
    }

    private boolean isFirstAdd() {
        return values.size() == 0;
    }

    private boolean isNotAddable(Section section) {
        return stations().contains(section.getUpStation()) == stations().contains(section.getDownStation());
    }

    private boolean containsSameSection(final Section section) {
        for (Section each : values) {
            if (each.containsSameStations(section)) {
                return true;
            }
        }
        return false;
    }

    private Set<Station> stations() {
        Set<Station> upStations = upStations();
        Set<Station> downStations = downStations();
        upStations.addAll(downStations);
        return upStations;
    }

    private Station findTopStation() {
        ArrayList<Station> topOrLowest = new ArrayList<>(lastStations());
        if (upStations().contains(topOrLowest.get(0))) {
            return topOrLowest.get(0);
        }
        return topOrLowest.get(1);
    }

    private Set<Station> upStations() {
        return values.stream().map(Section::getUpStation).collect(Collectors.toSet());
    }

    private Set<Station> downStations() {
        return values.stream().map(Section::getDownStation).collect(Collectors.toSet());
    }

    private Set<Station> lastStations() {
        // upStation과 downStation의 교집합을 전체 역에서 차집합한다.
        Set<Station> stations = stations();
        Set<Station> upStations = upStations();
        Set<Station> downStations = downStations();
        upStations.retainAll(downStations);
        stations.removeAll(upStations);
        return stations;
    }
}

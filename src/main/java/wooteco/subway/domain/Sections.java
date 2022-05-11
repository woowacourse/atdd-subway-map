package wooteco.subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {
    private final Set<Section> values = new HashSet<>();

    public void add(final Section section) {
        if (isFirstAdd()) {
            values.add(section);
            return;
        }

        if (containsSameSection(section) || isNotAddable(section)) {
            throw new IllegalArgumentException("연결 가능한 구간이 아닙니다.");
        }

        for (Section each : values) {
            rearrangeSectionIfForkRoadCase(each, section);
        }

        values.add(section);
    }

    public void removeStation(final Station station) {

        if (values.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 경우 역을 제거할 수 없습니다.");
        }
        if (!allStations().contains(station)) {
            throw new IllegalArgumentException("해당 역이 존재하지 않습니다.");
        }
        if (isLastStation(station)) {
            removeLastSection(station);
            return;
        }
        removeInterStation(station);

    }

    public Set<Section> values() {
        return Collections.unmodifiableSet(values);
    }

    private void removeInterStation(final Station station) {
        Section targetToUpdate = null;
        Section targetToRemove = null;
        Station downStationCandidate = null;

        List<Section> sectionsThatContainsStation = values.stream()
                .filter(it -> it.contains(station))
                .collect(Collectors.toList());

        for (Section section : sectionsThatContainsStation) {
            if (section.getDownStation().equals(station)) {
                targetToUpdate = section;
            }
            if (section.getUpStation().equals(station)) {
                targetToRemove = section;
                downStationCandidate = section.getDownStation();
            }
        }
        targetToUpdate.changeDownStation(downStationCandidate);
        targetToUpdate.changeDistance(targetToUpdate.getDistance() + targetToRemove.getDistance());
        values.remove(targetToRemove);

    }

    private boolean isLastStation(final Station station) {
        return lastStations().contains(station);
    }

    private void removeLastSection(final Station station) {
        Section lastSection = values.stream()
                .filter(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        values.remove(lastSection);
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
        return allStations().contains(section.getUpStation()) == allStations().contains(section.getDownStation());
    }

    private boolean containsSameSection(final Section section) {
        for (Section each : values) {
            if (each.containsSameStations(section)) {
                return true;
            }
        }
        return false;
    }

    private Set<Station> allStations() {
        Set<Station> upStations = upStations();
        Set<Station> downStations = downStations();
        upStations.addAll(downStations);
        return upStations;
    }

    private Set<Station> upStations() {
        return values.stream().map(Section::getUpStation).collect(Collectors.toSet());
    }

    private Set<Station> downStations() {
        return values.stream().map(Section::getDownStation).collect(Collectors.toSet());
    }

    private Set<Station> lastStations() {
        // upStation과 downStation의 교집합을 전체 역에서 차집합한다.
        Set<Station> stations = allStations();
        Set<Station> upStations = upStations();
        Set<Station> downStations = downStations();
        upStations.retainAll(downStations);
        stations.removeAll(upStations);
        return stations;
    }
}

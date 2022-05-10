package wooteco.subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;
    private final Set<Station> stations = new HashSet<>();
    private final Set<Section> sections = new HashSet<>();

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line(final long id, final String name, final String color, final Section section) {
        this.id = id;
        this.name = name;
        this.color = color;
        sections.add(section);
        stations.addAll(Set.of(section.getUpStation(), section.getDownStation()));
    }

    public void update(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    // TODO: 2022/05/11 별도의 클래스로 분리할 수 있을까?
    public void addSection(final Section section) {

        if (stations.size() == 0) {
            sections.add(section);
            stations.addAll(Set.of(section.getUpStation(), section.getDownStation()));
            return;
        }

        if (containsBothStationsIn(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 등록되어 있습니다.");
        }
        if (containsNeitherStationsIn(section)) {
            throw new IllegalArgumentException("상행역과 하행역 모두 노선에 등록되어 있지 않습니다.");
        }

        for (Section each : sections) {
            rearrangeSectionIfForkRoadCase(each, section);
        }

        sections.add(section);
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
    }

    public void removeStation(final Station station) {
        checkRemovable(station);

        List<Section> sectionsThatContainsStation = sections.stream()
                .filter(it -> it.contains(station))
                .collect(Collectors.toList());

        if (isLastStation(sectionsThatContainsStation)) {
            removeLastStation(station, sectionsThatContainsStation);
            return;
        }
        removeInterStation(station, sectionsThatContainsStation);
    }

    private void removeInterStation(final Station station, final List<Section> sectionsThatContainsStation) {
        Section targetToUpdate = null;
        Section targetToRemove = null;
        Station downStationCandidate = null;
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
        stations.remove(station);
        sections.remove(targetToRemove);
    }

    private boolean isLastStation(final List<Section> sectionsThatContainsStation) {
        return sectionsThatContainsStation.size() == 1;
    }

    private void removeLastStation(final Station station, final List<Section> sectionsThatContainsStation) {
        stations.remove(station);
        sections.remove(sectionsThatContainsStation.get(0));
    }

    private void checkRemovable(final Station station) {
        if (!stations.contains(station)) {
            throw new IllegalArgumentException("제거하려는 역이 노선 내에 존재하지 않습니다.");
        }

        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서는 역을 제거할 수 없습니다.");
        }
    }

    private boolean containsNeitherStationsIn(final Section section) {
        return !(stations.contains(section.getUpStation()) || stations.contains(section.getDownStation()));
    }

    private boolean containsBothStationsIn(final Section section) {
        return stations.containsAll(Set.of(section.getUpStation(), section.getDownStation()));
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Set<Station> getStations() {
        return Collections.unmodifiableSet(stations);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

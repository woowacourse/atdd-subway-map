package wooteco.subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
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

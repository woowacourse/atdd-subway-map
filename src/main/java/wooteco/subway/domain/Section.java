package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Section {

    private Long id;
    private final Station up;
    private final Station down;
    private final int distance;

    public Section(Station up, Station down, int distance) {
        validateStationsNotEqual(up, down);
        validateDistanceIsNatural(distance);
        this.up = up;
        this.down = down;
        this.distance = distance;
    }

    public Section(Long id, Station up, Station down, int distance) {
        this(up, down, distance);
        this.id = id;
    }

    public boolean isSameUpStation(Section section) {
        return this.up.equals(section.up);
    }

    public boolean isSameDownStation(Section section) {
        return this.down.equals(section.down);
    }

    public boolean canUpExtendBy(Section section) {
        return this.up.equals(section.down);
    }

    public boolean canDownExtendBy(Section section) {
        return this.down.equals(section.up);
    }

    public boolean isAlreadyIn(List<Station> stations) {
        return stations.containsAll(List.of(up, down));
    }

    public List<Section> divideBy(Section section) {
        if (this.up.equals(section.up)) {
            return List.of(section, new Section(section.down, this.down, this.distance - section.distance));
        }
        if (this.down.equals(section.down)) {
            return List.of(new Section(this.up, section.up, this.distance - section.distance), section);
        }
        throw new IllegalArgumentException("겹치는 역이 없어 나눌 수 없습니다");
    }

    public Section combine(Section section) {
        if (!this.down.equals(section.up)) {
            throw new IllegalArgumentException("합칠 수 없는 구간입니다.");
        }
        return new Section(this.up, section.down, this.distance + section.distance);
    }

    private void validateStationsNotEqual(Station up, Station down) {
        if (up.equals(down)) {
            throw new IllegalArgumentException("구간은 서로 다른 두 역으로 만들어야 합니다.");
        }
    }

    private void validateDistanceIsNatural(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리는 1 이하가 될 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Station getUp() {
        return up;
    }

    public Station getDown() {
        return down;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(up, section.up) && Objects.equals(down,
                section.down);
    }

    @Override
    public int hashCode() {
        return Objects.hash(up, down, distance);
    }
}

package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Section {

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

    public Section divideBy(Section section) {
        if (this.up.equals(section.up)) {
            return new Section(section.down, this.down, this.distance - section.distance);
        }
        if (this.down.equals(section.down)) {
            return new Section(this.up, section.up, this.distance - section.distance);
        }
        throw new IllegalArgumentException("겹치는 역이 없어 나눌 수 없습니다");
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

    public boolean isAlreadyIn(Set<Station> stations) {
        return stations.containsAll(List.of(up, down));
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

    public Station getUp() {
        return up;
    }

    public Station getDown() {
        return down;
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
